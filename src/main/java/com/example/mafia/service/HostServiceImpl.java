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

import java.util.List;

@RequiredArgsConstructor
@Service
@IntegrationComponentScan
@PropertySource("classpath:application.yml")
public class HostServiceImpl implements HostService{
    private final Play play;
    private Game game;
    private final SubscribableChannel deadChannel;
    private final SubscribableChannel savedChannel;

    private MessageHandler deadMessageHandler;
    private MessageHandler savedMessageHandler;

    @Value("${initial-names}")
    private final List<String> initialNames;

    @Override
    public void startPlay() throws InterruptedException {
        game = new Game(play.generateCitizens(initialNames));
        Thread.sleep(100);
        System.out.println("*************************NEW_GAME******************************");
        deadMessageHandler = createDeadMessageHandler(game);
        savedMessageHandler = createSavedMessageHandler();
        deadChannel.subscribe(deadMessageHandler);
        savedChannel.subscribe(savedMessageHandler);
        startNight();
    }
    private void startNight(){
        System.out.println("=============== Ночь " + game.getNightCount() + " ===============");
        System.out.println("Роли: " + game.getCitizens());
        System.out.println("Город засыпает...");
        System.out.println("Просыпается мафия. Мафия делает свой выбор. ");
        String dead = play.kill(game);
        System.out.println("*выбор мафии пал на " + dead + "*");
        System.out.println("Мафия засыпает.") ;
        System.out.println("Просыпается докор. Доктор делает свой выбор. " + (game.getPreviousCured() == null ?
                "" : "Напомню, прошлый выбор - " + game.getPreviousCured()));
        String saved = play.cure(game);
        System.out.println("*выбор доктора пал на " + saved + "*");
        play.distributeResult(new String[]{dead, saved});
        game.setNightCount(1 + game.getNightCount());

    }

    private boolean isGameOver(){
        return game.getCitizens().stream().noneMatch(c -> c.getRole().equals(Role.DOCTOR));
    }
    private MessageHandler createDeadMessageHandler(Game game){
        return (m) -> {
            List<Citizen> citizens = game.getCitizens();
            citizens.remove(citizens.stream().filter(c -> c.getName().equals(m.getPayload())).findAny()
                    .orElseThrow(() -> new RuntimeException("Dead was not found")));
            game.setCitizens(citizens);
            System.err.println(m.getPayload() + " был убит этой ночью");
            callStartNight();
        };
    }
    private MessageHandler createSavedMessageHandler(){
        return (m) -> {
            System.err.println(m.getPayload() + " чудом выжил этой ночью");
            callStartNight();
        };
    }
    private void callStartNight(){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!isGameOver()) startNight();
        else{
            deadChannel.unsubscribe(deadMessageHandler);
            savedChannel.unsubscribe(savedMessageHandler);
            System.out.println("Доктор погиб. Игра окончена!");
        }
    }
}
