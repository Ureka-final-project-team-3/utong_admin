package ureak.team3.utong_admin.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ureak.team3.utong_admin.auth.entity.Admin;

@Controller
public class AdminController {

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("로그인 페이지 호출됨");
        return "login"; 
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("대시보드 페이지 호출됨");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Admin admin) {
            model.addAttribute("adminEmail", admin.getEmail());
            System.out.println("관리자 이메일: " + admin.getEmail());
        }
        return "dashboard"; 
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}