package ureka.team3.utong_admin.coupon.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ureka.team3.utong_admin.coupon.entity.Coupon;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponDto {

    @Setter
    private String id;

    private String gifticonId;


    private LocalDateTime createdAt;


    private String couponCode;

    private String gifticonName;
    private Long gifticonPrice;
    private String gifticonImageUrl;

    public static CouponDto from(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId())
                .gifticonId(coupon.getGifticon() != null ? coupon.getGifticon().getId() : null)
                .createdAt(coupon.getCreatedAt())
                .couponCode(coupon.getCouponCode())
                .gifticonName(coupon.getGifticon() != null ? coupon.getGifticon().getName() : null)
                .gifticonPrice(coupon.getGifticon() != null ? coupon.getGifticon().getPrice() : null)
                .gifticonImageUrl(coupon.getGifticon() != null ? coupon.getGifticon().getImageUrl() : null)
                .build();
    }
}