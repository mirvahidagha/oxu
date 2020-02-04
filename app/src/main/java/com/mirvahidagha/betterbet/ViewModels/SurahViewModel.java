package com.mirvahidagha.betterbet.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mirvahidagha.betterbet.Dao.QuranRepository;
import com.mirvahidagha.betterbet.Entities.Surah;

import java.util.List;

public class SurahViewModel extends AndroidViewModel {

    private LiveData<List<Surah>> surahs;

    private QuranRepository repository;

    public SurahViewModel(@NonNull Application application) {
        super(application);
        repository = new QuranRepository(application);
        surahs = repository.getSurahs();
    }

    public LiveData<List<Surah>> getSurahs() {
        return surahs;
    }

    public LiveData<Surah> getSurah(int i) {
        return repository.getSurah(i);
    }
}
