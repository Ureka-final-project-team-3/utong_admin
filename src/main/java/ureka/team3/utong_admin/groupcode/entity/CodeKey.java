package ureka.team3.utong_admin.groupcode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CodeKey implements Serializable {

    @Column(name = "group_code", columnDefinition = "CHAR(3)")
    private String groupCode;

    @Column(name = "code", columnDefinition = "CHAR(3)")
    private String code;

}
