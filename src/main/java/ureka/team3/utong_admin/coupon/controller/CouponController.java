package ureka.team3.utong_admin.coupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.coupon.dto.CouponDto;
import ureka.team3.utong_admin.coupon.service.CouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDto>>> listCoupons(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        log.info("GET /api/admin/coupons called with page: {}, size: {}", pageNumber, pageSize);
        return ResponseEntity.ok(couponService.listCoupons(pageNumber, pageSize));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<CouponDto>> detailCoupon(
            @PathVariable(name = "couponId") String couponId) {
        log.info("GET /api/admin/coupons/{} called", couponId);
        return ResponseEntity.ok(couponService.detailCoupon(couponId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCoupon(
            @Valid @RequestBody CouponDto couponDto) {
        log.info("POST /api/admin/coupons called with gifticon: {}", couponDto.getGifticonId());
        return ResponseEntity.ok(couponService.createCoupon(couponDto));
    }

    @PutMapping("/{couponId}")
    public ResponseEntity<ApiResponse<Void>> updateCoupon(
            @PathVariable(name = "couponId") String couponId,
            @Valid @RequestBody CouponDto couponDto) {
        log.info("PUT /api/admin/coupons/{} called", couponId);
        return ResponseEntity.ok(couponService.updateCoupon(couponId, couponDto));
    }

    
    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @PathVariable(name = "couponId") String couponId) {
        log.info("DELETE /api/admin/coupons/{} called", couponId);
        return ResponseEntity.ok(couponService.deleteCoupon(couponId));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countCoupons() {
        log.info("GET /api/admin/coupons/count called");
        return ResponseEntity.ok(couponService.countCoupons());
    }
}