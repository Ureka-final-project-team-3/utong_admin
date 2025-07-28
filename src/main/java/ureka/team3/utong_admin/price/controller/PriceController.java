package ureka.team3.utong_admin.price.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.price.dto.PriceDto;
import ureka.team3.utong_admin.price.service.PriceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class PriceController {

    private final PriceService priceService;

    @PutMapping("/prices")
    public ResponseEntity<ApiResponse<Void>> updatePrice(
            @RequestParam(defaultValue = "903ee67c-71b3-432e-bbd1-aaf5d5043376") String id,
            @RequestBody PriceDto priceDto
    ) {
        return ResponseEntity.ok(priceService.updatePrice(id, priceDto));
    }

    @GetMapping("/prices")
    public ResponseEntity<ApiResponse<PriceDto>> getPrice(
            @RequestParam(defaultValue = "903ee67c-71b3-432e-bbd1-aaf5d5043376") String id
    ) {
        return ResponseEntity.ok(priceService.getPrice(id));
    }
}
