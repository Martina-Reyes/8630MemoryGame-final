package com.snatik.matches.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.snatik.matches.R;
import com.snatik.matches.databinding.DifficultyItemRowBinding;
import com.snatik.matches.themes.Difficulty;

public class DifficultyAdapterJ extends ListAdapter<Difficulty, RecyclerView.ViewHolder> {
    private final OnItemClickListener listener;

    private Display display;

    public DifficultyAdapterJ(OnItemClickListener listener,
                              Display display) {
        super(new GameDiffUtil());
        this.listener = listener;
        this.display = display;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return GameViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Difficulty item = getItem(position);
        GameViewHolder gameViewHolder = (GameViewHolder) holder;
        gameViewHolder.bind(item, listener,display);
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final DifficultyItemRowBinding binding;

        private GameViewHolder(DifficultyItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Difficulty gameElement, OnItemClickListener listener, Display display) {

            //SET DYNAMIC SIZE ACCORDING TO SCREEN SIZE:
            int width = display.getWidth();
            int height = display.getHeight();

            binding.allRv.setLayoutParams(new ViewGroup.LayoutParams(
                    width/4,
                    (int) (height/2.5)
            ));



            binding.setElement(gameElement);
            binding.setListener(listener);
            animate(binding.allRv);
            int highStarsOriginal = gameElement.getHighStars();//3 STARTS


            //DRAW STARTS ONLY FOR HIGHEST SCORE OTHERWISE DRAW JUST BG
            for (int i = 0; i < highStarsOriginal; i++) {
                createStars(binding.startsLn, true,display);
            }

            for (int i = 0; i < 3 - highStarsOriginal; i++) {
                createStars(binding.startsLn, false, display);
            }


            binding.executePendingBindings();
        }

        static GameViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            DifficultyItemRowBinding binding = DifficultyItemRowBinding.inflate(layoutInflater, parent, false);
            return new GameViewHolder(binding);
        }

        private void animate(View... view) {
            AnimatorSet animatorSet = new AnimatorSet();
            AnimatorSet.Builder builder = animatorSet.play(new AnimatorSet());
            for (int i = 0; i < view.length; i++) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(view[i], "scaleX", 0.8f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(view[i], "scaleY", 0.8f, 1f);
                builder.with(scaleX).with(scaleY);
            }
            animatorSet.setDuration(500);
            animatorSet.setInterpolator(new BounceInterpolator());
            animatorSet.start();
        }


        private static void createStars(LinearLayout startsLn, boolean hasSrc, Display display) {
            ImageView startImg = new ImageView(startsLn.getContext());
            View paddingV = new View(startsLn.getContext());

            if (hasSrc) {
                Log.d("hasSrc", "hasSrc");
                startImg.setImageResource(R.drawable.a_level_complete_star);
            }
            startImg.setBackgroundResource(R.drawable.a_won_small_circle);
            startImg.setLayoutParams(new LinearLayout.LayoutParams(
                    display.getWidth()/22,//60
                    display.getWidth()/22//60
            ));

            paddingV.setLayoutParams(new ViewGroup.LayoutParams(
                    14,
                    14
            ));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            params.setMargins(7, 7, 7, 7);
            startsLn.setLayoutParams(params);

            startsLn.setPadding(5, 5, 0, 0);
            startsLn.addView(startImg);
            startsLn.addView(paddingV);
        }
    }


    static class GameDiffUtil extends DiffUtil.ItemCallback<Difficulty> {
        @Override
        public boolean areItemsTheSame(@NonNull Difficulty oldItem, @NonNull Difficulty newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Difficulty oldItem, @NonNull Difficulty newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

    public interface OnItemClickListener {
        void onClick(Difficulty gameElement);
    }
}