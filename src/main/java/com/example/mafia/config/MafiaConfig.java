package com.example.mafia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public IntegrationFlow generateCitizensFlow(){
        return IntegrationFlows.from("generateCitizensChannel")
                .handle("randomService", "generateCitizens").get();
    }

    //Т.к. в дз был пункт про использование subFlow
    @Bean
    public IntegrationFlow distributeResultFlow(){
        return IntegrationFlows.from("distributeResultChannel").<String[], Boolean>route(
                (arr) -> arr[0].equals(arr[1]), m -> m.subFlowMapping(true, sf -> sf.transform(arr ->
                        ((String[]) arr)[0]).channel(
                        "savedChannel")).subFlowMapping(false, sf -> sf.transform(arr ->
                        ((String[]) arr)[0]).channel("deadChannel"))).get();
    }

    @Bean
    public SubscribableChannel deadChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public SubscribableChannel savedChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public PollableChannel generateCitizensChannel(){
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PollableChannel distributeResultChannel(){
        return MessageChannels.queue(10).get();
    }

}
