package ureka.team3.utong_admin.price.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.price.dto.PriceDto;
import ureka.team3.utong_admin.price.entity.Price;
import ureka.team3.utong_admin.price.repository.PriceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public ApiResponse<Void> updatePrice(String id, PriceDto priceDto) {
        try {
            priceRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRICE_NOT_FOUND, "해당 정보가 존재하지 않습니다."));

            priceDto.setId(id);
            Price price = Price.of(priceDto);

            priceRepository.save(price);

            return ApiResponse.success(null);

        } catch (Exception e) {
            log.info("수정 중 오류가 발생하였습니다. {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<PriceDto> getPrice(String id) {
        try {
            Price price = priceRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRICE_NOT_FOUND, "해당 정보가 존재하지 않습니다."));

            return ApiResponse.success(PriceDto.from(price));
        } catch (Exception e) {
            log.info("조회 중 오류가 발생하였습니다. {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
