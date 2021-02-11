package com.bbte.styoudent;

import com.bbte.styoudent.service.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class StyoudentApplication {

    public static void main(String[] args) {
        SpringApplication.run(StyoudentApplication.class, args);
    }

}
