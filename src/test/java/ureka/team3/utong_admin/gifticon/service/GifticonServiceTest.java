package ureka.team3.utong_admin.gifticon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.business.FileProcessingException;
import ureka.team3.utong_admin.gifticon.dto.request.GifticonRequestDto;
import ureka.team3.utong_admin.gifticon.dto.response.GifticonResponseDto;
import ureka.team3.utong_admin.gifticon.entity.Gifticon;
import ureka.team3.utong_admin.gifticon.repository.GifticonRepository;
import ureka.team3.utong_admin.s3.service.S3Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GifticonServiceTest {

    @Mock
    private GifticonRepository gifticonRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private GifticonServiceImpl gifticonService;

    private GifticonRequestDto gifticonRequestDto;
    private Gifticon gifticon;
    private MockMultipartFile mockImage;

    @BeforeEach
    void setUp() {
        // Mock 이미지 파일 생성
        mockImage = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );

        // GifticonRequestDto 설정 (AllArgsConstructor 사용)
        gifticonRequestDto = new GifticonRequestDto(
                "스타벅스 아메리카노",
                "시원한 아메리카노",
                4500L,
                mockImage
        );

        // Gifticon 엔티티 설정 (of 메서드 사용)
        gifticon = Gifticon.of(
                "스타벅스 아메리카노",
                4500L,
                "시원한 아메리카노",
                "https://bucket.s3.amazonaws.com/gifticons/test-image.jpg",
                "gifticons/test-image.jpg"
        );
        gifticon.setId("test-id-123");
    }

    @Test
    @DisplayName("기프티콘 생성 성공 - 이미지 포함")
    void createGifticon_WithImage_Success() {
        // Given
        String imageUrl = "https://bucket.s3.amazonaws.com/gifticons/test-image.jpg";
        String imageKey = "gifticons/test-image.jpg";

        given(s3Service.uploadFile(mockImage)).willReturn(imageUrl);
        given(s3Service.extractKeyFromUrl(imageUrl)).willReturn(imageKey);
        given(gifticonRepository.save(any(Gifticon.class))).willReturn(gifticon);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.createGifticon(gifticonRequestDto);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData().getName()).isEqualTo("스타벅스 아메리카노");
        assertThat(result.getData().getPrice()).isEqualTo(4500L);
        assertThat(result.getData().getImageUrl()).isEqualTo(imageUrl);

        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(s3Service, times(1)).extractKeyFromUrl(imageUrl);
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 생성 성공 - 이미지 없음")
    void createGifticon_WithoutImage_Success() {
        // Given - 이미지 없는 RequestDto 생성
        GifticonRequestDto requestWithoutImage = new GifticonRequestDto(
                "스타벅스 아메리카노",
                "시원한 아메리카노",
                4500L,
                null  // 이미지 없음
        );

        Gifticon gifticonWithoutImage = Gifticon.of(
                "스타벅스 아메리카노",
                4500L,
                "시원한 아메리카노",
                null,  // imageUrl 없음
                null   // imageKey 없음
        );
        gifticonWithoutImage.setId("test-id-123");

        given(gifticonRepository.save(any(Gifticon.class))).willReturn(gifticonWithoutImage);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.createGifticon(requestWithoutImage);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData().getName()).isEqualTo("스타벅스 아메리카노");
        assertThat(result.getData().getImageUrl()).isNull();

        verify(s3Service, never()).uploadFile(any());
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 생성 성공 - 빈 이미지")
    void createGifticon_WithEmptyImage_Success() {
        // Given - 빈 이미지 파일
        MockMultipartFile emptyImage = new MockMultipartFile(
                "image", "empty.jpg", "image/jpeg", new byte[0]
        );

        GifticonRequestDto requestWithEmptyImage = new GifticonRequestDto(
                "스타벅스 아메리카노",
                "시원한 아메리카노",
                4500L,
                emptyImage
        );

        Gifticon gifticonWithoutImage = Gifticon.of(
                "스타벅스 아메리카노",
                4500L,
                "시원한 아메리카노",
                null,
                null
        );
        gifticonWithoutImage.setId("test-id-123");

        given(gifticonRepository.save(any(Gifticon.class))).willReturn(gifticonWithoutImage);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.createGifticon(requestWithEmptyImage);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData().getImageUrl()).isNull();

        verify(s3Service, never()).uploadFile(any());  // 빈 파일은 업로드하지 않음
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 생성 실패 - S3 키 추출 실패")
    void createGifticon_S3KeyExtractionFails_ThrowsException() {
        // Given
        String imageUrl = "https://bucket.s3.amazonaws.com/gifticons/test-image.jpg";

        given(s3Service.uploadFile(mockImage)).willReturn(imageUrl);
        given(s3Service.extractKeyFromUrl(imageUrl))
                .willThrow(new IllegalArgumentException("키 추출 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.createGifticon(gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(s3Service, times(1)).extractKeyFromUrl(imageUrl);
        verify(gifticonRepository, never()).save(any());
    }

    @Test
    @DisplayName("기프티콘 생성 실패 - S3 업로드 실패")
    void createGifticon_S3UploadFails_ThrowsException() {
        // Given
        given(s3Service.uploadFile(mockImage)).willThrow(new RuntimeException("S3 업로드 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.createGifticon(gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(gifticonRepository, never()).save(any());
    }

    @Test
    @DisplayName("기프티콘 생성 실패 - DB 저장 실패")
    void createGifticon_DatabaseSaveFails_ThrowsException() {
        // Given
        String imageUrl = "https://bucket.s3.amazonaws.com/gifticons/test-image.jpg";
        String imageKey = "gifticons/test-image.jpg";

        given(s3Service.uploadFile(mockImage)).willReturn(imageUrl);
        given(s3Service.extractKeyFromUrl(imageUrl)).willReturn(imageKey);
        given(gifticonRepository.save(any(Gifticon.class)))
                .willThrow(new RuntimeException("DB 저장 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.createGifticon(gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 목록 조회 성공")
    void listGifticon_Success() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Gifticon> gifticonList = Arrays.asList(gifticon);
        Page<Gifticon> gifticonPage = new PageImpl<>(gifticonList, pageable, 1);

        given(gifticonRepository.findAll(pageable)).willReturn(gifticonPage);

        // When
        ApiResponse<List<GifticonResponseDto>> result = gifticonService.listGifticon(pageNumber, pageSize);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getName()).isEqualTo("스타벅스 아메리카노");
        assertThat(result.getData().get(0).getId()).isEqualTo("test-id-123");

        verify(gifticonRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("기프티콘 목록 조회 성공 - 빈 목록")
    void listGifticon_EmptyList_Success() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Gifticon> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        given(gifticonRepository.findAll(pageable)).willReturn(emptyPage);

        // When
        ApiResponse<List<GifticonResponseDto>> result = gifticonService.listGifticon(pageNumber, pageSize);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData()).isEmpty();

        verify(gifticonRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("기프티콘 목록 조회 실패 - DB 오류")
    void listGifticon_DatabaseError_ThrowsException() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        given(gifticonRepository.findAll(pageable)).willThrow(new RuntimeException("DB 연결 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.listGifticon(pageNumber, pageSize))
                .isInstanceOf(FileProcessingException.class);
    }

    @Test
    @DisplayName("기프티콘 상세 조회 성공")
    void detailGifticon_Success() {
        // Given
        String gifticonId = "test-id-123";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.detailGifticon(gifticonId);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData().getId()).isEqualTo(gifticonId);
        assertThat(result.getData().getName()).isEqualTo("스타벅스 아메리카노");
        assertThat(result.getData().getPrice()).isEqualTo(4500L);

        verify(gifticonRepository, times(1)).findById(gifticonId);
    }

    @Test
    @DisplayName("기프티콘 상세 조회 실패 - 존재하지 않는 ID")
    void detailGifticon_NotFound_ThrowsException() {
        // Given
        String gifticonId = "non-existent-id";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gifticonService.detailGifticon(gifticonId))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
    }

    @Test
    @DisplayName("기프티콘 수정 성공 - 새 이미지 포함")
    void updateGifticon_WithNewImage_Success() {
        // Given
        String gifticonId = "test-id-123";
        String oldImageKey = "gifticons/old-image.jpg";
        String newImageUrl = "https://bucket.s3.amazonaws.com/gifticons/new-image.jpg";
        String newImageKey = "gifticons/new-image.jpg";

        Gifticon existingGifticon = Gifticon.of(
                "기존 이름",
                3000L,
                "기존 설명",
                "https://bucket.s3.amazonaws.com/gifticons/old-image.jpg",
                oldImageKey
        );
        existingGifticon.setId(gifticonId);

        GifticonRequestDto updateRequest = new GifticonRequestDto(
                "수정된 이름",
                "수정된 설명",
                5000L,
                mockImage
        );

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(existingGifticon));
        given(s3Service.uploadFile(mockImage)).willReturn(newImageUrl);
        given(s3Service.extractKeyFromUrl(newImageUrl)).willReturn(newImageKey);
        given(gifticonRepository.save(any(Gifticon.class))).willReturn(existingGifticon);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.updateGifticon(gifticonId, updateRequest);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, times(1)).deleteFile(oldImageKey);  // 기존 이미지 삭제
        verify(s3Service, times(1)).uploadFile(mockImage);    // 새 이미지 업로드
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 수정 성공 - 이미지 변경 없음")
    void updateGifticon_WithoutNewImage_Success() {
        // Given
        String gifticonId = "test-id-123";

        GifticonRequestDto updateRequestWithoutImage = new GifticonRequestDto(
                "수정된 이름",
                "수정된 설명",
                5000L,
                null  // 새 이미지 없음
        );

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        given(gifticonRepository.save(any(Gifticon.class))).willReturn(gifticon);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.updateGifticon(gifticonId, updateRequestWithoutImage);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, never()).deleteFile(any());  // 이미지 삭제 안함
        verify(s3Service, never()).uploadFile(any());  // 이미지 업로드 안함
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 수정 성공 - 기존 이미지 없고 새 이미지 추가")
    void updateGifticon_NoOldImageButAddNewImage_Success() {
        // Given
        String gifticonId = "test-id-123";
        String newImageUrl = "https://bucket.s3.amazonaws.com/gifticons/new-image.jpg";
        String newImageKey = "gifticons/new-image.jpg";

        Gifticon existingGifticonWithoutImage = Gifticon.of(
                "기존 이름",
                3000L,
                "기존 설명",
                null,  // 기존 이미지 없음
                null
        );
        existingGifticonWithoutImage.setId(gifticonId);

        GifticonRequestDto updateRequest = new GifticonRequestDto(
                "수정된 이름",
                "수정된 설명",
                5000L,
                mockImage
        );

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(existingGifticonWithoutImage));
        given(s3Service.uploadFile(mockImage)).willReturn(newImageUrl);
        given(s3Service.extractKeyFromUrl(newImageUrl)).willReturn(newImageKey);
        given(gifticonRepository.save(any(Gifticon.class))).willReturn(existingGifticonWithoutImage);

        // When
        ApiResponse<GifticonResponseDto> result = gifticonService.updateGifticon(gifticonId, updateRequest);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, never()).deleteFile(any());  // 기존 이미지가 없으므로 삭제 안함
        verify(s3Service, times(1)).uploadFile(mockImage);  // 새 이미지 업로드
        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - 새 이미지 업로드 실패")
    void updateGifticon_NewImageUploadFails_ThrowsException() {
        // Given
        String gifticonId = "test-id-123";
        String oldImageKey = "gifticons/old-image.jpg";

        Gifticon existingGifticon = Gifticon.of(
                "기존 이름",
                3000L,
                "기존 설명",
                "https://bucket.s3.amazonaws.com/gifticons/old-image.jpg",
                oldImageKey
        );
        existingGifticon.setId(gifticonId);

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(existingGifticon));
        given(s3Service.uploadFile(mockImage)).willThrow(new RuntimeException("S3 업로드 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.updateGifticon(gifticonId, gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, times(1)).deleteFile(oldImageKey);  // 기존 이미지는 삭제됨
        verify(s3Service, times(1)).uploadFile(mockImage);    // 새 이미지 업로드 시도
        verify(gifticonRepository, never()).save(any());      // 저장은 안됨
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - 존재하지 않는 ID")
    void updateGifticon_NotFound_ThrowsException() {
        // Given
        String gifticonId = "non-existent-id";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gifticonService.updateGifticon(gifticonId, gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, never()).uploadFile(any());
        verify(gifticonRepository, never()).save(any());
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - 새 이미지 키 추출 실패")
    void updateGifticon_NewImageKeyExtractionFails_ThrowsException() {
        // Given
        String gifticonId = "test-id-123";
        String newImageUrl = "https://bucket.s3.amazonaws.com/gifticons/new-image.jpg";

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        given(s3Service.uploadFile(mockImage)).willReturn(newImageUrl);
        given(s3Service.extractKeyFromUrl(newImageUrl))
                .willThrow(new IllegalArgumentException("키 추출 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.updateGifticon(gifticonId, gifticonRequestDto))
                .isInstanceOf(FileProcessingException.class);

        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(s3Service, times(1)).extractKeyFromUrl(newImageUrl);
    }

    @Test
    @DisplayName("기프티콘 수정 실패 - DB 저장 실패")
    void updateGifticon_DatabaseSaveFails_ThrowsException() {
        // Given
        String gifticonId = "test-id-123";

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        given(gifticonRepository.save(any(Gifticon.class)))
                .willThrow(new RuntimeException("DB 저장 실패"));

        GifticonRequestDto updateRequestWithoutImage = new GifticonRequestDto(
                "수정된 이름",
                "수정된 설명",
                5000L,
                null
        );

        // When & Then
        assertThatThrownBy(() -> gifticonService.updateGifticon(gifticonId, updateRequestWithoutImage))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 삭제 성공")
    void deleteGifticon_Success() {
        // Given
        String gifticonId = "test-id-123";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));

        // When
        ApiResponse<Void> result = gifticonService.deleteGifticon(gifticonId);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData()).isNull();

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, times(1)).deleteFile(gifticon.getImageKey());
        verify(gifticonRepository, times(1)).delete(gifticon);
    }

    @Test
    @DisplayName("기프티콘 삭제 성공 - 이미지 없는 경우")
    void deleteGifticon_WithoutImage_Success() {
        // Given
        String gifticonId = "test-id-123";
        Gifticon gifticonWithoutImage = Gifticon.of(
                "이미지 없는 기프티콘",
                1000L,
                "설명",
                null,
                null
        );
        gifticonWithoutImage.setId(gifticonId);

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticonWithoutImage));

        // When
        ApiResponse<Void> result = gifticonService.deleteGifticon(gifticonId);

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, never()).deleteFile(any());  // 이미지 삭제 안함
        verify(gifticonRepository, times(1)).delete(gifticonWithoutImage);
    }

    @Test
    @DisplayName("기프티콘 삭제 실패 - 존재하지 않는 ID")
    void deleteGifticon_NotFound_ThrowsException() {
        // Given
        String gifticonId = "non-existent-id";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gifticonService.deleteGifticon(gifticonId))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, never()).deleteFile(any());
        verify(gifticonRepository, never()).delete(any());
    }

    @Test
    @DisplayName("기프티콘 삭제 실패 - S3 삭제 실패")
    void deleteGifticon_S3DeleteFails_ThrowsException() {
        // Given
        String gifticonId = "test-id-123";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        doThrow(new RuntimeException("S3 삭제 실패")).when(s3Service).deleteFile(gifticon.getImageKey());

        // When & Then
        assertThatThrownBy(() -> gifticonService.deleteGifticon(gifticonId))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, times(1)).deleteFile(gifticon.getImageKey());
        verify(gifticonRepository, never()).delete(any());  // S3 실패 시 DB 삭제 안함
    }

    @Test
    @DisplayName("기프티콘 삭제 실패 - DB 삭제 실패")
    void deleteGifticon_DatabaseDeleteFails_ThrowsException() {
        // Given
        String gifticonId = "test-id-123";
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        doThrow(new RuntimeException("DB 삭제 실패")).when(gifticonRepository).delete(gifticon);

        // When & Then
        assertThatThrownBy(() -> gifticonService.deleteGifticon(gifticonId))
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).findById(gifticonId);
        verify(s3Service, times(1)).deleteFile(gifticon.getImageKey());
        verify(gifticonRepository, times(1)).delete(gifticon);
    }

    @Test
    @DisplayName("기프티콘 수량 조회 성공")
    void countGifticon_Success() {
        // Given
        long expectedCount = 5L;
        given(gifticonRepository.count()).willReturn(expectedCount);

        // When
        ApiResponse<Long> result = gifticonService.countGifticon();

        // Then
        assertThat(result.getResultCode()).isEqualTo(200);
        assertThat(result.getData()).isEqualTo(expectedCount);

        verify(gifticonRepository, times(1)).count();
    }

    @Test
    @DisplayName("기프티콘 수량 조회 실패 - DB 오류")
    void countGifticon_DatabaseError_ThrowsException() {
        // Given
        given(gifticonRepository.count()).willThrow(new RuntimeException("DB 연결 실패"));

        // When & Then
        assertThatThrownBy(() -> gifticonService.countGifticon())
                .isInstanceOf(FileProcessingException.class);

        verify(gifticonRepository, times(1)).count();
    }
}