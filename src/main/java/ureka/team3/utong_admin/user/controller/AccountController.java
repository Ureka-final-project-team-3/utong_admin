package ureka.team3.utong_admin.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.user.dto.AccountDto;
import ureka.team3.utong_admin.user.service.AccountService;

import jakarta.annotation.PostConstruct;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostConstruct
    public void init() {
        log.info("AccountController initialized successfully");
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountDto>>> listAccount(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        log.info("GET /api/admin/accounts called");
        return ResponseEntity.ok(accountService.listAccount(pageNumber, pageSize));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<AccountDto>> detailAccount(@PathVariable(name = "accountId") String accountId) {
        log.info("GET /api/admin/accounts/{} called", accountId);
        return ResponseEntity.ok(accountService.detailAccount(accountId));
    }

    @GetMapping("/accounts/search")
    public ResponseEntity<ApiResponse<List<AccountDto>>> searchAccount(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        log.info("GET /api/admin/accounts/search called with keyword: {}", keyword);
        return ResponseEntity.ok(accountService.searchAccount(keyword, pageNumber, pageSize));
    }

    @GetMapping("/accounts/count")
    public ResponseEntity<ApiResponse<Long>> countAccount() {
        log.info("GET /api/admin/accounts/count called");
        return ResponseEntity.ok(accountService.countAccount());
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable(name = "accountId") String accountId) {
        log.info("DELETE /api/admin/accounts/{} called", accountId);
        return ResponseEntity.ok(accountService.deleteAccount(accountId));
    }
}