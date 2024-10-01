package com.snatik.matches.adapters;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.snatik.matches.R;
import com.snatik.matches.common.Memory;
import com.snatik.matches.databinding.ThemeItemRowBinding;
import com.snatik.matches.themes.Theme;

public class ThemeAdapterJ extends ListAdapter<Theme, RecyclerView.ViewHolder> {
    private final OnItemClickListener listener;
    private Display display;

    public ThemeAdapterJ(OnItemClickListener listener,
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
        Theme item = getItem(position);
        GameViewHolder gameViewHolder = (GameViewHolder) holder;
        gameViewHolder.bind(item, listener,display);
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final ThemeItemRowBinding binding;

        private GameViewHolder(ThemeItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Theme gameElement,
                         OnItemClickListener listener, Display display) {

            //SET DYNAMIC SIZE ACCORDING TO SCREEN SIZE:
            int width = display.getWidth();
            int height = display.getHeight();

            binding.allRv.setLayoutParams(new ViewGroup.LayoutParams(
                    width/3,
                    height/2
            ));


            binding.setElement(gameElement);
            binding.setListener(listener);
            animateShow(binding.allRv);

            binding.themeImg.setBackgroundResource(gameElement.getGameBg());

            int sum = 0;
            for (int difficulty = 1; difficulty <= 6; difficulty++) {
                sum += Memory.getHighStars(gameElement.getId(), difficulty);
            }
            int num = sum / 6;


            //DRAW STARTS ONLY FOR HIGHEST SCORE OTHERWISE DRAW JUST BG
            for (int i = 0; i < num; i++) {
                createStars(binding.startsLn, true,display);
            }

            for (int i = 0; i < 3 - num; i++) {
                createStars(binding.startsLn, false, display);
            }

            binding.executePendingBindings();
        }

        static GameViewHolder from(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ThemeItemRowBinding binding = ThemeItemRowBinding.inflate(layoutInflater, parent, false);
            return new GameViewHolder(binding);
        }

        private void animateShow(View view) {
            ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f);
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animatorScaleX, animatorScaleY);
            animatorSet.setInterpolator(new DecelerateInterpolator(2));
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
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


            startImg.setLayoutParams(new ViewGroup.LayoutParams(
                    display.getWidth()/22,//60
                    display.getWidth()/22//60
            ));

            paddingV.setLayoutParams(new ViewGroup.LayoutParams(
                    14,
                    14
            ));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(7, 7, 7, 7);
            startsLn.setLayoutParams(params);

            startsLn.setPadding(5, 5, 0, 0);
            startsLn.addView(startImg);
            startsLn.addView(paddingV);
        }

    }


    static class GameDiffUtil extends DiffUtil.ItemCallback<Theme> {
        @Override
        public boolean areItemsTheSame(@NonNull Theme oldItem, @NonNull Theme newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Theme oldItem, @NonNull Theme newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

    public interface OnItemClickListener {
        void onClick(Theme gameElement);
    }
}