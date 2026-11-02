package com.zhijiaoyou.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置（替代 springfox+knife4j）
 * 访问地址：http://localhost:9999/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智驾游 - All in one")
                        .version("1.0")
                        .description("智驾游顺风车 API 文档")
                        .contact(new Contact().name("Shawn").email("wangshouwen@itcast.cn")));
    }
}
