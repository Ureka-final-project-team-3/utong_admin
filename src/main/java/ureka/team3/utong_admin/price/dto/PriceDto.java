package ureka.team3.utong_admin.price.dto;

import lombok.*;
import ureka.team3.utong_admin.price.entity.Price;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PriceDto {

    @Setter
    private String id;

    private Long minimumPrice;

    private Float minimumRate;

    private Float tax;

    public static PriceDto from(Price price) {
        PriceDto priceDto = new PriceDto();

        priceDto.id = price.getId();
        priceDto.minimumPrice = price.getMinimumPrice();
        priceDto.minimumRate = price.getMinimumRate();
        priceDto.tax = price.getTax();

        return priceDto;
    }
}
