package com.example.mafia.service;

import com.example.mafia.Play;
import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final Play play;

    @Override
    public String cure(Game game) {
        List<String> possibleCured = new ArrayList<>(game.getCitizens().stream().map(Citizen::getName).toList());
        String previousCuredCitizen = game.getPreviousCured();
        if(previousCuredCitizen != null)
            possibleCured = possibleCured.stream().filter(c -> !c.equals(previousCuredCitizen)).toList();
        String curedCitizenName = play.selectCitizenName(possibleCured);
        game.setPreviousCured(curedCitizenName);
        return curedCitizenName;
    }

}
