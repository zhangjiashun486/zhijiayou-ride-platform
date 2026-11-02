package com.zhijiaoyou.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.InetAddress;
import java.util.regex.Pattern;

@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    final static Logger logger = LoggerFactory.getLogger(MinioConfig.class);
    private static final Pattern IP_PATTERN =
            Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");

    private static String host;
    private static String port;
    private static String bucket;
    private static String username;
    private static String password;
    private static String url;

    @Bean
    @Lazy
    public MinioClient minioClient() throws Exception {
        if (!IP_PATTERN.matcher(host).matches()) {
            logger.warn("MinIO: host is not an IP, resolving...");
            InetAddress inetAddress = InetAddress.getByName(host);
            host = inetAddress.getHostAddress();
            logger.info("MinIO: host resolved to {}", host);
        }
        MinioClient minioClient = MinioClient.builder()
                .endpoint(host, Integer.parseInt(port), false)
                .credentials(username, password)
                .build();

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        logger.info("MinIO: bucket init ok, bucket={}", bucket);
        return minioClient;
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getBucket() { return bucket; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
    public static String getUrl() { return url; }

    public void setHost(String host) { MinioConfig.host = host; }
    public void setPort(String port) { MinioConfig.port = port; }
    public void setBucket(String bucket) { MinioConfig.bucket = bucket; }
    public void setUsername(String username) { MinioConfig.username = username; }
    public void setPassword(String password) { MinioConfig.password = password; }
    public void setUrl(String url) { MinioConfig.url = url; }
}
