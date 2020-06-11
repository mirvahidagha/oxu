package com.mirvahidagha.betterbet.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mirvahidagha.betterbet.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0.0");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.splash_logo)
                .addItem(versionElement)
                //.addItem(adsElement)
                .setDescription("Oxu dilimizdə olan bütün Qurani-Kərim tərcümələrini özündə birləşdirən yeganə tətbiqdir. Bu tətbiqlə siz Quranı oxuya, dinləyə və onun müxtəlif tərcümələrini müqayisə edə bilərsiniz.")
                .addGroup("Let's keep in touch")
                .addEmail("kodachi.agency@gmail.com")
                .addFacebook("kodachi.agency")
                .addTwitter("kodachiagency")
                .addYoutube("UCpdIj_B2GruORQE-zz1fAfg")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addInstagram("kodachi.agency")
                .addWebsite("http://mirvahidagha.atwebpages.com/")
                .create();

        setContentView(aboutPage);

    }


}
