package com.example.mafia;

import com.example.mafia.service.HostService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.DirectChannel;

@SpringBootApplication
public class MafiaApplication {

	public static void main(String[] args) throws InterruptedException {

		ConfigurableApplicationContext run = SpringApplication.run(MafiaApplication.class, args);
		while (true) {
			run.getBean(HostService.class).startPlay();
		}
	}

}
