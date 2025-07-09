package ureka.team3.utong_admin.groupcode.service;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;

import java.util.List;

public interface GroupCodeService {

    public ApiResponse<List<GroupCodeDto>> listGroupCode(int pageNumber, int pageSize);

    public ApiResponse<GroupCodeDto> detailGroupCode(String groupCode);

    public ApiResponse<Void> createGroupCode(GroupCodeDto groupCodeDto);

    public ApiResponse<Void> updateGroupCode(String groupCode, GroupCodeDto groupCodeDto);

    public ApiResponse<Void> deleteGroupCode(String groupCode);

    public ApiResponse<Long> countGroupCode();

}
