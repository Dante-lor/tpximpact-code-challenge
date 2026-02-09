package com.tpximpact.shortenerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entrypoint for a spring boot application.
 */
@SpringBootApplication
public class ShortenerServiceApplication {

	/**
	 * Run the app.
	 *
	 * @param args CLI args.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ShortenerServiceApplication.class, args);
	}

}
