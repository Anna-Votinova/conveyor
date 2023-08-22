package com.neoflex.deal.entity;

import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "application", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@TypeDef(name = "json", typeClass = JsonType.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    private Client client;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id")
    @ToString.Exclude
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate;

    @Type(type = "json")
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppliedOffer appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @Type(type = "json")
    @Column(name = "status_history", columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<StatusHistory> statusHistory = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Application)) return false;
        return id != null && id.equals(((Application) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
