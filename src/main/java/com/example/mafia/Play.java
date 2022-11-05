package com.example.mafia;

import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.List;


@MessagingGateway
public interface Play {

    @Gateway(requestChannel = "selectChannel")
    String selectCitizenName(List<String> citizens);

    @Gateway(requestChannel = "killChannel")
    void kill(Game game);

    @Gateway(requestChannel = "cureChannel")
    void cure(Game game);
    @Gateway(requestChannel = "distributeResultChannel")
    void distributeResult(String[] names);
}
