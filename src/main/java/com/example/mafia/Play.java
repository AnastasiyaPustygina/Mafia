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
    String kill(Game game);
    @Gateway(requestChannel = "cureChannel")
    String cure(Game game);
    @Gateway(requestChannel = "checkChannel")
    void check(Game game);
    @Gateway(requestChannel = "generateCitizensChannel")
    List<Citizen> generateCitizens(List<String> names);
}
