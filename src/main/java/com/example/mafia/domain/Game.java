package com.example.mafia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
public class Game {

    private List<Citizen> citizens;

    private String previousCured;

    private int nightCount = 1;

    public Game(List<Citizen> citizens) {
        this.citizens = citizens;
    }
}
