package com.snatik.matches.themes;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import java.util.List;

public class Theme {
    private String title;

    private Drawable gameRes;
    private @DrawableRes int gameBg;

    private int id;
    private List<String> tileImageUrls;


    public Theme() {
    }

    public Theme(
            int id,
            List<String> tileImageUrls,

            String title,
            Drawable gameRes,
            @DrawableRes int gameBg
    ) {


        this.id = id;
        this.tileImageUrls = tileImageUrls;

        this.title = title;
        this.gameRes = gameRes;
        this.gameBg = gameBg;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getGameRes() {
        return gameRes;
    }

    public void setGameRes(Drawable gameRes) {
        this.gameRes = gameRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public List<String> getTileImageUrls() {
        return tileImageUrls;
    }

    public void setTileImageUrls(List<String> tileImageUrls) {
        this.tileImageUrls = tileImageUrls;
    }

    public int getGameBg() {
        return gameBg;
    }

    public void setGameBg(int gameBg) {
        this.gameBg = gameBg;
    }
}
