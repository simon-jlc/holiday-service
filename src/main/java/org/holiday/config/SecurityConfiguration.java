package org.holiday.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.holiday.security.Authority.DEVELOPER;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        var usersByUsernameQuery = ""
                + "SELECT e.email as username, e.password, 'true' as enabled "
                + "FROM th_employee e "
                + "WHERE e.email = ?";

        var authoritiesByUsernameQuery = ""
                + "SELECT e.email as username, a.name as role "
                + "FROM th_employee e "
                + "    INNER JOIN th_employee_authority ea ON e.id = ea.employee_id "
                + "    INNER JOIN th_authority a ON ea.authority_name = a.name "
                + "WHERE e.email = ?";

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery(usersByUsernameQuery)
                .authoritiesByUsernameQuery(authoritiesByUsernameQuery);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/health").permitAll()
                .antMatchers(HttpMethod.POST, "/api/holiday").hasAuthority(DEVELOPER)
                .antMatchers(HttpMethod.PUT, "/api/holiday").hasAuthority(DEVELOPER)
                .antMatchers(HttpMethod.DELETE, "/api/holiday").hasAuthority(DEVELOPER)
                .anyRequest().authenticated()
                .and()
                .httpBasic()
        ;
        // @formatter:on
    }
}
