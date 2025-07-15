package ureka.team3.utong_admin.coupon.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.CouponNotFoundException;
import ureka.team3.utong_admin.coupon.dto.CouponDto;
import ureka.team3.utong_admin.coupon.entity.Coupon;
import ureka.team3.utong_admin.coupon.repository.CouponRepository;
import ureka.team3.utong_admin.gifticon.entity.Gifticon;
import ureka.team3.utong_admin.gifticon.repository.GifticonRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final GifticonRepository gifticonRepository;

    @Override
    public ApiResponse<List<CouponDto>> listCoupons(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Coupon> page = couponRepository.findAllWithGifticon(pageable);
            
            List<CouponDto> couponDtoList = page.getContent()
                    .stream()
                    .map(CouponDto::from)
                    .toList();

            return ApiResponse.success(couponDtoList);
        } catch (Exception e) {
            log.error("서버에서 쿠폰 목록을 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<CouponDto> detailCoupon(String couponId) {
        try {
            Coupon coupon = couponRepository.findByIdWithGifticon(couponId)
                    .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));

            return ApiResponse.success(CouponDto.from(coupon));
        } catch (CouponNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 쿠폰 상세 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> createCoupon(CouponDto couponDto) {
        try {
            Gifticon gifticon = gifticonRepository.findById(couponDto.getGifticonId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "존재하지 않는 기프티콘입니다."));
            couponDto.setId(UUID.randomUUID().toString());
            Coupon coupon = Coupon.of(couponDto, gifticon);
            couponRepository.save(coupon);

            log.info("쿠폰 생성 완료: {}", coupon.getId());
            return ApiResponse.success(null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 쿠폰을 생성하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateCoupon(String couponId, CouponDto couponDto) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));

            Gifticon gifticon = gifticonRepository.findById(couponDto.getGifticonId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "존재하지 않는 기프티콘입니다."));

            

            coupon.updateCoupon(couponDto, gifticon);
            couponRepository.save(coupon);

            log.info("쿠폰 수정 완료: {}", couponId);
            return ApiResponse.success(null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 쿠폰을 수정하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    

    @Override
    @Transactional
    public ApiResponse<Void> deleteCoupon(String couponId) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));

            couponRepository.delete(coupon);

            log.info("쿠폰 삭제 완료: {}", couponId);
            return ApiResponse.success(null);
        } catch (CouponNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 쿠폰을 삭제하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Long> countCoupons() {
        try {
            long count = couponRepository.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("서버에서 쿠폰 개수를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}