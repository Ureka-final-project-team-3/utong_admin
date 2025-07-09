package ureka.team3.utong_admin.groupcode.dto;

import lombok.*;
import ureka.team3.utong_admin.groupcode.entity.GroupCode;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GroupCodeDto {

    @Setter
    private String groupCode;

    private String groupCodeName;

    private String groupCodeDesc;

    public static GroupCodeDto from(GroupCode groupCode) {
        GroupCodeDto groupCodeDto = new GroupCodeDto();

        groupCodeDto.groupCode = groupCode.getGroupCode();
        groupCodeDto.groupCodeName = groupCode.getGroupCodeName();
        groupCodeDto.groupCodeDesc = groupCode.getGroupCodeDesc();

        return groupCodeDto;
    }
}
