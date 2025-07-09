package ureka.team3.utong_admin.code.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CodeKey implements Serializable {

    @Column(name = "group_code", columnDefinition = "CHAR(3)")
    private String groupCode;

    @Column(name = "code", columnDefinition = "CHAR(3)")
    private String code;

}
