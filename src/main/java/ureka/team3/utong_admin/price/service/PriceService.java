package ureka.team3.utong_admin.price.service;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.price.dto.PriceDto;

public interface PriceService {

    public ApiResponse<Void> updatePrice(String id, PriceDto priceDto);

}
