package ao.com.angotech.application;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Teste {

    @Operation(summary = "Teste da API", description = "Recurso para testar a API",
        responses = {
            @ApiResponse(responseCode = "200", description = "Operação está funcinal")
        }
    )
    @GetMapping
    public String health() {
        return "Health";
    }
}
