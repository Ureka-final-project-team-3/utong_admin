package ureka.team3.utong_admin.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "line")
@Getter
@ToString(exclude = {"user", "plan"})
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Line {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "plan_id", length = 36)
    private String planId;

    @Column(name = "country_code")
    private Integer countryCode;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private Plan plan;
}