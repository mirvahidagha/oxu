package com.mirvahidagha.betterbet.Others;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;

import java.util.ArrayList;
import java.util.List;

public class CombinedLiveData extends MediatorLiveData<Pair<List<Surah>, List<Ayah>>> {
    private List<Surah> surahs;
    private List<Ayah> ayahs;


    public CombinedLiveData(LiveData<List<Surah>> s, LiveData<List<Ayah>> a) {
        setValue(Pair.create(surahs, ayahs));

        addSource(s, (surahs) -> {
            if (surahs!=null)
                this.surahs=surahs;
            setValue(Pair.create(surahs, ayahs));
        });

        addSource(a, (ayahs) -> {
            if (ayahs!=null)
                this.ayahs=ayahs;
            setValue(Pair.create(surahs, ayahs));
        });

    }
}
