package ureka.team3.utong_admin.code.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.code.dto.CodeDto;

@Entity
@Table(name = "code")
@Getter
@ToString
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Code {

    @EmbeddedId
    private CodeKey codeKey;

    private String codeName;

    private String codeNameBrief;

    private Integer orderNo;

    public static Code of(CodeDto codeDto) {
        Code code = new Code();

        CodeKey codeKey = new CodeKey(codeDto.getGroupCode(), codeDto.getCode());

        code.codeKey = codeKey;
        code.codeName = codeDto.getCodeName();
        code.codeNameBrief = codeDto.getCodeNameBrief();
        code.orderNo = codeDto.getOrderNo();

        return code;
    }

}
