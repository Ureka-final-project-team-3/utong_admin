package ureka.team3.utong_admin.user.service;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.user.dto.AccountDto;

import java.util.List;

public interface AccountService {

    ApiResponse<List<AccountDto>> listAccount(int pageNumber, int pageSize);

    ApiResponse<AccountDto> detailAccount(String accountId);

    ApiResponse<List<AccountDto>> searchAccount(String searchKeyword, int pageNumber, int pageSize);

    ApiResponse<Long> countAccount();

    ApiResponse<Void> deleteAccount(String accountId);
}