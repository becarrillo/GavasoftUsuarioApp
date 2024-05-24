package com.microservices.usuarioapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Brando Elí Carrillo Pérez
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UsuarioappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuarioappApplication.class, args);
	}
}
