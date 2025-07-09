package ureka.team3.utong_admin.code.service;

import ureka.team3.utong_admin.code.dto.CodeDto;
import ureka.team3.utong_admin.code.entity.CodeKey;
import ureka.team3.utong_admin.common.dto.ApiResponse;

import java.util.List;

public interface CodeService {

    public ApiResponse<List<CodeDto>> listCode(String groupCode, int pageNumber, int pageSize);

    public ApiResponse<CodeDto> detailCode(CodeKey codeKey);

    public ApiResponse<Void> createCode(CodeDto codeDto);

    public ApiResponse<Void> updateCode(CodeKey codeKey, CodeDto codeDto);

    public ApiResponse<Void> deleteCode(CodeKey codeKey);

    public ApiResponse<Long> countCode();
}
