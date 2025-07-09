package ureka.team3.utong_admin.groupcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ureka.team3.utong_admin.groupcode.entity.GroupCode;

@Repository
public interface GroupCodeRepository extends JpaRepository<GroupCode, String> {

    
}
