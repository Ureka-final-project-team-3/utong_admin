package ureka.team3.utong_admin.gifticon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.gifticon.dto.request.GifticonRequestDto;
import ureka.team3.utong_admin.gifticon.dto.response.GifticonResponseDto;
import ureka.team3.utong_admin.gifticon.service.GifticonService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class GifticonController {

    private final GifticonService gifticonService;

    @GetMapping("/gifticons")
    public ResponseEntity<ApiResponse<List<GifticonResponseDto>>> listGifticon(int pageNumber, int pageSize) {
        return ResponseEntity.ok(gifticonService.listGifticon(pageNumber, pageSize));
    }

    @GetMapping("/gifticons/{id}")
    public ResponseEntity<ApiResponse<GifticonResponseDto>> detailGifticon(@PathVariable String id) {
        return ResponseEntity.ok(gifticonService.detailGifticon(id));
    }

    @PostMapping("/gifticons")
    public ResponseEntity<ApiResponse<GifticonResponseDto>> createGifticon(
            @ModelAttribute GifticonRequestDto gifticonRequestDto
    ) {
        return ResponseEntity.ok(gifticonService.createGifticon(gifticonRequestDto));
    }

    @PutMapping("/gifticons/{id}")
    public ResponseEntity<ApiResponse<GifticonResponseDto>> updateGifticon(
            @PathVariable String id,
            @ModelAttribute GifticonRequestDto gifticonRequestDto
    ) {
        return ResponseEntity.ok(gifticonService.updateGifticon(id, gifticonRequestDto));
    }

    @DeleteMapping("/gifticons/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGifticon(@PathVariable String id) {
        return ResponseEntity.ok(gifticonService.deleteGifticon(id));
    }
}


