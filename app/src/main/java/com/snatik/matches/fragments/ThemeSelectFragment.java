package com.snatik.matches.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.snatik.matches.R;
import com.snatik.matches.adapters.ThemeAdapterJ;
import com.snatik.matches.common.Shared;
import com.snatik.matches.databinding.ThemeSelectFragmentBinding;
import com.snatik.matches.events.ui.ThemeSelectedEvent;
import com.snatik.matches.themes.Theme;

import java.util.ArrayList;

public class ThemeSelectFragment extends Fragment {
    private ThemeSelectFragmentBinding binding;
    private ThemeAdapterJ adapter;
    private ArrayList<Theme> themeArrayList = new ArrayList<Theme>();
    private String URI_DRAWABLE = "drawable://";

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ThemeSelectFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeArrayList.add(createAnimalsTheme());
        themeArrayList.add(createMosterTheme());
        themeArrayList.add(createEmojiTheme());

        //SET DYNAMIC SIZE ACCORDING TO SCREEN SIZE:
        Display display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
             display = requireContext()
            .getDisplay();
        }else {
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
             display = windowManager.getDefaultDisplay();
        }


        adapter = new ThemeAdapterJ(new ThemeAdapterJ.OnItemClickListener() {
            @Override
            public void onClick(@NonNull Theme gameElement) {
                Shared.eventBus.notify(new ThemeSelectedEvent(gameElement));

            }
        },display);

        adapter.submitList(themeArrayList);


        binding.gameRv.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));

        binding.gameRv.setAdapter(adapter);
    }


    Theme createAnimalsTheme() {
        ArrayList<String> imageUrls = new ArrayList<String>();
        // 40 drawables
        for (int i = 1; i <= 28; i++) {
            imageUrls.add(URI_DRAWABLE + String.format("animals_%d", i));
        }

        return new Theme(1,
                imageUrls,
                getString(R.string.Theme1),
                ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.animals_16, getContext().getTheme()),

                R.drawable.animals_bg
        );
    }


    Theme createMosterTheme() {

        ArrayList<String> imageUrls = new ArrayList<String>();
        // 40 drawables
        for (int i = 1; i <= 40; i++) {
            imageUrls.add(URI_DRAWABLE + String.format("mosters_%d", i));
        }


        return new Theme(2,
                imageUrls,
                getString(R.string.theme2),
                ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.mosters_12, getContext().getTheme()),
                R.drawable.mosters_bg
        );
    }


    Theme createEmojiTheme() {
        ArrayList<String> imageUrls = new ArrayList<String>();
        // 40 drawables
        for (int i = 1; i <= 40; i++) {
            imageUrls.add(URI_DRAWABLE + String.format("emoji_%d", i));
        }


        return new Theme(3,
                imageUrls,
                getString(R.string.theme3),
                ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.emoji_8, getContext().getTheme()),
                R.drawable.emoji_bg
        );
    }


}
