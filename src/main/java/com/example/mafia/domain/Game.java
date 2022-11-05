package com.example.mafia.domain;

import lombok.Getter;
import lombok.Setter;

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
