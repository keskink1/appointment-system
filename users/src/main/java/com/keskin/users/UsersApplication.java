package com.keskin.users;

import com.keskin.users.infrastructure.persistence.config.JwtConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
		"com.keskin.users",
		"com.keskin.common"
})
@EnableDiscoveryClient
@EnableConfigurationProperties(JwtConfig.class)
@OpenAPIDefinition(
		info = @Info(
				title = "Application to manage user appointments",
				version = "1.0",
				description = "User Management Service"
		)
)
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
