package ureka.team3.utong_admin.groupcode.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;

@Entity
@Table(name = "group_code")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCode {

    @Id
    @Column(name = "group_code", columnDefinition = "CHAR(3)")
    private String groupCode;

    private String groupCodeName;

    private String groupCodeDesc;

    // 팩토리 메소드
    public static GroupCode of(GroupCodeDto groupCodeDto) {
        GroupCode group = new GroupCode();

        group.groupCode = groupCodeDto.getGroupCode();
        group.groupCodeName = groupCodeDto.getGroupCodeName();
        group.groupCodeDesc = groupCodeDto.getGroupCodeDesc();

        return group;
    }

}
