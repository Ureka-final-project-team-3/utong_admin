package ureka.team3.utong_admin.roullette.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.roullette.dto.RouletteEventDto;
import ureka.team3.utong_admin.roullette.service.RouletteEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/roulette")
@Slf4j
public class RouletteEventController {

    private final RouletteEventService rouletteEventService;

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<RouletteEventDto>>> listRouletteEvents(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        log.info("GET /api/admin/roulette/events called with page: {}, size: {}", pageNumber, pageSize);
        return ResponseEntity.ok(rouletteEventService.listRouletteEvents(pageNumber, pageSize));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<RouletteEventDto>> detailRouletteEvent(
            @PathVariable(name = "eventId") String eventId) {
        log.info("GET /api/admin/roulette/events/{} called", eventId);
        return ResponseEntity.ok(rouletteEventService.detailRouletteEvent(eventId));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Void>> createRouletteEvent(
            @Valid @RequestBody RouletteEventDto eventDto) {
        log.info("POST /api/admin/roulette/events called with title: {}", eventDto.getTitle());
        return ResponseEntity.ok(rouletteEventService.createRouletteEvent(eventDto));
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> updateRouletteEvent(
            @PathVariable(name = "eventId") String eventId,
            @Valid @RequestBody RouletteEventDto eventDto) {
        log.info("PUT /api/admin/roulette/events/{} called", eventId);
        return ResponseEntity.ok(rouletteEventService.updateRouletteEvent(eventId, eventDto));
    }

    @PutMapping("/events/{eventId}/status")
    public ResponseEntity<ApiResponse<Void>> updateEventStatus(
            @PathVariable(name = "eventId") String eventId,
            @RequestBody Map<String, Boolean> statusRequest) {
        log.info("PUT /api/admin/roulette/events/{}/status called with status: {}", eventId, statusRequest.get("isActive"));
        Boolean isActive = statusRequest.get("isActive");
        return ResponseEntity.ok(rouletteEventService.updateEventStatus(eventId, isActive));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteRouletteEvent(
            @PathVariable(name = "eventId") String eventId) {
        log.info("DELETE /api/admin/roulette/events/{} called", eventId);
        return ResponseEntity.ok(rouletteEventService.deleteRouletteEvent(eventId));
    }

    @GetMapping("/events/count")
    public ResponseEntity<ApiResponse<Long>> countRouletteEvents() {
        log.info("GET /api/admin/roulette/events/count called");
        return ResponseEntity.ok(rouletteEventService.countRouletteEvents());
    }
}