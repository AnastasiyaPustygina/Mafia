package com.example.mafia.service;

import com.example.mafia.Play;
import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import com.example.mafia.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@IntegrationComponentScan
@PropertySource("classpath:application.yml")
public class HostServiceImpl implements HostService {
    private final Play play;
    private Game game;
    private final SubscribableChannel mafiaFoundChannel;
    private final SubscribableChannel negativeCheckResultChannel;

    private MessageHandler mafiaFoundMessageHandler;
    private MessageHandler negativeCheckResultMessageHandler;

    @Value("${initial-names}")
    private final List<String> initialNames;

    @Override
    public void startPlay() throws InterruptedException {
        game = new Game(play.generateCitizens(initialNames));
        System.out.println("*************************NEW_GAME******************************");
        createAndSubscribeHandlers();
        startNight();
    }

    @Override
    public boolean isMafia(String name) {
        Optional<Citizen> citizen = game.getCitizens().stream().filter(c -> c.getName()
                .equals(name)).findAny();
        return citizen.map(value -> value.getRole().equals(Role.MAFIA)).orElse(false);
    }

    private void startNight() {
        printBackground();
        String dead = wakeUpMafia();
        String saved = null;
        if (!game.isDoctorKilled()) {
            saved = wakeUpDoctor();
        }
        if(!game.isCommissionerKilled()) {
            wakeUpCommissioner();
        }
        sumUp(saved, dead);
        callStartNight();
    }

    @Override
    public String handleCommissionCheck(String name){
        System.out.println("*выбор комиссара пал на " + name + "*");
        List<String> uncheckedCitizenNames = new ArrayList<>(game.getUncheckedCitizenNames());
        uncheckedCitizenNames.remove(name);
        game.setUncheckedCitizenNames(uncheckedCitizenNames);
        return name;
    }

    private void sumUp(String saved, String dead){
        if (saved != null && saved.equals(dead)) System.err.println(saved + " чудом выжил этой ночью");
        else {
            Citizen citizen = removeCitizenFromList(dead);
            System.err.print(dead + " был убит этой ночью. ");
            switch (citizen.getRole()){
                case PEACEFUL_CITIZEN -> {
                    System.err.println("Он был мирным жителем");
                }
                case DOCTOR -> {
                    game.setDoctorKilled(true);
                    System.err.println("Он был доктором");
                }
                case COMMISSIONER -> {
                    game.setCommissionerKilled(true);
                    System.err.println("Он был комиссаром");
                }
            }
        }
    }

    private boolean isGameOver() {
        return game.isCommissionerKilled() && game.isDoctorKilled() || game.isMafiaFound();
    }

    private MessageHandler createMafiaFoundMessageHandler(Game game) {
        return m -> {
            game.setMafiaFound(true);
            System.err.println("Мафия найдена.");
        };
    }

    private MessageHandler createNegativeCheckResultMessageHandler() {
        return (m) -> {
            System.err.println("Коммисар ошибся.");
        };
    }

    private void callStartNight() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!isGameOver()){
            game.setNightCount(1 + game.getNightCount());
            startNight();
        }
        else {
            mafiaFoundChannel.unsubscribe(mafiaFoundMessageHandler);
            negativeCheckResultChannel.unsubscribe(negativeCheckResultMessageHandler);
            System.err.println("Игра окончена!");
        }
    }
    private void createAndSubscribeHandlers(){
        mafiaFoundMessageHandler = createMafiaFoundMessageHandler(game);
        negativeCheckResultMessageHandler = createNegativeCheckResultMessageHandler();
        mafiaFoundChannel.subscribe(mafiaFoundMessageHandler);
        negativeCheckResultChannel.subscribe(negativeCheckResultMessageHandler);
    }

    private void printBackground(){
        System.out.println("=============== Ночь " + game.getNightCount() + " ===============");
        System.out.println("Роли: " + game.getCitizens());
        System.out.println("Город засыпает...");
    }
    private String wakeUpMafia(){
        System.out.println("Просыпается мафия. Мафия делает свой выбор. ");
        String dead = play.kill(game);
        System.out.println("*выбор мафии пал на " + dead + "*");
        System.out.println("Мафия засыпает.");
        return dead;
    }
    private String wakeUpDoctor(){
        System.out.println("Просыпается доктор. Доктор делает свой выбор. " + (game.getPreviousCured() == null ?
                "" : "Напомню, прошлый выбор - " + game.getPreviousCured()));
        String saved = play.cure(game);
        System.out.println("*выбор доктора пал на " + saved + "*");
        System.out.println("Доктор засыпает.");
        return saved;
    }
    private void wakeUpCommissioner(){
        System.out.println("Просыпается комиссар. Коммисар делает свой выбор.");
        play.check(game);
    }
    private Citizen removeCitizenFromList(String dead){
        List<Citizen> citizens = game.getCitizens();
        Citizen citizen = citizens.stream().filter(c -> c.getName().equals(dead)).findAny()
                .orElseThrow(() -> new RuntimeException("Dead was not found"));
        citizens.remove(citizen);
        List<String> uncheckedCitizenNames = new ArrayList<>(game.getUncheckedCitizenNames());
        uncheckedCitizenNames.remove(dead);
        game.setCitizens(citizens);
        game.setUncheckedCitizenNames(uncheckedCitizenNames);
        return citizen;
    }
}
