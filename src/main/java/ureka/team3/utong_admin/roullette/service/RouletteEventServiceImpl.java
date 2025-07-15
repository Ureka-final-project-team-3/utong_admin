package ureka.team3.utong_admin.roullette.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.common.exception.BusinessException;
import ureka.team3.utong_admin.common.exception.ErrorCode;
import ureka.team3.utong_admin.common.exception.business.RouletteEventNotFoundException;
import ureka.team3.utong_admin.roullette.dto.RouletteEventDto;
import ureka.team3.utong_admin.roullette.entity.RouletteEvent;
import ureka.team3.utong_admin.roullette.repository.RouletteEventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RouletteEventServiceImpl implements RouletteEventService {

    private final RouletteEventRepository rouletteEventRepository;

    @Override
    public ApiResponse<List<RouletteEventDto>> listRouletteEvents(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<RouletteEvent> page = rouletteEventRepository.findAllOrderByCreatedAtDesc(pageable);
            
            List<RouletteEventDto> eventDtoList = page.getContent()
                    .stream()
                    .map(RouletteEventDto::from)
                    .toList();

            return ApiResponse.success(eventDtoList);
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트 목록을 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<RouletteEventDto> detailRouletteEvent(String eventId) {
        try {
            RouletteEvent event = rouletteEventRepository.findById(eventId)
                    .orElseThrow(() -> new RouletteEventNotFoundException("룰렛 이벤트가 존재하지 않습니다."));

            return ApiResponse.success(RouletteEventDto.from(event));
        } catch (RouletteEventNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트 상세 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> createRouletteEvent(RouletteEventDto eventDto) {
        try {
            if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            }

            if (eventDto.getWinProbability().compareTo(BigDecimal.ZERO) < 0 || 
                eventDto.getWinProbability().compareTo(BigDecimal.ONE) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "당첨 확률은 0과 1 사이의 값이어야 합니다.");
            }

            eventDto.setId(UUID.randomUUID().toString());
            RouletteEvent event = RouletteEvent.of(eventDto);
            rouletteEventRepository.save(event);

            log.info("룰렛 이벤트 생성 완료: {}", event.getId());
            return ApiResponse.success(null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트를 생성하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateRouletteEvent(String eventId, RouletteEventDto eventDto) {
        try {
            RouletteEvent event = rouletteEventRepository.findById(eventId)
                    .orElseThrow(() -> new RouletteEventNotFoundException("룰렛 이벤트가 존재하지 않습니다."));

            if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            }

            if (eventDto.getWinProbability().compareTo(BigDecimal.ZERO) < 0 || 
                eventDto.getWinProbability().compareTo(BigDecimal.ONE) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "당첨 확률은 0과 1 사이의 값이어야 합니다.");
            }

            event.updateEvent(eventDto);
            rouletteEventRepository.save(event);

            log.info("룰렛 이벤트 수정 완료: {}", eventId);
            return ApiResponse.success(null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트를 수정하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateEventStatus(String eventId, Boolean isActive) {
        try {
            RouletteEvent event = rouletteEventRepository.findById(eventId)
                    .orElseThrow(() -> new RouletteEventNotFoundException("룰렛 이벤트가 존재하지 않습니다."));

            event.updateStatus(isActive);
            rouletteEventRepository.save(event);

            log.info("룰렛 이벤트 상태 변경 완료: {} -> {}", eventId, isActive);
            return ApiResponse.success(null);
        } catch (RouletteEventNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트 상태를 변경하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteRouletteEvent(String eventId) {
        try {
            RouletteEvent event = rouletteEventRepository.findById(eventId)
                    .orElseThrow(() -> new RouletteEventNotFoundException("룰렛 이벤트가 존재하지 않습니다."));

            rouletteEventRepository.delete(event);

            log.info("룰렛 이벤트 삭제 완료: {}", eventId);
            return ApiResponse.success(null);
        } catch (RouletteEventNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트를 삭제하는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Long> countRouletteEvents() {
        try {
            long count = rouletteEventRepository.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("서버에서 룰렛 이벤트 개수를 가져오는 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}