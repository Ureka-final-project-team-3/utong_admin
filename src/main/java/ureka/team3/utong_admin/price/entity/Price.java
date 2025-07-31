package ureka.team3.utong_admin.price.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ureka.team3.utong_admin.price.dto.PriceDto;

@Entity
@Table(name = "price")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Price {

    @Id
    private String id;

    private Long minimumPrice;

    private Float minimumRate;

    private Float tax;

    private Float availableTradeRate;

    public static Price of(PriceDto priceDto) {
        Price price = new Price();

        price.id = priceDto.getId();
        price.minimumPrice = priceDto.getMinimumPrice();
        price.minimumRate = priceDto.getMinimumRate();
        price.tax = priceDto.getTax();
        price.availableTradeRate = priceDto.getAvailableTradeRate();

        return price;
    }

}
