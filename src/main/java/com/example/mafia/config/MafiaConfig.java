package com.example.mafia.config;

import com.example.mafia.domain.Citizen;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.*;

import java.util.List;

@Configuration
public class MafiaConfig {

    @Bean
    public IntegrationFlow selectCitizenName(){
        return IntegrationFlows.from("selectChannel")
                .handle("randomServiceImpl", "selectCitizenName").get();
    }
    @Bean
    public IntegrationFlow kill(){
        return IntegrationFlows.from("killChannel")
                .handle("mafiaServiceImpl", "kill").get();
    }
    @Bean
    public IntegrationFlow cure(){
        return IntegrationFlows.from("cureChannel")
                .handle("doctorServiceImpl", "cure").get();
    }

    //Т.к. в дз был пункт про использование subFlow
    @Bean
    public IntegrationFlow distributeResult(){
        return IntegrationFlows.from("distributeResultChannel").<String[], Boolean>route(
                (arr) -> arr[0].equals(arr[1]), m -> m.subFlowMapping(true, sf -> sf.transform(arr ->
                        ((String[]) arr)[0]).channel(
                        "savedChannel")).subFlowMapping(false, sf -> sf.transform(arr ->
                        ((String[]) arr)[0]).channel("deadChannel"))).get();
    }

    @Bean
    public SubscribableChannel deadChannel(){
        return new DirectChannel();
    }
    @Bean
    public SubscribableChannel savedChannel(){
        return new DirectChannel();
    }

    @Bean
    public PollableChannel distributeResultChannel(){
        return new QueueChannel();
    }

}
