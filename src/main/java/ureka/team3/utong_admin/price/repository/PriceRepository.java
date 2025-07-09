package ureka.team3.utong_admin.price.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ureka.team3.utong_admin.price.entity.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {

}
