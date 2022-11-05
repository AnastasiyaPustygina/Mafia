package com.example.mafia.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Builder
@ToString
@Getter
public class Citizen {

    private final String name;

    private final Role role;

}
