package ureka.team3.utong_admin.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.user.entity.Plan;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanDto {

    private String id;
    private String name;
    private Long data;

    public static PlanDto from(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .data(plan.getData())
                .build();
    }
}