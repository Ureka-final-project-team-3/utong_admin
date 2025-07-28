package ureka.team3.utong_admin.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ureka.team3.utong_admin.common.exception.business.FileProcessingException;

import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "uploadDir", "gifticons/");
    }

    @Test
    void uploadFile_성공_test() throws Exception {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String expectedUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/gifticons/uuid_test-image.jpg";
        URL mockUrl = new URL(expectedUrl);

        given(amazonS3.getUrl(eq("test-bucket"), anyString())).willReturn(mockUrl);

        String result = s3Service.uploadFile(mockFile);

        assertThat(result).isEqualTo(expectedUrl);
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFile_실패_test() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        given(amazonS3.putObject(any(PutObjectRequest.class)))
                .willThrow(new RuntimeException("S3 연결 실패"));

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(mockFile))
                .isInstanceOf(FileProcessingException.class);
    }

    @Test
    void deleteFile_성공_test() {
        // Given
        String key = "gifticons/test-image.jpg";

        // When
        assertThatCode(() -> s3Service.deleteFile(key))
                .doesNotThrowAnyException();

        // Then
        verify(amazonS3, times(1)).deleteObject(any());
    }

    @Test
    void deleteFile_실패_test() {
        // Given
        String key = "gifticons/test-image.jpg";
        doThrow(new RuntimeException("S3 삭제 실패"))
                .when(amazonS3).deleteObject(any());

        // When & Then
        assertThatThrownBy(() -> s3Service.deleteFile(key))
                .isInstanceOf(FileProcessingException.class);
    }

    @Test
    void extractKeyFromUrl_성공_test() {
        // Given
        String url = "https://bucket.s3.amazonaws.com/gifticons/uuid_image.jpg";

        // When
        String result = s3Service.extractKeyFromUrl(url);

        // Then
        assertThat(result).isEqualTo("gifticons/uuid_image.jpg");
    }

    @Test
    void extractKeyFromUrl_실패_test() {
        // When & Then
        assertThatThrownBy(() -> s3Service.extractKeyFromUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL이 비어있거나 null입니다.");
    }

    @Test
    void extractKeyFromUrl_실패_null_test() {
        // When & Then
        assertThatThrownBy(() -> s3Service.extractKeyFromUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL이 비어있거나 null입니다.");
    }
}