package ureka.team3.utong_admin.code.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ureka.team3.utong_admin.code.dto.CodeDto;
import ureka.team3.utong_admin.code.entity.Code;
import ureka.team3.utong_admin.code.entity.CodeKey;
import ureka.team3.utong_admin.code.repository.CodeRepository;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @InjectMocks
    private CodeServiceImpl codeService;

    @Mock
    private CodeRepository codeRepository;

    @Test
    void listCode_성공_Test() {
        // given
        String groupCode = "010";
        int pageNumber = 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        CodeDto codeDto1 = CodeDto.builder()
                .groupCode("010")
                .code("001")
                .codeName("테스트 코드1")
                .codeNameBrief("테스트 설명1")
                .orderNo(1)
                .build();

        CodeDto codeDto2 = CodeDto.builder()
                .groupCode("010")
                .code("002")
                .codeName("테스트 코드2")
                .codeNameBrief("테스트 설명2")
                .orderNo(2)
                .build();

        List<Code> codeList = Arrays.asList(
                Code.of(codeDto1),
                Code.of(codeDto2)
        );

        Page<Code> page = new PageImpl<>(codeList, pageable, codeList.size());

        when(codeRepository.findByGroupCode(groupCode, pageable)).thenReturn(page);

        // when
        ApiResponse<List<CodeDto>> response = codeService.listCode(groupCode, pageNumber, pageSize);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getCode()).isEqualTo("001");
        assertThat(response.getData().get(1).getCode()).isEqualTo("002");

    }

    @Test
    void listCode_실패_Test() {
        // given
        String groupCode = "999";
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(codeRepository.findByGroupCode(groupCode, pageable)).thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.listCode(groupCode, pageNumber, pageSize);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).findByGroupCode(groupCode, pageable);
    }

    @Test
    void detailCode_성공_Test() {
        // given
        String groupCode = "010";
        String code = "001";

        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("테스트 코드1")
                .codeNameBrief("테스트 설명1")
                .orderNo(1)
                .build();

        Code codeEntity = Code.of(codeDto);
        CodeKey codeKey = new CodeKey(groupCode, code);

        when(codeRepository.findById(codeKey)).thenReturn(Optional.of(codeEntity));

        when(codeRepository.findById(new CodeKey(groupCode, code))).thenReturn(Optional.of(codeEntity));

        // when
        ApiResponse<CodeDto> response = codeService.detailCode(codeKey);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData().getCode()).isEqualTo("001");
    }

    @Test
    void detailCode_실패_Test() {
        // given
        String groupCode = "010";
        String code = "999"; // 존재하지 않는 코드

        CodeKey codeKey = new CodeKey(groupCode, code);

        when(codeRepository.findById(codeKey)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.detailCode(codeKey);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).findById(codeKey);
    }

    @Test
    void createCode_성공_Test() {
        // given
        CodeDto codeDto = CodeDto.builder()
                .groupCode("010")
                .code("001")
                .codeName("테스트 코드1")
                .codeNameBrief("테스트 설명1")
                .orderNo(1)
                .build();

        Code codeEntity = Code.of(codeDto);

        // when
        ApiResponse<Void> response = codeService.createCode(codeDto);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void createCode_실패_Test() {
        // given
        CodeDto codeDto = CodeDto.builder()
                .groupCode("010")
                .code("001")
                .codeName("테스트 코드1")
                .codeNameBrief("테스트 설명1")
                .orderNo(1)
                .build();

        when(codeRepository.save(any(Code.class))).thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.createCode(codeDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).save(any(Code.class));
    }

    @Test
    void updateCode_성공_Test() {
        // given
        String groupCode = "010";
        String code = "001";

        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("업데이트된 코드")
                .codeNameBrief("업데이트된 설명")
                .orderNo(2)
                .build();

        Code existingCode = Code.of(codeDto);
        CodeKey codeKey = new CodeKey(groupCode, code);

        when(codeRepository.findById(codeKey)).thenReturn(Optional.of(existingCode));

        // when
        ApiResponse<Void> response = codeService.updateCode(codeKey, codeDto);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void updateCode_실패_Test() {
        // given
        String groupCode = "010";
        String code = "999"; // 존재하지 않는 코드

        CodeKey codeKey = new CodeKey(groupCode, code);
        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("업데이트된 코드")
                .codeNameBrief("업데이트된 설명")
                .orderNo(2)
                .build();

        when(codeRepository.findById(codeKey)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.updateCode(codeKey, codeDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).findById(codeKey);
    }

    @Test
    void deleteCode_성공_Test() {
        // given
        String groupCode = "010";
        String code = "001";

        CodeKey codeKey = new CodeKey(groupCode, code);

        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("테스트 코드")
                .codeNameBrief("테스트 설명")
                .orderNo(1)
                .build();

        Code existingCode = Code.of(codeDto);

        when(codeRepository.findById(codeKey)).thenReturn(Optional.of(existingCode));

        // when
        ApiResponse<Void> response = codeService.deleteCode(codeKey);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void deleteCode_실패_Test() {
        // given
        String groupCode = "010";
        String code = "999"; // 존재하지 않는 코드

        CodeKey codeKey = new CodeKey(groupCode, code);

        when(codeRepository.findById(codeKey)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.deleteCode(codeKey);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).findById(codeKey);
    }

    @Test
    void countCode_성공_Test() {
        // given
        long expectedCount = 5;

        when(codeRepository.count()).thenReturn(expectedCount);

        // when
        ApiResponse<Long> response = codeService.countCode();

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData()).isEqualTo(expectedCount);
    }

    @Test
    void countCode_실패_Test() {
        // given
        when(codeRepository.count()).thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            codeService.countCode();
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(codeRepository, times(1)).count();
    }
}
