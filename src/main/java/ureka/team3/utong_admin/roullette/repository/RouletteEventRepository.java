package ureka.team3.utong_admin.roullette.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ureka.team3.utong_admin.roullette.entity.RouletteEvent;

@Repository
public interface RouletteEventRepository extends JpaRepository<RouletteEvent, String> {

    @Query("SELECT r FROM RouletteEvent r ORDER BY r.createdAt DESC")
    Page<RouletteEvent> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT r FROM RouletteEvent r WHERE r.isActive = true AND r.startDate <= :now AND r.endDate >= :now")
    List<RouletteEvent> findActiveEvents(LocalDateTime now);

    @Query("SELECT COUNT(r) FROM RouletteEvent r WHERE r.isActive = true")
    long countActiveEvents();
    
}