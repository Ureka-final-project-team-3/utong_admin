package ureka.team3.utong_admin.groupcode.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.GroupCodeNotFoundException;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;
import ureka.team3.utong_admin.groupcode.entity.GroupCode;
import ureka.team3.utong_admin.groupcode.repository.GroupCodeRepository;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GroupCodeServiceTest {

    @InjectMocks
    private GroupCodeServiceImpl groupCodeService;

    @Mock
    private GroupCodeRepository groupCodeRepository;

    @Test
    void listGroupCode_성공_Test() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        GroupCodeDto groupCode1 = GroupCodeDto.builder()
                .groupCode("010")
                .groupCodeName("테스트1")
                .groupCodeDesc("테스트 그룹 코드 1")
                .build();

        GroupCodeDto groupCode2 = GroupCodeDto.builder()
                .groupCode("020")
                .groupCodeName("테스트2")
                .groupCodeDesc("테스트 그룹 코드 2")
                .build();

        List<GroupCode> groupCodeList = Arrays.asList(
                GroupCode.of(groupCode1),
                GroupCode.of(groupCode2)
        );

        Page<GroupCode> page = new PageImpl<>(groupCodeList, pageable, groupCodeList.size());

        when(groupCodeRepository.findAll(pageable)).thenReturn(page);

        // when
        ApiResponse<List<GroupCodeDto>> response = groupCodeService.listGroupCode(pageNumber, pageSize);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getGroupCode()).isEqualTo("010");
        assertThat(response.getData().get(0).getGroupCodeName()).isEqualTo("테스트1");
        assertThat(response.getData().get(0).getGroupCodeDesc()).isEqualTo("테스트 그룹 코드 1");
        assertThat(response.getData().get(1).getGroupCode()).isEqualTo("020");
        assertThat(response.getData().get(1).getGroupCodeName()).isEqualTo("테스트2");
        assertThat(response.getData().get(1).getGroupCodeDesc()).isEqualTo("테스트 그룹 코드 2");
    }

    @Test
    void listGroupCode_실패_test() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(groupCodeRepository.findAll(pageable)).thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.listGroupCode(pageNumber, pageSize);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).findAll(pageable);
    }

    @Test
    void detailGroupCode_성공_Test() {
        // given
        String groupCode = "010";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("테스트 그룹 코드")
                .groupCodeDesc("테스트 그룹 코드 설명")
                .build();

        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.of(GroupCode.of(groupCodeDto)));

        // when
        ApiResponse<GroupCodeDto> response = groupCodeService.detailGroupCode(groupCode);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData().getGroupCode()).isEqualTo("010");
        assertThat(response.getData().getGroupCodeName()).isEqualTo("테스트 그룹 코드");
        assertThat(response.getData().getGroupCodeDesc()).isEqualTo("테스트 그룹 코드 설명");
    }

    @Test
    void detailGroupCode_실패_test() {
        // given
        String groupCode = "NONEXISTENT";
        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.detailGroupCode(groupCode);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).findById(groupCode);
    }

    @Test
    void createGroupCode_성공_Test() {
        // given
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode("030")
                .groupCodeName("새로운 그룹 코드")
                .groupCodeDesc("새로운 그룹 코드 설명")
                .build();

        GroupCode groupCode = GroupCode.of(groupCodeDto);

        // when
        ApiResponse<Void> response = groupCodeService.createGroupCode(groupCodeDto);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void createGroupCode_실패_test() {
        // given
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode("010") // 이미 존재하는 그룹 코드
                .groupCodeName("중복 그룹 코드")
                .groupCodeDesc("중복 그룹 코드 설명")
                .build();

        doThrow(new RuntimeException("Group code already exists"))
                .when(groupCodeRepository).save(any(GroupCode.class));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.createGroupCode(groupCodeDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).save(any(GroupCode.class));
    }

    @Test
    void updateGroupCode_성공_Test() {
        // given
        String groupCode = "010";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("업데이트된 그룹 코드")
                .groupCodeDesc("업데이트된 그룹 코드 설명")
                .build();

        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.of(GroupCode.of(groupCodeDto)));

        // when
        ApiResponse<Void> response = groupCodeService.updateGroupCode(groupCode, groupCodeDto);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
    }

    @Test
    void updateGroupCode_실패_test() {
        // given
        String groupCode = "NONEXISTENT";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("업데이트된 그룹 코드")
                .groupCodeDesc("업데이트된 그룹 코드 설명")
                .build();

        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.updateGroupCode(groupCode, groupCodeDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).findById(groupCode);
    }

    @Test
    void deleteGroupCode_성공_Test() {
        // given
        String groupCode = "010";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("삭제할 그룹 코드")
                .groupCodeDesc("삭제할 그룹 코드 설명")
                .build();

        GroupCode findGroupCode = GroupCode.of(groupCodeDto);

        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.of(findGroupCode));

        // when
        ApiResponse<Void> response = groupCodeService.deleteGroupCode(groupCode);

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        verify(groupCodeRepository, times(1)).delete(findGroupCode);
    }

    @Test
    void deleteGroupCode_실패_test() {
        // given
        String groupCode = "NONEXISTENT";
        when(groupCodeRepository.findById(groupCode)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.deleteGroupCode(groupCode);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).findById(groupCode);

    }

    @Test
    void countGroupCode_성공_Test() {
        // given
        long expectedCount = 5L;
        when(groupCodeRepository.count()).thenReturn(expectedCount);

        // when
        ApiResponse<Long> response = groupCodeService.countGroupCode();

        // then
        assertThat(response.getResultCode()).isEqualTo(200);
        assertThat(response.getData()).isEqualTo(expectedCount);
    }

    @Test
    void countGroupCode_실패_test() {
        // given
        when(groupCodeRepository.count()).thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            groupCodeService.countGroupCode();
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        verify(groupCodeRepository, times(1)).count();
    }

}