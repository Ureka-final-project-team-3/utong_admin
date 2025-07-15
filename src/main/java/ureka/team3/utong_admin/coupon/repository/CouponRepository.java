package ureka.team3.utong_admin.coupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ureka.team3.utong_admin.coupon.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.gifticon ORDER BY c.createdAt DESC")
    Page<Coupon> findAllWithGifticon(Pageable pageable);

    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.gifticon WHERE c.id = :id")
    Optional<Coupon> findByIdWithGifticon(@Param("id") String id);
    
    @Query("SELECT c FROM Coupon c")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(c) FROM Coupon c ")
    long countActiveCoupons();

    List<Coupon> findByGifticon_Id(String gifticonId);
}