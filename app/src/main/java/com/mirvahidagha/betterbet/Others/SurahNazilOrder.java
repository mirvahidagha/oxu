package com.mirvahidagha.betterbet.Others;

import com.mirvahidagha.betterbet.Entities.Surah;

import java.util.Comparator;

public class SurahNazilOrder implements Comparator<Surah> {
    @Override
    public int compare(Surah o1, Surah o2) {

        return o1.getOrder() - o2.getOrder();
    }

}
