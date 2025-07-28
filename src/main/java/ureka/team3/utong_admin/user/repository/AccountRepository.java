package ureka.team3.utong_admin.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ureka.team3.utong_admin.user.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT DISTINCT a FROM Account a " +
           "LEFT JOIN FETCH a.user u " +
           "LEFT JOIN FETCH u.lines l " +
           "LEFT JOIN FETCH l.plan p")
    Page<Account> findAllWithUserAndLines(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Account a " +
           "LEFT JOIN FETCH a.user u " +
           "LEFT JOIN FETCH u.lines l " +
           "LEFT JOIN FETCH l.plan p " +
           "WHERE a.id = :id")
    Optional<Account> findByIdWithUserAndLines(@Param("id") String id);

    @Query("SELECT DISTINCT a FROM Account a " +
           "LEFT JOIN FETCH a.user u " +
           "LEFT JOIN FETCH u.lines l " +
           "LEFT JOIN FETCH l.plan p " +
           "WHERE a.email LIKE %:email% OR a.nickname LIKE %:nickname%")
    Page<Account> findByEmailContainingOrNicknameContaining(
            @Param("email") String email, 
            @Param("nickname") String nickname, 
            Pageable pageable);
}