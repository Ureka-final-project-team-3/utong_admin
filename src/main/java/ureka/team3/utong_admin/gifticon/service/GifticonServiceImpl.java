package ureka.team3.utong_admin.gifticon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.business.FileProcessingException;
import ureka.team3.utong_admin.gifticon.dto.request.GifticonRequestDto;
import ureka.team3.utong_admin.gifticon.dto.response.GifticonResponseDto;
import ureka.team3.utong_admin.gifticon.entity.Gifticon;
import ureka.team3.utong_admin.gifticon.repository.GifticonRepository;
import ureka.team3.utong_admin.s3.service.S3Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GifticonServiceImpl implements GifticonService {

    private final GifticonRepository gifticonRepository;

    private final S3Service s3Service;

    @Override
    public ApiResponse<GifticonResponseDto> createGifticon(GifticonRequestDto gifticonRequestDto) {
        String imageUrl = null;
        String imageKey = null;

        try {

            log.info("받은 데이터 - name: {}, price: {}, description: {}, category: {}, image: {}",
                    gifticonRequestDto.getName(),
                    gifticonRequestDto.getPrice(),
                    gifticonRequestDto.getDescription(),
                    gifticonRequestDto.getCategory(),
                    gifticonRequestDto.getImage() != null ? gifticonRequestDto.getImage().getOriginalFilename() : "null");

            // 데이터 검증
            if (gifticonRequestDto.getName() == null || gifticonRequestDto.getName().trim().isEmpty()) {
                throw new FileProcessingException("기프티콘 이름이 비어있습니다.");
            }
            if (gifticonRequestDto.getPrice() == null) {
                throw new FileProcessingException("가격이 설정되지 않았습니다.");
            }
            if (gifticonRequestDto.getCategory() == null || gifticonRequestDto.getCategory().trim().isEmpty()) {
                throw new FileProcessingException("카테고리가 설정되지 않았습니다.");
            }

            if(gifticonRequestDto.getImage() != null && !gifticonRequestDto.getImage().isEmpty()) {
                imageUrl = s3Service.uploadFile(gifticonRequestDto.getImage());
                imageKey = s3Service.extractKeyFromUrl(imageUrl);
            }

            Gifticon gifticon = Gifticon.of(
                    gifticonRequestDto.getName(),
                    gifticonRequestDto.getPrice(),
                    gifticonRequestDto.getDescription(),
                    imageUrl,
                    imageKey,
                    gifticonRequestDto.getCategory()
            );

            Gifticon savedGifticon = gifticonRepository.save(gifticon);
            log.info("기프티콘 생성 완료 : {}", savedGifticon.getId());

            return ApiResponse.success(GifticonResponseDto.from(savedGifticon));
        } catch (Exception e) {
            log.error("기프티콘 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new FileProcessingException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<GifticonResponseDto>> listGifticon(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            List<GifticonResponseDto> gifticonResponseDtoList = gifticonRepository.findAll(pageable)
                    .stream()
                    .map(GifticonResponseDto::from)
                    .toList();

            return ApiResponse.success(gifticonResponseDtoList);
        } catch (Exception e) {
            log.info("기프티콘 목록 조회 중 오류 발생: {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<GifticonResponseDto> detailGifticon(String id) {
        try {
            Gifticon gifticon = gifticonRepository.findById(id)
                    .orElseThrow(() -> new FileProcessingException("해당 기프티콘이 존재하지 않습니다."));

            return ApiResponse.success(GifticonResponseDto.from(gifticon));
        } catch (Exception e) {
            log.info("기프티콘 상세 조회 중 오류 발생: {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    @Override
    public ApiResponse<GifticonResponseDto> updateGifticon(String id, GifticonRequestDto gifticonRequestDto) {
        try {
            Gifticon gifticon = gifticonRepository.findById(id)
                    .orElseThrow(() -> new FileProcessingException("해당 기프티콘이 존재하지 않습니다."));

            gifticon.setName(gifticonRequestDto.getName());
            gifticon.setPrice(gifticonRequestDto.getPrice());
            gifticon.setDescription(gifticonRequestDto.getDescription());
            gifticon.setCategory(gifticonRequestDto.getCategory());

            if(gifticonRequestDto.getImage() != null && !gifticonRequestDto.getImage().isEmpty()) {
                if (gifticon.getImageKey() != null) {
                    s3Service.deleteFile(gifticon.getImageKey());
                }
                String newImageUrl = s3Service.uploadFile(gifticonRequestDto.getImage());
                String newImageKey = s3Service.extractKeyFromUrl(newImageUrl);

                gifticon.setImageUrl(newImageUrl);
                gifticon.setImageKey(newImageKey);
            }

            Gifticon updatedGifticon = gifticonRepository.save(gifticon);
            log.info("기프티콘 수정 완료 : {}", updatedGifticon.getId());

            return ApiResponse.success(GifticonResponseDto.from(updatedGifticon));
        } catch (Exception e) {
            log.info("기프티콘 수정 중 오류 발생: {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    @Override
    public ApiResponse<Void> deleteGifticon(String id) {
        try {
            Gifticon gifticon = gifticonRepository.findById(id)
                    .orElseThrow(() -> new FileProcessingException("해당 기프티콘이 존재하지 않습니다."));

            if (gifticon.getImageKey() != null) {
                s3Service.deleteFile(gifticon.getImageKey());
            }

            gifticonRepository.delete(gifticon);
            log.info("기프티콘 삭제 완료 : {}", id);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("기프티콘 삭제 중 오류 발생: {}", e.getMessage());
            throw new FileProcessingException();
        }
    }

    @Override
    public ApiResponse<Long> countGifticon() {
        try {
            long count = gifticonRepository.count();
            log.info("기프티콘 총 개수: {}", count);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.info("기프티콘 개수 조회 중 오류 발생: {}", e.getMessage());
            throw new FileProcessingException();
        }
    }
}