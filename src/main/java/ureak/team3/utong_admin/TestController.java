package ureak.team3.utong_admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String test() {
        return "Hello, this is a test response from the TestController!😀 제발요 please;;";
    }

}
