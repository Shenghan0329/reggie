package com.John.reggie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@MapperScan("com.John.reggie.mapper")
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
@SpringBootApplication
public class ReggieApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReggieApplication.class, args);
		log.info("Application start success");
	}

}
