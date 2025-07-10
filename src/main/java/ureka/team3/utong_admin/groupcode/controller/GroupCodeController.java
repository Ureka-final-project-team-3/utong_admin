package ureka.team3.utong_admin.groupcode.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;
import ureka.team3.utong_admin.groupcode.service.GroupCodeServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class GroupCodeController {

    private final GroupCodeServiceImpl groupCodeService;

    @GetMapping("/group-codes")
    public ResponseEntity<ApiResponse<List<GroupCodeDto>>> listGroupCode(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(groupCodeService.listGroupCode(pageNumber, pageSize));
    }

    @GetMapping("/group-codes/{groupCode}")
    public ResponseEntity<ApiResponse<GroupCodeDto>> getGroupCode(@PathVariable("groupCode") String groupCode) {
        return ResponseEntity.ok(groupCodeService.detailGroupCode(groupCode));
    }

    @GetMapping("/group-codes/count")
    public ResponseEntity<ApiResponse<Long>> countGroupCodes() {
        return ResponseEntity.ok(groupCodeService.countGroupCode());
    }

    @PostMapping("/group-codes")
    public ResponseEntity<ApiResponse<Void>> createGroupCode(@RequestBody GroupCodeDto groupCodeDto) {
        return ResponseEntity.ok(groupCodeService.createGroupCode(groupCodeDto));
    }

    @PutMapping("/group-codes/{groupCode}")
    public ResponseEntity<ApiResponse<Void>> updateGroupCode(
            @PathVariable("groupCode") String groupCode,
            @RequestBody GroupCodeDto groupCodeDto
    ) {
        return ResponseEntity.ok(groupCodeService.updateGroupCode(groupCode, groupCodeDto));
    }

    @DeleteMapping("/group-codes/{groupCode}")
    public ResponseEntity<ApiResponse<Void>> deleteGroupCode(@PathVariable("groupCode") String groupCode) {
        return ResponseEntity.ok(groupCodeService.deleteGroupCode(groupCode));
    }
}
