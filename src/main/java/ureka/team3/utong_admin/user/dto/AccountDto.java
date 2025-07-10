package ureka.team3.utong_admin.user.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.user.entity.Account;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountDto {

    private String id;
    private String nickname;
    private String email;
    private String provider;
    private Long mileage;
    private UserDto user;
    private List<LineDto> lines;

    public static AccountDto from(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .nickname(account.getNickname())
                .email(account.getEmail())
                .provider(account.getProvider())
                .mileage(account.getMileage())
                .user(account.getUser() != null ? UserDto.from(account.getUser()) : null)
                .lines(account.getLines() != null ? 
                       account.getLines().stream().map(LineDto::from).toList() : null)
                .build();
    }
}