
package com.mirvahidagha.betterbet.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mirvahidagha.betterbet.Dao.QuranRepository;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;

import java.util.ArrayList;
import java.util.List;

public class AyahViewModel extends AndroidViewModel {

    private QuranRepository repository;

    public AyahViewModel(@NonNull Application application) {
        super(application);
        repository = new QuranRepository(application);

    }

    public void update(Ayah ayah) {
        repository.update(ayah);
    }

    public LiveData<List<Ayah>> getStarredAyahs() {
        LiveData<List<Ayah>> ayahs = repository.getStarredAyahs();
        return ayahs;
    }

    public LiveData<List<Ayah>> getAyahs(int surahId) {
        LiveData<List<Ayah>> ayahs = repository.getAllAyahs(surahId);
        return ayahs;
    }

    public ArrayList<LiveData<AyahContent>> getAyahContent(ArrayList<String> tableName, int surah, int ayah) {
        ArrayList<LiveData<AyahContent>> listOthers = new ArrayList<>();

        for (String table : tableName) {
            listOthers.add(repository.getAyahContent(table, surah, ayah));
        }

        return listOthers;
    }


    public LiveData<AyahContent> getAyahContent(String tableName, int surah, int ayah) {

        return repository.getAyahContent(tableName, surah, ayah);
    }

    public LiveData<List<AyahContent>>getAllAyahContent(String table){
        return repository.getAllAyahContent(table);
    }

    public LiveData<List<Ayah>> getAllAyahs() {
        return repository.getAllAyahs();
    }
}
