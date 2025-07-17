package ureka.team3.utong_admin.coupon.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ureka.team3.utong_admin.coupon.dto.CouponDto;
import ureka.team3.utong_admin.gifticon.entity.Gifticon;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gifticon_id")
    private Gifticon gifticon;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "coupon_code", columnDefinition = "CHAR(3)")
    private String couponCode;

    public static Coupon of(CouponDto dto, Gifticon gifticon) {
        return Coupon.builder()
                .id(dto.getId())
                .gifticon(gifticon)
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .couponCode(dto.getCouponCode())
                .build();
    }

    

    public void updateCoupon(CouponDto dto, Gifticon gifticon) {
        this.gifticon = gifticon;
        this.couponCode = dto.getCouponCode();
    }
}