package com.zhijiaoyou;

import com.zhijiaoyou.commons.initial.annotation.EnableRequestInital;
import com.zhijiaoyou.commons.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableRequestInital
@MapperScan({"com.zhijiaoyou.storage.mapper", "com.zhijiaoyou.aichat.mapper"})
@Import(SpringUtil.class)
@EnableCaching
@EnableTransactionManagement
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
@ServletComponentScan
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
