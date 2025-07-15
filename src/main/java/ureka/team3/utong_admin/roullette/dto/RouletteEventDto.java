package ureka.team3.utong_admin.roullette.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ureka.team3.utong_admin.roullette.entity.RouletteEvent;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RouletteEventDto {

    @Setter
    private String id;

    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer maxWinners;

    private Integer currentWinners;

    private BigDecimal winProbability;

    private Boolean isActive;

    private LocalDateTime createdAt;

    public static RouletteEventDto from(RouletteEvent event) {
        return RouletteEventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .maxWinners(event.getMaxWinners())
                .currentWinners(event.getCurrentWinners())
                .winProbability(event.getWinProbability())
                .isActive(event.getIsActive())
                .createdAt(event.getCreatedAt())
                .build();
    }
}