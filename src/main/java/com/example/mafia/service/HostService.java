package com.example.mafia.service;

public interface HostService {
    void startPlay() throws InterruptedException;
    boolean isMafia(String name);

    String handleCommissionCheck(String name);
}
