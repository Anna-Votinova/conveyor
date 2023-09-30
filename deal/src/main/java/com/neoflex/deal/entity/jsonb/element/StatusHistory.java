package com.neoflex.deal.entity.jsonb.element;

import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusHistory {

    private UUID id;

    private ApplicationStatus status;

    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    private ChangeType changeType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusHistory)) return false;
        return id != null && id.equals(((StatusHistory) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
