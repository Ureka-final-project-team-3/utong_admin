package ureka.team3.utong_admin.code.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ureka.team3.utong_admin.code.entity.Code;
import ureka.team3.utong_admin.code.entity.CodeKey;

@Repository
public interface CodeRepository extends JpaRepository<Code, CodeKey> {

    @Query("select c from Code c where c.codeKey.groupCode = :groupCode order by c.orderNo")
    Page<Code> findByGroupCode(@Param("groupCode") String groupCode, Pageable pageable);

}
