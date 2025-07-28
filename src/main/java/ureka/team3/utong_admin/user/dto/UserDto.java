package ureka.team3.utong_admin.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.user.entity.User;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserDto {

    private String id;
    private String accountId;
    private String name;
    private LocalDate birthDate;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .accountId(user.getAccountId())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .build();
    }
}