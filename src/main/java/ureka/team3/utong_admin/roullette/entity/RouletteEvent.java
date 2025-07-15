package ureka.team3.utong_admin.roullette.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.roullette.dto.RouletteEventDto;

@Entity
@Table(name = "roulette_event")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RouletteEvent {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "max_winners", nullable = false)
    private Integer maxWinners;

    @Column(name = "current_winners", nullable = false)
    private Integer currentWinners;

    @Column(name = "win_probability", precision = 5, scale = 4, nullable = false)
    private BigDecimal winProbability;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static RouletteEvent of(RouletteEventDto dto) {
        return RouletteEvent.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .maxWinners(dto.getMaxWinners())
                .currentWinners(dto.getCurrentWinners() != null ? dto.getCurrentWinners() : 0)
                .winProbability(dto.getWinProbability())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    public void updateStatus(Boolean isActive) {
        this.isActive = isActive;
    }

    public void updateEvent(RouletteEventDto dto) {
        this.title = dto.getTitle();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.maxWinners = dto.getMaxWinners();
        this.winProbability = dto.getWinProbability();
        this.isActive = dto.getIsActive();
    }
}