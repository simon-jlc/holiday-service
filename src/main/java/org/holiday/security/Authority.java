package org.holiday.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "th_authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data(staticConstructor = "of")
@NoArgsConstructor
public class Authority {

    /**
     * Defines authorities constants
     */
    public static String DEVELOPER = "DEVELOPER";
    public static String CONSULTANT = "CONSULTANT";

    @NotNull
    @Size(max = 50)
    @Id
    @Column(length = 50)
    private String name;
}
