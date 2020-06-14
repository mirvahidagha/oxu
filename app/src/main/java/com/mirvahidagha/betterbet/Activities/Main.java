package com.mirvahidagha.betterbet.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.FragmentAdapter;
import com.mirvahidagha.betterbet.Others.Just;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.Others.ToolbarSpinnerAdapterer;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.fragments.AyahsFragment;
import com.mirvahidagha.betterbet.fragments.ListenFragment;
import com.mirvahidagha.betterbet.fragments.SubjectsFragment;
import com.mirvahidagha.betterbet.fragments.SearchFragment;
import com.mirvahidagha.betterbet.fragments.SurahsFragment;
import com.mirvahidagha.betterbet.fragments.StarFragment;
import com.mirvahidagha.betterbet.tabs.ntb.NavigationTabBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<String> list;
    SearchView searchView;
    String[] tags = {"subjects", "search", "sura", "star", "listen"};
    String[] tabNames, colors, translations, tableNames;
    private Menu menu;
    ViewPager viewPager;
    public static Typeface bold, regular, light;
    CoordinatorLayout coordinatorLayout;
    int currentPosition = 2;
    TextView title;
    public Toolbar toolbar;
    NavigationTabBar navigation;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean[] checkedItems;
    final ArrayList<TabFragment> fragments = new ArrayList<>();
    FragmentAdapter adapter;
    FragmentManager fm;
    MenuItem menuItem;
    LinearLayout appNameContainer;
    int[] main;
    ToolbarSpinnerAdapterer spinnerAdapter;

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

        fm = getSupportFragmentManager();

        colors = getResources().getStringArray(R.array.rengler);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main);
        coordinatorLayout.setBackgroundColor(Color.parseColor(colors[2]));
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
        regular = Typeface.createFromAsset(getAssets(), "regular.ttf");
        light = Typeface.createFromAsset(getAssets(), "light.ttf");
        tabNames = getResources().getStringArray(R.array.tab_names);
        toolbar = findViewById(R.id.toolbar);
        main = new int[]{pref.getInt("main", 0)};
        AppCompatSpinner spinner = toolbar.findViewById(R.id.spinner);

        spinnerAdapter = new ToolbarSpinnerAdapterer(getApplicationContext(), translations, main[0]);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);


        spinner.setSelection(main[0]);
        title = toolbar.findViewById(R.id.appname);
        appNameContainer= toolbar.findViewById(R.id.appname_container);
        title.setTypeface(bold);
        //   toolbar.inflateMenu(R.menu.menu_search);
        toolbar.setBackgroundColor(Color.parseColor(colors[2]));
        title.setText(tabNames[2]);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();

    }

    private void setFragments() {
        fragments.add(new SubjectsFragment());
        fragments.add(new SearchFragment());
        fragments.add(new SurahsFragment());
        fragments.add(new StarFragment());
        fragments.add(new ListenFragment());
    }

    private void initUI() {
        setFragments();
        adapter = new FragmentAdapter(fm);
        viewPager = findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(adapter);
        adapter.setFragments(fragments);
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
                EventBus.getDefault().post(new Just());
                appNameContainer.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);
                title.setText(tabNames[position]);
                changeBackground(position);

                if (position != 2) navigationTabBar.show();
                if (position == 4) {
                    navigationTabBar.setBehaviorEnabled(false);
                } else {
                    navigationTabBar.setBehaviorEnabled(true);
                }

            }

            @Override
            public void onPageScrollStateChanged(final int state) {


            }
        });

        navigationTabBar.postDelayed(new Runnable() {
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
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

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

        menuItem = menu.findItem(R.id.menu_action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TabFragment tabFragment = (TabFragment) fm.findFragmentByTag(tags[currentPosition]);
                assert tabFragment != null;
                tabFragment.search(newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appNameContainer.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                appNameContainer.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);

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
                coordinatorLayout.setBackgroundColor((int) animator.getAnimatedValue());
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
        tabNames[2] = getResources().getStringArray(R.array.tab_names)[2];
        title.setText(tabNames[currentPosition]);
    }


    @Subscribe
    public void customEventReceived(Integer event) {
        viewPager.setCurrentItem(event, true);
        //  title.setText( tabNames[currentPosition]);
    }

    @Subscribe
    public void customEventReceived(MyData data) {
        viewPager.setCurrentItem(2, true);
        assert getFragmentManager() != null;

        setTitle(data.getSurahId());

        AyahsFragment fragment = (AyahsFragment) fm.findFragmentByTag("ayah");
        if (fragment == null) {
            fragment = (new AyahsFragment());
        }
        adapter.updateFragment("ayah", fragment.getInstance(data.getSurahId(), data.getScrollPosition(), data.getTranstalion()));
        fragment.update(data.getTranstalion());
    }

    @Override
    public void onBackPressed() {

        adapter.updateFragment("sura", (TabFragment) fm.findFragmentByTag("sura"));

    }

    public void setTitle(int i) {
        SurahViewModel viewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        viewModel.getSurah(i).observe(this, new Observer<Surah>() {
            @Override
            public void onChanged(Surah surah) {
                tabNames[2] = surah.getAzeri();
                title.setText(surah.getAzeri());
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, translations[position], Toast.LENGTH_SHORT).show();
        //  main[0]=position;
        editor.putInt("main", position);
        editor.apply();

        spinnerAdapter.setSelected(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}