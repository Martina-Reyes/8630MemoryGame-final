package com.snatik.matches.themes;

import android.graphics.Bitmap;

import com.snatik.matches.utils.Utils;

public class Themes {


    public static String URI_DRAWABLE = "drawable://";


    public static Bitmap getBackgroundImage(Theme theme) {
        int drawableResourceId = theme.getGameBg();
        return Utils.scaleDown(drawableResourceId, Utils.screenWidth(), Utils.screenHeight());
    }

}
