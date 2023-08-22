package com.neoflex.deal.entity;

import com.neoflex.deal.entity.enums.CreditStatus;
import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "credit", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@TypeDef(name = "json", typeClass = JsonType.class)
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer term;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(nullable = false)
    private BigDecimal rate;

    @Column(nullable = false)
    private BigDecimal psk;

    @Type(type = "json")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Column(name = "payment_schedule", nullable = false, columnDefinition = "jsonb")
    private List<PaymentScheduleElement> paymentSchedule;

    @Column(name = "insurance_enable", nullable = false)
    private Boolean insuranceEnable;

    @Column(name = "salary_client", nullable = false)
    private Boolean salaryClient;

    @Column(name = "credit_status", nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus = CreditStatus.CALCULATED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credit)) return false;
        return id != null && id.equals(((Credit) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
