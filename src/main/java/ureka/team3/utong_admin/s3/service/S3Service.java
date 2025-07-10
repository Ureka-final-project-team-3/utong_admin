package ureka.team3.utong_admin.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ureka.team3.utong_admin.common.exception.business.FileProcessingException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String key = uploadDir + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest pubObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), metadata
            );

            amazonS3.putObject(pubObjectRequest);

            return amazonS3.getUrl(bucketName, key).toString();

        } catch (Exception e) {
            log.info("파일 업로드 중 오류가 발생하였습니다. {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    public void deleteFile(String key) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            log.info("파일이 성공적으로 삭제되었습니다. key: {}", key);
        } catch (Exception e) {
            log.info("파일 삭제 중 오류가 발생하였습니다. {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    public String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL이 비어있거나 null입니다.");
        }
        return url.substring(url.indexOf(uploadDir));
    }

}
