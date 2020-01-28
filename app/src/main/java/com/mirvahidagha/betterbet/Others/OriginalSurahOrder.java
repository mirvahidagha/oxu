package com.mirvahidagha.betterbet.Others;

import com.mirvahidagha.betterbet.Entities.Surah;

import java.util.Comparator;

public class OriginalSurahOrder implements Comparator<Surah> {
    @Override
    public int compare(Surah o1, Surah o2) {
        return o1.getId()-o2.getId();
    }
}
