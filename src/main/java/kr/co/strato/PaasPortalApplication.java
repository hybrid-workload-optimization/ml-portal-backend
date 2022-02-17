package kr.co.strato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PaasPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaasPortalApplication.class, args);
    }

}
