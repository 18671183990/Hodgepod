package com.alan.hodgepod.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alan.hodgepod.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 16-6-7.
 */
public class LogoActivity extends Activity implements Animation.AnimationListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.activity_logo, null);
        ButterKnife.bind(this, view);
        setContentView(view);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_activity_enter_activity);
        animation.setAnimationListener(this);
        view.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        startActivity(new Intent(LogoActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.transition_enter_pop_in, R.anim.transition_enter_pop_out);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void finish() {
        super.finish();
    }
}
