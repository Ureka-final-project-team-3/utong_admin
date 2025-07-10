package ureka.team3.utong_admin.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "account")
@Getter
@ToString(exclude = {"user"})
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "mileage")
    private Long mileage;

    @OneToOne(mappedBy = "account")
    private User user;

    public List<Line> getLines() {
        return user != null ? user.getLines() : null;
    }
}