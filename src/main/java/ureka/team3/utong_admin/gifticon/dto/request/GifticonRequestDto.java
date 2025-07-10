package ureka.team3.utong_admin.gifticon.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GifticonRequestDto {

    private String name;

    private String description;

    private Long price;

    private MultipartFile image;

}
