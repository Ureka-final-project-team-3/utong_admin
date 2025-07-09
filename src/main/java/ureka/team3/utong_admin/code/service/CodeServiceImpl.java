package ureka.team3.utong_admin.code.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ureka.team3.utong_admin.code.dto.CodeDto;
import ureka.team3.utong_admin.code.entity.Code;
import ureka.team3.utong_admin.code.entity.CodeKey;
import ureka.team3.utong_admin.code.repository.CodeRepository;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.CodeNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;


    @Override
    public ApiResponse<List<CodeDto>> listCode(String groupCode, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Code> page = codeRepository.findByGroupCode(groupCode, pageable);

            List<CodeDto> codeDtoList = page.toList()
                    .stream()
                    .map(CodeDto::from)
                    .toList();

            return ApiResponse.success(codeDtoList);

        } catch (Exception e) {
            log.info("서버에서 코드 목록을 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<CodeDto> detailCode(CodeKey codeKey) {
        try {
            Code code = codeRepository.findById(codeKey)
                    .orElseThrow(() -> new CodeNotFoundException("코드가 존재하지 않습니다."));

            return ApiResponse.success(CodeDto.from(code));
        } catch (Exception e) {
            log.info("서버에서 코드 상세 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Void> createCode(CodeDto codeDto) {
        try {
            Code code = Code.of(codeDto);

            codeRepository.save(code);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 코드를 생성하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Void> updateCode(CodeKey codeKey, CodeDto codeDto) {
        try {
            Code code = codeRepository.findById(codeKey)
                    .orElseThrow(() -> new CodeNotFoundException("코드가 존재하지 않습니다."));

            codeDto.setGroupCode(codeKey.getGroupCode());
            codeDto.setCode(codeKey.getCode());

            Code updatedCode = Code.of(codeDto);

            codeRepository.save(updatedCode);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 코드를 업데이트하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Void> deleteCode(CodeKey codeKey) {
        try {
            Code code = codeRepository.findById(codeKey)
                    .orElseThrow(() -> new CodeNotFoundException("코드가 존재하지 않습니다."));

            codeRepository.delete(code);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 코드를 삭제하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Long> countCode() {
        try {
            long count = codeRepository.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.info("서버에서 코드 개수를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
