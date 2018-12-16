package com.myproject.androcryptor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashFragment extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_splash);

		ImageView sBoxImageView = (ImageView) findViewById(R.id.logoSplash);
		//Load the animation.
		Animation sBoxFadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fadein);
		sBoxImageView.startAnimation(sBoxFadeInAnimation);
	}

	public void clickEnter(View view) {
		Intent sBoxI = new Intent(this, AccountLoginFragment.class);
		startActivity(sBoxI);
	}
}