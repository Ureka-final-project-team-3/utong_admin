package ureka.team3.utong_admin.coupon.service;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.coupon.dto.CouponDto;

import java.util.List;

public interface CouponService {

    ApiResponse<List<CouponDto>> listCoupons(int pageNumber, int pageSize);

    ApiResponse<CouponDto> detailCoupon(String couponId);

    ApiResponse<Void> createCoupon(CouponDto couponDto);

    ApiResponse<Void> updateCoupon(String couponId, CouponDto couponDto);


    ApiResponse<Void> deleteCoupon(String couponId);

    ApiResponse<Long> countCoupons();
}