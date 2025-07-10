package ureka.team3.utong_admin.gifticon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.business.FileProcessingException;
import ureka.team3.utong_admin.gifticon.dto.response.GifticonResponseDto;
import ureka.team3.utong_admin.gifticon.service.GifticonService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GifticonController.class)
@WithMockUser
class GifticonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GifticonService gifticonService;

    private GifticonResponseDto sampleGifticonDto;
    private List<GifticonResponseDto> sampleGifticonList;

    @BeforeEach
    void setUp() {
        sampleGifticonDto = GifticonResponseDto.builder()
                .id("test-id-123")
                .name("스타벅스 아메리카노")
                .description("시원한 아메리카노")
                .price(4500L)
                .imageUrl("https://bucket.s3.amazonaws.com/gifticons/test-image.jpg")
                .build();

        sampleGifticonList = Arrays.asList(sampleGifticonDto);
    }

    @Test
    @DisplayName("기프티콘 목록 조회 성공")
    void listGifticon_Success() throws Exception {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        ApiResponse<List<GifticonResponseDto>> apiResponse = ApiResponse.success(sampleGifticonList);

        given(gifticonService.listGifticon(pageNumber, pageSize)).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/gifticons")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("test-id-123"))
                .andExpect(jsonPath("$.data[0].name").value("스타벅스 아메리카노"))
                .andExpect(jsonPath("$.data[0].price").value(4500));
    }

    @Test
    @DisplayName("기프티콘 목록 조회 성공 - 기본 파라미터")
    void listGifticon_DefaultParameters_Success() throws Exception {
        // Given
        ApiResponse<List<GifticonResponseDto>> apiResponse = ApiResponse.success(sampleGifticonList);
        given(gifticonService.listGifticon(anyInt(), anyInt())).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/gifticons")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200));
    }

    @Test
    @DisplayName("기프티콘 목록 조회 실패 - 서비스 예외")
    void listGifticon_ServiceException_InternalServerError() throws Exception {
        // Given
        given(gifticonService.listGifticon(anyInt(), anyInt()))
                .willThrow(new FileProcessingException("서비스 오류"));

        // When & Then
        mockMvc.perform(get("/api/admin/gifticons")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기프티콘 상세 조회 성공")
    void detailGifticon_Success() throws Exception {
        // Given
        String gifticonId = "test-id-123";
        ApiResponse<GifticonResponseDto> apiResponse = ApiResponse.success(sampleGifticonDto);

        given(gifticonService.detailGifticon(gifticonId)).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/gifticons/{id}", gifticonId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.data.id").value(gifticonId))
                .andExpect(jsonPath("$.data.name").value("스타벅스 아메리카노"))
                .andExpect(jsonPath("$.data.price").value(4500));
    }

    @Test
    @DisplayName("기프티콘 상세 조회 실패 - 존재하지 않는 ID")
    void detailGifticon_NotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        given(gifticonService.detailGifticon(nonExistentId))
                .willThrow(new FileProcessingException("기프티콘을 찾을 수 없습니다"));

        // When & Then
        mockMvc.perform(get("/api/admin/gifticons/{id}", nonExistentId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기프티콘 생성 성공 - 이미지 포함")
    void createGifticon_WithImage_Success() throws Exception {
        // Given
        MockMultipartFile image = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );

        ApiResponse<GifticonResponseDto> apiResponse = ApiResponse.success(sampleGifticonDto);
        given(gifticonService.createGifticon(any())).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons")
                        .file(image)
                        .param("name", "스타벅스 아메리카노")
                        .param("description", "시원한 아메리카노")
                        .param("price", "4500")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.data.id").value("test-id-123"))
                .andExpect(jsonPath("$.data.name").value("스타벅스 아메리카노"));
    }

    @Test
    @DisplayName("기프티콘 생성 성공 - 이미지 없음")
    void createGifticon_WithoutImage_Success() throws Exception {
        // Given
        ApiResponse<GifticonResponseDto> apiResponse = ApiResponse.success(sampleGifticonDto);
        given(gifticonService.createGifticon(any())).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons")
                        .param("name", "스타벅스 아메리카노")
                        .param("description", "시원한 아메리카노")
                        .param("price", "4500")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200));
    }

    @Test
    @DisplayName("기프티콘 생성 실패 - 서비스 예외")
    void createGifticon_ServiceException_InternalServerError() throws Exception {
        // Given
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "content".getBytes()
        );

        given(gifticonService.createGifticon(any()))
                .willThrow(new FileProcessingException("파일 처리 오류"));

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons")
                        .file(image)
                        .param("name", "테스트")
                        .param("price", "1000")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기프티콘 수정 성공 - 새 이미지 포함")
    void updateGifticon_WithNewImage_Success() throws Exception {
        // Given
        String gifticonId = "test-id-123";
        MockMultipartFile newImage = new MockMultipartFile(
                "image", "new-image.jpg", "image/jpeg", "new image content".getBytes()
        );

        GifticonResponseDto updatedDto = GifticonResponseDto.builder()
                .id(gifticonId)
                .name("수정된 아메리카노")
                .description("수정된 설명")
                .price(5000L)
                .imageUrl("https://bucket.s3.amazonaws.com/gifticons/new-image.jpg")
                .build();

        ApiResponse<GifticonResponseDto> apiResponse = ApiResponse.success(updatedDto);
        given(gifticonService.updateGifticon(eq(gifticonId), any())).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons/{id}", gifticonId)
                        .file(newImage)
                        .param("name", "수정된 아메리카노")
                        .param("description", "수정된 설명")
                        .param("price", "5000")
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.data.name").value("수정된 아메리카노"))
                .andExpect(jsonPath("$.data.price").value(5000));
    }

    @Test
    @DisplayName("기프티콘 수정 성공 - 이미지 변경 없음")
    void updateGifticon_WithoutNewImage_Success() throws Exception {
        // Given
        String gifticonId = "test-id-123";
        ApiResponse<GifticonResponseDto> apiResponse = ApiResponse.success(sampleGifticonDto);
        given(gifticonService.updateGifticon(eq(gifticonId), any())).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons/{id}", gifticonId)
                        .param("name", "수정된 이름")
                        .param("description", "수정된 설명")
                        .param("price", "6000")
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200));
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - 존재하지 않는 ID")
    void updateGifticon_NotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        given(gifticonService.updateGifticon(eq(nonExistentId), any()))
                .willThrow(new FileProcessingException("기프티콘을 찾을 수 없습니다"));

        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons/{id}", nonExistentId)
                        .param("name", "수정 시도")
                        .param("price", "1000")
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - 잘못된 파라미터")
    void updateGifticon_InvalidParameters_BadRequest() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons/{id}", "test-id")
                        .param("price", "invalid-price")  // 잘못된 price 형식
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기프티콘 삭제 성공")
    void deleteGifticon_Success() throws Exception {
        // Given
        String gifticonId = "test-id-123";
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        given(gifticonService.deleteGifticon(gifticonId)).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(delete("/api/admin/gifticons/{id}", gifticonId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("기프티콘 삭제 실패 - 존재하지 않는 ID")
    void deleteGifticon_NotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        given(gifticonService.deleteGifticon(nonExistentId))
                .willThrow(new FileProcessingException("기프티콘을 찾을 수 없습니다"));

        // When & Then
        mockMvc.perform(delete("/api/admin/gifticons/{id}", nonExistentId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("기프티콘 삭제 실패 - 서비스 예외")
    void deleteGifticon_ServiceException_InternalServerError() throws Exception {
        // Given
        String gifticonId = "test-id-123";
        given(gifticonService.deleteGifticon(gifticonId))
                .willThrow(new RuntimeException("예상치 못한 오류"));

        // When & Then
        mockMvc.perform(delete("/api/admin/gifticons/{id}", gifticonId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("인증 없이 접근 - Unauthorized")
    void accessWithoutAuth_Unauthorized() throws Exception {
        // Given - @WithMockUser 제거된 상태에서 테스트하려면 별도 테스트 클래스 필요
        // 현재는 @WithMockUser가 클래스 레벨에 있어서 이 테스트는 스킵하거나
        // 별도 테스트 클래스로 분리해야 함
    }

    @Test
    @DisplayName("CSRF 토큰 없이 POST 요청 - Forbidden")
    void postWithoutCSRF_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/admin/gifticons")
                        .param("name", "테스트")
                        .param("price", "1000"))
                // .with(csrf()) 없이 요청
                .andExpect(status().isForbidden());
    }

}