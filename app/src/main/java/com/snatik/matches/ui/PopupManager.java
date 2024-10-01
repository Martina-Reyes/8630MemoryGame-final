package com.snatik.matches.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.snatik.matches.R;
import com.snatik.matches.common.Music;
import com.snatik.matches.common.Shared;
import com.snatik.matches.databinding.PopupSettingsViewBinding;
import com.snatik.matches.databinding.PopupWonViewBinding;
import com.snatik.matches.events.ui.BackGameEvent;
import com.snatik.matches.events.ui.NextGameEvent;
import com.snatik.matches.model.GameState;
import com.snatik.matches.utils.Clock;

public class PopupManager {

    private static PopupSettingsViewBinding popupSettingsViewBinding;
    private static PopupWonViewBinding popupWonViewBinding;
    private static AlertDialog settingAlertDialog;
    private static AlertDialog wonAlertDialog;


    private static Handler mHandler;

    public static void showSettingsAlert(Context context) {

        popupSettingsViewBinding = PopupSettingsViewBinding
                .inflate(LayoutInflater.from(context));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupSettingsViewBinding.getRoot());

        settingAlertDialog = builder.create();

        if (settingAlertDialog.getWindow() != null) {
            settingAlertDialog.show();
            settingAlertDialog.getWindow().setLayout(
                    context.getResources().getDimensionPixelSize(R.dimen.popup_settings_width),
                    context.getResources().getDimensionPixelSize(R.dimen.popup_settings_height)
            );
            settingAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            showAnimations(popupSettingsViewBinding);
            popupSettingsViewBinding.soundOff.setOnClickListener(v -> {
                Music.OFF = !Music.OFF;
                setMusicButton();
            });

            popupSettingsViewBinding.rate.setOnClickListener(v -> {
                final String appPackageName = Shared.context.getPackageName();
                try {
                    Shared.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    Shared.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            });
            setMusicButton();
        }

    }


    @SuppressLint("SetTextI18n")
    private static void setMusicButton() {
        if (Music.OFF) {
            popupSettingsViewBinding.soundOffText.setText(R.string.soundoff);
            popupSettingsViewBinding.soundImage.setImageResource(R.drawable.button_music_off);
        } else {
            popupSettingsViewBinding.soundOffText.setText(R.string.soundon);
            popupSettingsViewBinding.soundImage.setImageResource(R.drawable.button_music_on);
        }
    }


    public static void showPopupWon(Context context, GameState gameState) {

        popupWonViewBinding = PopupWonViewBinding
                .inflate(LayoutInflater.from(context));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupWonViewBinding.getRoot());

        wonAlertDialog = builder.create();

        if (wonAlertDialog.getWindow() != null) {
            wonAlertDialog.show();
            wonAlertDialog.getWindow().setLayout(
                    context.getResources().getDimensionPixelSize(R.dimen.popup_won_width),
                    context.getResources().getDimensionPixelSize(R.dimen.popup_won_height)
            );
            wonAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            wonAlertDialog.setCancelable(false);
            showAnimations(popupWonViewBinding);


        }


        int min = gameState.remainedSeconds / 60;
        int sec = gameState.remainedSeconds - min * 60;
        popupWonViewBinding.timeBarText.setText(" " + String.format("%02d", min) + ":" + String.format("%02d", sec));
        popupWonViewBinding.scoreBarText.setText("" + 0);

        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            animateScoreAndTime(popupWonViewBinding, gameState.remainedSeconds, gameState.achievedScore);


            int animationDuration = 600;
            int startWidth;

            startWidth = context.getResources().getDimensionPixelSize(R.dimen.popup_button_star_size);

            for (int i = 0; i < gameState.achievedStars; i++) {
                createStars(context, animationDuration * i,
                        startWidth
                );
            }

        }, 500);

        popupWonViewBinding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wonAlertDialog.isShowing()) {
                    wonAlertDialog.dismiss();
                }

                Shared.eventBus.notify(new BackGameEvent());
            }
        });

        popupWonViewBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wonAlertDialog.isShowing()) {
                    wonAlertDialog.dismiss();
                }

                Shared.eventBus.notify(new NextGameEvent());
            }
        });

    }

    private static void createStars(Context context, int animationDuration, int startWidth) {
        ImageView startImg = new ImageView(context);
        startImg.setImageResource(R.drawable.a_level_complete_star);
        startImg.setBackgroundResource(R.drawable.a_won_small_circle);

        startImg.setLayoutParams(new LinearLayout.LayoutParams(
                startWidth,
                startWidth
        ));

        startImg.setAlpha(0f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(10, 10, 10, 10);
        popupWonViewBinding.startsLn.setLayoutParams(params);
        popupWonViewBinding.startsLn.addView(startImg);

        animateStar(startImg, animationDuration);
    }

    private static void animateScoreAndTime(PopupWonViewBinding popupWonViewBinding, final int remainedSeconds, final int achievedScore) {
        final int totalAnimation = 1200;

        Clock.getInstance().startTimer(totalAnimation, 35, new Clock.OnTimerCount() {

            @Override
            public void onTick(long millisUntilFinished) {
                float factor = millisUntilFinished / (totalAnimation * 1f); // 0.1
                int scoreToShow = achievedScore - (int) (achievedScore * factor);
                int timeToShow = (int) (remainedSeconds * factor);
                int min = timeToShow / 60;
                int sec = timeToShow - min * 60;
                popupWonViewBinding.timeBarText.setText(" " + String.format("%02d", min) + ":" + String.format("%02d", sec));
                popupWonViewBinding.scoreBarText.setText("" + scoreToShow);
            }

            @Override
            public void onFinish() {
                popupWonViewBinding.timeBarText.setText(" " + String.format("%02d", 0) + ":" + String.format("%02d", 0));
                popupWonViewBinding.scoreBarText.setText("" + achievedScore);
            }
        });

    }


    private static void animateStar(final View view, int delay) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1f);
        alpha.setDuration(100);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alpha, scaleX, scaleY);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setStartDelay(delay);
        animatorSet.setDuration(600);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Music.showStar();
            }
        }, delay);
    }


    //MUTUAL
    private static void showAnimations(ViewBinding popupSettingsViewBinding) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(popupSettingsViewBinding.getRoot(), "scaleX", 0f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(popupSettingsViewBinding.getRoot(), "scaleY", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        animatorSet.start();
    }


    public static void closePopup() {
        final RelativeLayout popupContainer = Shared.activity.findViewById(R.id.popup_container);
        int childCount = popupContainer.getChildCount();
        if (childCount > 0) {
            View background = null;
            View viewPopup = null;
            if (childCount == 1) {
                viewPopup = popupContainer.getChildAt(0);
            } else {
                background = popupContainer.getChildAt(0);
                viewPopup = popupContainer.getChildAt(1);
            }

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleX", 0f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(viewPopup, "scaleY", 0f);
            if (childCount > 1) {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(background, "alpha", 0f);
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
            } else {
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            }
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateInterpolator(2));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    popupContainer.removeAllViews();
                }
            });
            animatorSet.start();
        }


        if (settingAlertDialog != null) {
            if (settingAlertDialog.isShowing()) {
                settingAlertDialog.dismiss();
            }
        }
    }

    public static boolean isShown() {
        if (settingAlertDialog != null) {
            return settingAlertDialog.isShowing();
        }
        return false;
    }
}
