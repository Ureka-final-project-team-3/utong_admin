package ureka.team3.utong_admin.gifticon.dto.response;

import lombok.*;
import ureka.team3.utong_admin.gifticon.entity.Gifticon;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GifticonResponseDto {

    private String id;

    private String name;

    private String description;

    private Long price;

    private String imageUrl;

    public static GifticonResponseDto from(Gifticon gifticon) {
        GifticonResponseDto responseDto = new GifticonResponseDto();

        responseDto.id = gifticon.getId();
        responseDto.name = gifticon.getName();
        responseDto.description = gifticon.getDescription();
        responseDto.price = gifticon.getPrice();
        responseDto.imageUrl = gifticon.getImageUrl();

        return responseDto;
    }
}
