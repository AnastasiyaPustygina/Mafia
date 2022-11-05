package com.example.mafia.service;

import com.example.mafia.Play;
import com.example.mafia.domain.Citizen;
import com.example.mafia.domain.Game;
import com.example.mafia.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@IntegrationComponentScan
public class HostServiceImpl implements HostService{

    private final MafiaService mafiaService;
    private final DoctorService doctorService;
    private final Play play;

    private MessageHandler deadMessageHandler;

    private MessageHandler savedMessageHandler;

    private final SubscribableChannel deadChannel;

    private final SubscribableChannel savedChannel;

    @Override
    public void startPlay() throws InterruptedException {
        System.out.println("*************************NEW_GAME******************************");
        Game game = new Game(RandomServiceImpl.generateCitizens());
        game.setNightCount(1);
        deadMessageHandler = (m) -> {
            List<Citizen> citizens = game.getCitizens();
            citizens.remove(citizens.stream().filter(c -> c.getName().equals(m.getPayload())).findAny()
                    .orElseThrow(() -> new RuntimeException("Dead was not found")));
            System.err.println(m.getPayload() + " был убит этой ночью");
        };
        savedMessageHandler = (m) -> System.err.println( m.getPayload() + " чудом выжил этой ночью");
        startNight(game);
    }
    private void startNight(Game game) throws InterruptedException {
        System.out.println("=============== Ночь " + game.getNightCount() + " ===============");
        System.out.println("Роли: " + game.getCitizens());
        System.out.println("Город засыпает...");
        System.out.println("Просыпается мафия. Мафия делает свой выбор. ");
        String dead = mafiaService.kill(game);
        System.out.println("*выбор мафии пал на " + dead + "*");
        System.out.println("Мафия засыпает.") ;
        System.out.println("Просыпается докор. Доктор делает свой выбор. " + (game.getPreviousCured() == null ?
                "" : "Напомню, прошлый выбор - " + game.getPreviousCured()));
        String saved = doctorService.cure(game);
        System.out.println("*выбор доктора пал на " + saved + "*");
        deadChannel.subscribe(deadMessageHandler);
        savedChannel.subscribe(savedMessageHandler);
        play.distributeResult(new String[]{dead, saved});
        game.setNightCount(1 + game.getNightCount());
        Thread.sleep(1000);
        if(!isGameOver(game)) startNight(game);
    }

    private boolean isGameOver(Game game){
        return game.getCitizens().stream().noneMatch(c -> c.getRole().equals(Role.DOCTOR));
    }
}
