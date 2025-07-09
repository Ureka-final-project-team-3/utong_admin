package ureka.team3.utong_admin.groupcode.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

}
