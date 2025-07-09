package ureak.team3.utong_admin.auth.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ureak.team3.utong_admin.auth.entity.Admin;
import ureak.team3.utong_admin.auth.repository.AdminRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!adminRepository.existsByEmail("admin@naver.com")) {
            Admin admin = Admin.builder()
                    .id(UUID.randomUUID().toString())
                    .email("admin@naver.com")
                    .password(passwordEncoder.encode("1234"))
                    .build();
            
            adminRepository.save(admin);
            System.out.println("기본 관리자 계정이 생성되었습니다: admin@naver.com / 1234");
        }
    }
}