package ureka.team3.utong_admin.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.user.entity.Line;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LineDto {

    private String id;
    private String phoneNumber;
    private String userId;
    private String planId;
    private Integer countryCode;
    private PlanDto plan;

    public static LineDto from(Line line) {
        return LineDto.builder()
                .id(line.getId())
                .phoneNumber(line.getPhoneNumber())
                .userId(line.getUserId())
                .planId(line.getPlanId())
                .countryCode(line.getCountryCode())
                .plan(line.getPlan() != null ? PlanDto.from(line.getPlan()) : null)
                .build();
    }
}