package com.example.mafia.service;

import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Role;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomService {

    public static String selectCitizenName(List<String> citizens){
        int index = ThreadLocalRandom.current().nextInt(citizens.size());
        return citizens.get(index);
    }
    public static List<Citizen> generateCitizens(List<String> initialNames){
        List<Citizen> citizens = new ArrayList<>();
        List<String> names = new ArrayList<>(initialNames);
        giveRole(citizens, names, Role.MAFIA);
        giveRole(citizens, names, Role.DOCTOR);
        giveRole(citizens, names, Role.COMMISSIONER);
        names.forEach(n -> citizens.add(Citizen.builder().name(n).role(Role.PEACEFUL_CITIZEN).build()));
        return citizens;
    }
    private static void giveRole(List<Citizen> citizens, List<String> names, Role role){
        int index = ThreadLocalRandom.current().nextInt(names.size());
        citizens.add(Citizen.builder().name(names.get(index)).role(role).build());
        names.remove(index);
    }
}
