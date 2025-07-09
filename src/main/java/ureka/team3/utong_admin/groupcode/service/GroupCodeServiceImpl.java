package ureka.team3.utong_admin.groupcode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.GroupCodeNotFoundException;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;
import ureka.team3.utong_admin.groupcode.entity.GroupCode;
import ureka.team3.utong_admin.groupcode.repository.GroupCodeRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupCodeServiceImpl implements GroupCodeService {

    private final GroupCodeRepository groupCodeRepository;

    public ApiResponse<List<GroupCodeDto>> listGroupCode(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<GroupCode> page = groupCodeRepository.findAll(pageable);
            List<GroupCodeDto> groupCodeDtoList = page.toList()
                    .stream()
                    .map(GroupCodeDto::from)
                    .toList();

            return ApiResponse.success(groupCodeDtoList);
        } catch (Exception e) {
            log.info("서버에서 그룹 코드 목록을 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiResponse<GroupCodeDto> detailGroupCode(String groupCode) {
        try {
            GroupCode findGroupCode = groupCodeRepository.findById(groupCode)
                    .orElseThrow(() -> new GroupCodeNotFoundException("그룹 코드가 존재하지 않습니다."));

            return ApiResponse.success(GroupCodeDto.from(findGroupCode));
        } catch (Exception e) {
            log.info("서버에서 그룹 코드 상세 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiResponse<Void> createGroupCode(GroupCodeDto groupCodeDto) {
        try {
            GroupCode groupCode = GroupCode.of(groupCodeDto);

            groupCodeRepository.save(groupCode);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 그룹 코드를 생성하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiResponse<Void> updateGroupCode(String groupCode, GroupCodeDto groupCodeDto) {
        try {
            GroupCode findGroupCode = groupCodeRepository.findById(groupCode)
                    .orElseThrow(() -> new GroupCodeNotFoundException("그룹 코드가 존재하지 않습니다."));

            groupCodeDto.setGroupCode(groupCode);
            GroupCode updatedGroupCode = GroupCode.of(groupCodeDto);

            groupCodeRepository.save(updatedGroupCode);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 그룹 코드를 업데이트하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiResponse<Void> deleteGroupCode(String groupCode) {
        try {
            GroupCode findGroupCode = groupCodeRepository.findById(groupCode)
                    .orElseThrow(() -> new GroupCodeNotFoundException("그룹 코드가 존재하지 않습니다."));

            groupCodeRepository.delete(findGroupCode);

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.info("서버에서 그룹 코드를 삭제하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiResponse<Long> countGroupCode() {
        try {
            long count = groupCodeRepository.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.info("서버에서 그룹 코드 개수를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
