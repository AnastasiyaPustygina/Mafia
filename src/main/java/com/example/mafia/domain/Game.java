package com.example.mafia.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Game {
    private List<Citizen> citizens;

    private boolean isMafiaFound = false;
    private boolean isDoctorKilled = false;
    private boolean isCommissionerKilled = false;

    private String previousCured;

    private int nightCount = 1;
    private List<String> uncheckedCitizenNames;

    public Game(List<Citizen> citizens) {
        this.citizens = citizens;
        uncheckedCitizenNames = citizens.stream().filter(c -> !c.getRole().equals(Role.COMMISSIONER))
                .map(Citizen::getName).toList();
    }
}
