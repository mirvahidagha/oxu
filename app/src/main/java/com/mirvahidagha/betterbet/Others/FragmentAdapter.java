package com.mirvahidagha.betterbet.Others;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    FragmentManager fm;
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> fragmentTitles = new ArrayList<>();
    private String[] tags = {"subjects", "search", "sura", "star", "listen"};

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        FragmentTransaction ft = fm.beginTransaction();

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

//    public void removeAyahFragmentFromFragmentManager(String tag, Fragment fragmentLoading) {
//        FragmentTransaction trans = fm.beginTransaction();
//        Fragment fragment1 = fm.findFragmentByTag("ayah");
//        Fragment fragment2 = fm.findFragmentByTag("sura");
//        if (fragment1 != null) trans.remove(fragment1);
//        if (fragment2 != null) trans.detach(fragment2);
//        trans.commitNow();
//        updateFragment(fragmentLoading, tag);
//    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        if (fragments.indexOf(object) == -1)
            return POSITION_NONE;
        return fragments.indexOf((Fragment) object);

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    public void setFragments(ArrayList<Fragment> list) {
        fragments = list;
        notifyDataSetChanged();
    }

    public void updateFragment( String tag, Fragment fragment) {
        tags[2] = tag;
        fragments.set(2, fragment);
        notifyDataSetChanged();
    }

}
