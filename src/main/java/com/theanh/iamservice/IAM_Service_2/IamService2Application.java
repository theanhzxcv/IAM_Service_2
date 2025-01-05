package com.theanh.iamservice.IAM_Service_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IamService2Application {

	public static void main(String[] args) {
		SpringApplication.run(IamService2Application.class, args);
	}

}
