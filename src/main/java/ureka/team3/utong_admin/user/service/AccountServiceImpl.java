package ureka.team3.utong_admin.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.UserNotFoundException;
import ureka.team3.utong_admin.user.dto.AccountDto;
import ureka.team3.utong_admin.user.entity.Account;
import ureka.team3.utong_admin.user.repository.AccountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public ApiResponse<List<AccountDto>> listAccount(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Account> page = accountRepository.findAllWithUserAndLines(pageable);
            
            List<AccountDto> accountDtoList = page.getContent()
                    .stream()
                    .map(AccountDto::from)
                    .toList();

            return ApiResponse.success(accountDtoList);
        } catch (Exception e) {
            log.error("서버에서 계정 목록을 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<AccountDto> detailAccount(String accountId) {
        try {
            Account account = accountRepository.findByIdWithUserAndLines(accountId)
                    .orElseThrow(() -> new UserNotFoundException("계정이 존재하지 않습니다."));

            return ApiResponse.success(AccountDto.from(account));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 계정 상세 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<List<AccountDto>> searchAccount(String searchKeyword, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Account> page = accountRepository.findByEmailContainingOrNicknameContaining(
                    searchKeyword, searchKeyword, pageable);
            
            List<AccountDto> accountDtoList = page.getContent()
                    .stream()
                    .map(AccountDto::from)
                    .toList();

            return ApiResponse.success(accountDtoList);
        } catch (Exception e) {
            log.error("서버에서 계정 검색 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Long> countAccount() {
        try {
            long count = accountRepository.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("서버에서 계정 개수를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteAccount(String accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new UserNotFoundException("계정이 존재하지 않습니다."));

            accountRepository.delete(account);
            log.info("계정 삭제 완료: {}", accountId);

            return ApiResponse.success(null);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 계정을 삭제하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}