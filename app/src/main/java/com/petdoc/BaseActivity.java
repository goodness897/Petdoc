package com.petdoc;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by STU on 2016-05-30.
 */
public class BaseActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumGothicBold.otf"))
                .addItalic(Typekit.createFromAsset(this, "NanumGothic.otf"))
                .addBoldItalic(Typekit.createFromAsset(this, "NanumGothic.otf"))
                .addCustom1(Typekit.createFromAsset(this, "NanumGothic.ttf"))
                .addCustom2(Typekit.createFromAsset(this, "NanumGothic.ttf"));
    }
}
