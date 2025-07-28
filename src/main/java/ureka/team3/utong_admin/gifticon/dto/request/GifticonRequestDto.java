package ureka.team3.utong_admin.gifticon.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GifticonRequestDto {

    private String name;

    private String description;

    private Long price;

    private MultipartFile image;

    private String category;
}
