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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snatik.matches.R;
import com.snatik.matches.adapters.DifficultyAdapterJ;
import com.snatik.matches.common.Memory;
import com.snatik.matches.common.Shared;
import com.snatik.matches.databinding.DifficultySelectFragmentBinding;
import com.snatik.matches.events.ui.DifficultySelectedEvent;
import com.snatik.matches.themes.Difficulty;
import com.snatik.matches.themes.Theme;

import java.util.ArrayList;

public class DifficultySelectFragment extends Fragment {


    private DifficultySelectFragmentBinding binding;
    private DifficultyAdapterJ adapter;
    private ArrayList<Difficulty> difficultyArrayList = new ArrayList<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DifficultySelectFragmentBinding.inflate(inflater, container,
                false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Theme theme = Shared.engine.getSelectedTheme();

        for (int i = 1; i <= 6; i++) {//6 LEVELS
            Difficulty difficulty = new Difficulty(
                    i,
                    Memory.getHighStars(theme.getId(), i),
                    getBestTimeForStage(theme.getId(), i),
                    this

            );

            difficultyArrayList.add(difficulty);
        }

        //SET DYNAMIC SIZE ACCORDING TO SCREEN SIZE:
        Display display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = requireContext()
                    .getDisplay();
        }else {
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }


        adapter = new DifficultyAdapterJ(new DifficultyAdapterJ.OnItemClickListener() {
            @Override
            public void onClick(@NonNull Difficulty gameElement) {
                Shared.eventBus.notify(new DifficultySelectedEvent(gameElement.getId()));

            }
        },display);

        adapter.submitList(difficultyArrayList);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
                3);


        binding.gameRv.setLayoutManager(mLayoutManager);
        binding.gameRv.setAdapter(adapter);

    }


    private String getBestTimeForStage(int theme, int difficulty) {
        int bestTime = Memory.getBestTime(theme, difficulty);
        if (bestTime != -1) {
            int minutes = (bestTime % 3600) / 60;
            int seconds = (bestTime) % 60;
            return String.format(getString(R.string.best), minutes, seconds);
        } else {
            return getString(R.string.best_dash);
        }
    }


}
