package ureka.team3.utong_admin.code.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureka.team3.utong_admin.code.dto.CodeDto;
import ureka.team3.utong_admin.code.entity.CodeKey;
import ureka.team3.utong_admin.code.service.CodeService;
import ureka.team3.utong_admin.common.dto.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class CodeController {

    private final CodeService codeService;

    @GetMapping("/codes")
    public ResponseEntity<ApiResponse<List<CodeDto>>> listCode(String groupCode, int pageNumber, int pageSize) {
        return ResponseEntity.ok(codeService.listCode(groupCode, pageNumber, pageSize));
    }

    @GetMapping("/codes/{groupCode}/{code}")
    public ResponseEntity<ApiResponse<CodeDto>> detailCode(@PathVariable String groupCode, @PathVariable String code) {
        return ResponseEntity.ok(codeService.detailCode(new CodeKey(groupCode, code)));
    }

    @PostMapping("/codes")
    public ResponseEntity<ApiResponse<Void>> createCode(@RequestBody CodeDto codeDto) {
        return ResponseEntity.ok(codeService.createCode(codeDto));
    }

    @PutMapping("/codes/{groupCode}/{code}")
    public ResponseEntity<ApiResponse<Void>> updateCode(
            @PathVariable String groupCode,
            @PathVariable String code,
            @RequestBody CodeDto codeDto
    ) {
        return ResponseEntity.ok(codeService.updateCode(new CodeKey(groupCode, code), codeDto));
    }

    @DeleteMapping("/codes/{groupCode}/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteCode(@PathVariable String groupCode, @PathVariable String code) {
        return ResponseEntity.ok(codeService.deleteCode(new CodeKey(groupCode, code)));
    }

    @GetMapping("/codes/count")
    public ResponseEntity<ApiResponse<Long>> countCode() {
        return ResponseEntity.ok(codeService.countCode());
    }
}
