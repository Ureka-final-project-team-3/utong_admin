package ureka.team3.utong_admin.gifticon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "gifticon")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gifticon {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    @Setter
    private String id;

    @Column(nullable = false)
    private Long price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_key", length = 500)
    private String imageKey;
    @Column(name = "category", columnDefinition = "CHAR(3)")
    private String category;
    public static Gifticon of(String name, Long price, String description, String imageUrl, String imageKey, String category) {
        Gifticon gifticon = new Gifticon();

        gifticon.name = name;
        gifticon.price = price;
        gifticon.description = description;
        gifticon.imageUrl = imageUrl;
        gifticon.imageKey = imageKey;
        gifticon.category = category;
        return gifticon;
    }

}