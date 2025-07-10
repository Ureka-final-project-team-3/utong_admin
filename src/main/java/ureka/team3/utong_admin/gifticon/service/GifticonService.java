package ureka.team3.utong_admin.gifticon.service;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.gifticon.dto.request.GifticonRequestDto;
import ureka.team3.utong_admin.gifticon.dto.response.GifticonResponseDto;

import java.util.List;

public interface GifticonService {

    ApiResponse<GifticonResponseDto> createGifticon(GifticonRequestDto gifticonRequestDto);

    ApiResponse<List<GifticonResponseDto>> listGifticon(int pageNumber, int pageSize);

    ApiResponse<GifticonResponseDto> detailGifticon(String id);

    ApiResponse<GifticonResponseDto> updateGifticon(String id, GifticonRequestDto gifticonRequestDto);

    ApiResponse<Void> deleteGifticon(String id);

}
