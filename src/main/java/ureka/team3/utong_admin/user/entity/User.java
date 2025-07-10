package ureka.team3.utong_admin.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@ToString(exclude = {"account", "lines"})
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "account_id", length = 36)
    private String accountId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @OneToMany(mappedBy = "user")
    private List<Line> lines;
}