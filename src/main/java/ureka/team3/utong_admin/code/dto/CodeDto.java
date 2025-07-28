package ureka.team3.utong_admin.code.dto;

import lombok.*;
import ureka.team3.utong_admin.code.entity.Code;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CodeDto {

    @Setter
    private String groupCode;

    @Setter
    private String code;

    private String codeName;

    private String codeNameBrief;

    private int orderNo;

    public static CodeDto from(Code code) {
        CodeDto codeDto = new CodeDto();

        codeDto.groupCode = code.getCodeKey().getGroupCode();
        codeDto.code = code.getCodeKey().getCode();
        codeDto.codeName = code.getCodeName();
        codeDto.codeNameBrief = code.getCodeNameBrief();
        codeDto.orderNo = code.getOrderNo();

        return codeDto;
    }

}
