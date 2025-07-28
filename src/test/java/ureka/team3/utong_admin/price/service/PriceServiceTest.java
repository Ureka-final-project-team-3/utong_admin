package ureka.team3.utong_admin.price.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.price.dto.PriceDto;
import ureka.team3.utong_admin.price.entity.Price;
import ureka.team3.utong_admin.price.repository.PriceRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @Test
    void updatePrice_성공_test() {
        // given
        String id = UUID.randomUUID().toString();
        PriceDto priceDto = PriceDto.builder()
                .minimumPrice(5000L)
                .minimumRate(30.0F)
                .tax(2.5F)
                .build();

        when(priceRepository.findById(id)).thenReturn(Optional.of(Price.of(priceDto)));

        // when

        ApiResponse<Void> response = priceService.updatePrice(id, priceDto);

        // then

        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void updatePrice_실패_test() {
        // given
        String id = UUID.randomUUID().toString();
        PriceDto priceDto = PriceDto.builder()
                .minimumPrice(5000L)
                .minimumRate(30.0F)
                .tax(2.5F)
                .build();

        when(priceRepository.findById(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            priceService.updatePrice(id, priceDto);
        });

        // then

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(priceRepository, times(1)).findById(id);
    }

    @Test
    void getPrice_성공_test() {
        // given
        String id = UUID.randomUUID().toString();
        PriceDto priceDto = PriceDto.builder()
                .id(id)
                .minimumPrice(5000L)
                .minimumRate(30.0F)
                .tax(2.5F)
                .build();

        Price price = Price.of(priceDto);

        when(priceRepository.findById(id)).thenReturn(Optional.of(price));

        // when
        ApiResponse<PriceDto> response = priceService.getPrice(id);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData().getMinimumPrice()).isEqualTo(5000L);
    }

    @Test
    void getPrice_실패_test() {
        // given
        String id = UUID.randomUUID().toString();

        when(priceRepository.findById(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            priceService.getPrice(id);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(priceRepository, times(1)).findById(id);
    }
}