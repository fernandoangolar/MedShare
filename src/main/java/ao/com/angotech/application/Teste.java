package ao.com.angotech.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Teste {

    @GetMapping
    public String health() {
        return "Health";
    }
}
