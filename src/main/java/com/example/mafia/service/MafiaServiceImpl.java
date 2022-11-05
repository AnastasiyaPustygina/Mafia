package com.example.mafia.service;

import com.example.mafia.Play;
import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import com.example.mafia.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@IntegrationComponentScan
@RequiredArgsConstructor
public class MafiaServiceImpl implements MafiaService{
    private final Play play;
    @Override
    public String kill(Game game) {
        List<Citizen> possibleDead = new ArrayList<>(game.getCitizens());
        possibleDead.remove(possibleDead.stream().filter(c -> c.getRole().equals(Role.MAFIA)).findAny()
                .orElseThrow(() -> new RuntimeException("mafia was not found")));
        return play.selectCitizenName(possibleDead.stream().map(Citizen::getName).toList());
    }
}
