package com.example.mafia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.*;

@Configuration
public class MafiaConfig {

    @Bean
    public IntegrationFlow selectCitizenNameFlow(){
        return IntegrationFlows.from("selectChannel")
                .handle("randomService", "selectCitizenName").get();
    }
    @Bean
    public IntegrationFlow killFlow(){
        return IntegrationFlows.from("killChannel")
                .handle("mafiaServiceImpl", "kill").get();
    }
    @Bean
    public IntegrationFlow cureFlow(){
        return IntegrationFlows.from("cureChannel")
                .handle("doctorServiceImpl", "cure").get();
    }

    @Bean
    public IntegrationFlow checkFlow(){
        return IntegrationFlows.from("checkChannel")
                .handle("commissionerServiceImpl", "check")
                .handle("hostServiceImpl", "handleCommissionCheck")
                .handle("hostServiceImpl", "isMafia")
                .route(this::route).get();
    }

    @Bean
    public IntegrationFlow generateCitizensFlow(){
        return IntegrationFlows.from("generateCitizensChannel")
                .handle("randomService", "generateCitizens").get();
    }

    @Bean
    public SubscribableChannel mafiaFoundChannel(){
        return MessageChannels.publishSubscribe()
                .minSubscribers(0).get();
    }
    @Bean
    public SubscribableChannel negativeCheckResultChannel(){
        return MessageChannels.publishSubscribe().minSubscribers(0).get();
    }
    @Bean
    public PollableChannel generateCitizensChannel(){
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PollableChannel checkChannel(){
        return MessageChannels.queue(10).get();
    }

    @Router
    public String route(Object payload){
        return (Boolean) payload ? "mafiaFoundChannel" : "negativeCheckResultChannel";
    }

}
