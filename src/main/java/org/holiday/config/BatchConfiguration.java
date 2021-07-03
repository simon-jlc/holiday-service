package org.holiday.config;

import org.holiday.batch.DynamicFilenameDefiner;
import org.holiday.batch.EmployeeDayOffRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    /**
     * Default CSV column delimiter
     */
    private static final String DELIMITER_CHAR = ",";

    @Value("${org.holiday.employees_day_off.output.dir:#{systemProperties['java.io.tmpdir']}}")
    private String outputDir;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public Job fileEmployeeDaysOffJob(
            @Qualifier("employeeDaysOffStep") Step employeeDaysOffStep
    ) {
        return jobs.get("employees_day_off")
                .incrementer(new RunIdIncrementer())
                .flow(employeeDaysOffStep)
                .end()
                .build();
    }

    @Bean
    protected Step employeeDaysOffStep(
            @Qualifier("fetchEmployeeDataModelReader") ItemReader<EmployeeDayOffRecord> reader,
            @Qualifier("noopProcessor") ItemProcessor<EmployeeDayOffRecord, EmployeeDayOffRecord> processor,
            @Qualifier("csvFileWriter") ItemWriter<EmployeeDayOffRecord> writer
    ) {
        return steps.get("employees_day_off_step")
                .<EmployeeDayOffRecord, EmployeeDayOffRecord>chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new DynamicFilenameDefiner())
                .build();
    }

    @Bean
    protected ItemReader<EmployeeDayOffRecord> fetchEmployeeDataModelReader() {
        return new JdbcCursorItemReaderBuilder<EmployeeDayOffRecord>()
                .name("fetchEmployeesWithAggregates")
                .dataSource(dataSource)
                .sql(sql())
                .rowMapper(mapToRecord())
                .queryTimeout((int) TimeUnit.MINUTES.toMillis(1))
                .maxItemCount(10_000)
                .maxRows(10)
                .build();
    }

    @Bean
    protected ItemProcessor<EmployeeDayOffRecord, EmployeeDayOffRecord> noopProcessor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    @StepScope
    protected FlatFileItemWriter<EmployeeDayOffRecord> csvFileWriter(
            @Value("#{stepExecutionContext['output.file.name']}") String outputFilename
    ) {
        var fieldExtractor = new BeanWrapperFieldExtractor<EmployeeDayOffRecord>();
        fieldExtractor.setNames(EmployeeDayOffRecord.RECORD_FIELDS);

        var lineAggregator = new DelimitedLineAggregator<EmployeeDayOffRecord>();
        lineAggregator.setDelimiter(DELIMITER_CHAR);
        lineAggregator.setFieldExtractor(fieldExtractor);

        var outputFilePath = Paths.get(outputDir, outputFilename);
        return new FlatFileItemWriterBuilder<EmployeeDayOffRecord>()
                .name("employeesDaysOffFileWriter")
                .resource(new FileSystemResource(outputFilePath))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.append(String.join(DELIMITER_CHAR, EmployeeDayOffRecord.RECORD_FIELDS)))
                .build();
    }

    private String sql() {
        return "select e.id, e.email, e.first_name, e.last_name, d.day_off, dpy.year, dpy.days_off_count as available_per_year, count(d.day_off) OVER (PARTITION BY e.id, dpy.year) as days_off_taken, b.balance " +
                "from th_employee e " +
                "    left join th_employee_dayoff ed on ed.employee_id = e.id " +
                "    left join th_day_off d on d.id = ed.dayoff_id " +
                "    left join th_emp_day_off_balance b on e.id = b.employee_id " +
                "    left join th_day_off_per_year dpy on dpy.year = b.year " +
                "where b.year = cast ( to_char(d.day_off, 'YYYY') as int8)" +
                "order by e.id, d.day_off";
    }

    private RowMapper<EmployeeDayOffRecord> mapToRecord() {
        return (resultSet, i) -> {
            var id = resultSet.getLong("id");
            var email = resultSet.getString("email");
            var first_name = resultSet.getString("first_name");
            var last_name = resultSet.getString("last_name");
            var day_off = resultSet.getDate("day_off");
            var year = resultSet.getInt("year");
            var available_per_year = resultSet.getInt("available_per_year");
            var days_off_taken = resultSet.getInt("days_off_taken");
            var balance = resultSet.getInt("balance");

            return new EmployeeDayOffRecord(id, email, first_name, last_name, day_off, year, available_per_year, days_off_taken, balance);
        };
    }
}
