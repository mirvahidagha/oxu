package com.mirvahidagha.betterbet.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.ColorInt;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.dialog.SweetAlertDialog;
import com.mirvahidagha.betterbet.fragments.ListenFragment;
import com.mirvahidagha.betterbet.fragments.LiveFragment;
import com.mirvahidagha.betterbet.fragments.SearchFragment;
import com.mirvahidagha.betterbet.fragments.SurahsFragment;
import com.mirvahidagha.betterbet.fragments.StarFragment;
import com.mirvahidagha.betterbet.tabs.ntb.NavigationTabBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends AppCompatActivity {

    String[] tabNames, colors, translations, tableNames;
    private Menu menu;
    ViewPager viewPager;
    Typeface bold, regular, light;
    CoordinatorLayout coordinatorLayout;
    int currentPosition = 2;
    TextView title;
    Toolbar toolbar;
    NavigationTabBar navigation;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean[] checkedItems;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        pref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
        translations = getResources().getStringArray(R.array.translations);
        checkedItems = getCheckedItems();
        colors = getResources().getStringArray(R.array.tab_colors);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main);
        //  coordinatorLayout.setBackgroundColor(Color.parseColor(colors[2]));
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
        regular = Typeface.createFromAsset(getAssets(), "regular.ttf");
        light = Typeface.createFromAsset(getAssets(), "light.ttf");
        tabNames = getResources().getStringArray(R.array.tab_names);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.appname);
        title.setTypeface(bold);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setBackgroundColor(Color.parseColor(colors[2]));
        title.setText(tabNames[2]);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();

    }

    private void initUI() {

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        final NavigationTabBar navigationTabBar = findViewById(R.id.ntb_horizontal);
        navigation = navigationTabBar;
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        navigationTabBar.setBgColor(Color.parseColor("#2c3e50"));
        navigationTabBar.setActiveColor(Color.WHITE);
        navigationTabBar.setInactiveColor(Color.WHITE);

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_bookmark),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_bookmark_1))
                        .title(tabNames[0])
                        .badgeTitle("2")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_search),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_search_2))
                        .title(tabNames[1])
                        .badgeTitle("4")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_quran),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_quran_2))
                        .title(tabNames[2])
                        .badgeTitle("7")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_star),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_star_2))
                        .title(tabNames[3])
                        .badgeTitle("0")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_audio),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_audio_2))
                        .title(tabNames[4])
                        .build()
        );

        navigationTabBar.setIsBadged(false);
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, currentPosition);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageSelected(final int position) {

                title.setText(tabNames[position]);

                changeBackground(position);

                if (position != 2) navigationTabBar.show();


                AppBarLayout appbar = findViewById(R.id.appbar);

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });

        navigationTabBar.postDelayed(new

                                             Runnable() {
                                                 @Override
                                                 public void run() {
                                                     for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                                                         final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                                                         navigationTabBar.postDelayed(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 model.showBadge();
                                                             }
                                                         }, i * 100);
                                                     }
                                                 }
                                             }, 500);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_translations:
                chooseTranslations();
                break;
            case R.id.choose_main:
                chooseMain();
                break;
            case R.id.about:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseTranslations() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(translations, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });

        builder.setCancelable(false);
        builder.setPositiveButton("Təsdiqlə", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCheckedItems(checkedItems);
            }
        });

        builder.show();
    }

    private void setCheckedItems(boolean[] isChecked) {
        for (int i = 0; i < isChecked.length; i++) {
            editor.putBoolean(Integer.toString(i), isChecked[i]);
        }
        editor.apply();
    }

    private boolean[] getCheckedItems() {
        boolean[] reChecked = new boolean[translations.length];
        for (int i = 0; i < translations.length; i++) {
            reChecked[i] = pref.getBoolean(Integer.toString(i), false);
        }
        return reChecked;
    }


    private void chooseMain() {
        final int[] main = {pref.getInt("main", 0)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setSingleChoiceItems(translations, main[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main[0] = which;
            }
        });

        builder.setCancelable(false);
        builder.setPositiveButton("Təsdiqlə", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putInt("main", main[0]);
                editor.apply();
            }
        });
        builder.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void changeBackground(int position) {

        int colorFrom = Color.parseColor(colors[currentPosition]);
        int colorTo = Color.parseColor(colors[position]);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500);

        colorAnimation.setInterpolator(new DecelerateInterpolator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        int rəngdən = Color.parseColor(colors[currentPosition]);
        int rəngə = Color.parseColor("#ffffff");

        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), rəngdən, rəngə);
        colorAnimation2.setDuration(500);
        colorAnimation2.setInterpolator(new DecelerateInterpolator());
        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                title.setTextColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation.start();
        colorAnimation2.start();
        currentPosition = position;

    }

    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5 ? Color.BLACK : Color.WHITE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void customEventReceived(String event) {
        if (!event.equals("empty"))
            tabNames[2] = event;
        else tabNames = getResources().getStringArray(R.array.tab_names);

        title.setText(tabNames[currentPosition]);
    }

    @Subscribe
    public void customEventReceived(Boolean searchPressed) {
        if (searchPressed)
            title.setVisibility(View.GONE);
        else title.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void customEventReceived(Integer event) {

        viewPager.setCurrentItem(event, true);
        //  title.setText( tabNames[currentPosition]);
    }

    static class FragmentAdapter extends FragmentPagerAdapter {

        FragmentManager fm;

        FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FragmentTransaction ft = fm.beginTransaction();
            String[] tags = {"tag1", "tag2", "tag3", "tag4", "tag5"};

            Fragment fragment = fm.findFragmentByTag(tags[position]);
            if (fragment == null) {
                fragment = getItem(position);
                ft.add(container.getId(), fragment, tags[position]);
            } else {
                ft.attach(fragment);
            }

            ft.commit();

            return fragment;

        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            switch (position) {

                case 0:
                    fragment = new LiveFragment();
                    break;
                case 1:
                    fragment = new SearchFragment();
                    break;
                case 2:
                    fragment = new SurahsFragment();
                    break;
                case 3:
                    fragment = new StarFragment();
                    break;
                case 4:
                    fragment = new ListenFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }

    }

}