package com.example.mafia.service;

import com.example.mafia.Play;
import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionerServiceImpl implements CommissionerService{

    private final Play play;

    @Override
    public String check(Game game){
        return play.selectCitizenName(game.getUncheckedCitizenNames());
    }
}
