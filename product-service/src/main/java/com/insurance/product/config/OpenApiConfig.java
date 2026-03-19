package com.insurance.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String port;

    @Value("${gateway.url:http://localhost:8888}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        String localUrl = "http://localhost:" + port;

        return new OpenAPI()
                .info(new Info()
                        .title("保险产品服务 API")
                        .version("1.0.0")
                        .description("保险产品管理系统RESTful API文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@insurance.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("bearerAuth", securityScheme)
                .servers(List.of(
                        new Server().url(localUrl).description("本地开发环境"),
                        new Server().url(gatewayUrl).description("网关入口")
                ));
    }
}
