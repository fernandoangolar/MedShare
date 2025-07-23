package ao.com.angotech.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openApi() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("REST API - SPRING MEDSHARE")
                                .description("MedShare é um SaaS que facilita o compartilhamento seguro e anônimo de dados médicos entre pacientes, médicos e pesquisadores, promovendo avanços na saúde pública e na pesquisa médica.")
                                .version("v1")
                                .license(new License().name("Apache 2.0"))
                                .contact(new Contact().name("Fernando Angolar").email("fernandomedshare@gmail.com").url("https://github.com/fernandoangolar"))
                );
    }
}
