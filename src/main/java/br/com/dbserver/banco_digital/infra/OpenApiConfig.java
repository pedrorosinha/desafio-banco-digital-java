package br.com.dbserver.banco_digital.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banco Digital API")
                        .version("1.0.0")
                        .description("API RESTful para simulação de operações essenciais de um banco digital. " +
                                "Possui mecanismos contra condições de corrida e validação estrita de contratos.")
                        .contact(new Contact()
                                .name("Pedro Felipe Rosinha")
                                .email("pedro.rosinha@dbserver.com.br")));
    }
}