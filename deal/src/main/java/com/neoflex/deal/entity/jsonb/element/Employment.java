package com.neoflex.deal.entity.jsonb.element;

import com.neoflex.deal.entity.enums.EmploymentPosition;
import com.neoflex.deal.entity.enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "employment", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    @Column(name = "employer_inn")
    private String employerINN;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal salary = new BigDecimal("0");

    @Enumerated(EnumType.STRING)
    private EmploymentPosition position;

    @Builder.Default
    @Column(name = "work_experience_total", nullable = false)
    private Integer workExperienceTotal = 0;

    @Builder.Default
    @Column(name = "work_experience_current", nullable = false)
    private Integer workExperienceCurrent = 0;
}
