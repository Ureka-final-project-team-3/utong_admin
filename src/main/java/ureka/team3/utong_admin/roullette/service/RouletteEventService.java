package ureka.team3.utong_admin.roullette.service;

import java.util.List;

import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.roullette.dto.RouletteEventDto;

public interface RouletteEventService {

    ApiResponse<List<RouletteEventDto>> listRouletteEvents(int pageNumber, int pageSize);

    ApiResponse<RouletteEventDto> detailRouletteEvent(String eventId);

    ApiResponse<Void> createRouletteEvent(RouletteEventDto eventDto);

    ApiResponse<Void> updateRouletteEvent(String eventId, RouletteEventDto eventDto);

    ApiResponse<Void> updateEventStatus(String eventId, Boolean isActive);

    ApiResponse<Void> deleteRouletteEvent(String eventId);

    ApiResponse<Long> countRouletteEvents();
}