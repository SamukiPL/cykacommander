package me.samuki.cykacommander;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import me.samuki.cykacommander.CykaGame;

public class AndroidLauncher extends AndroidApplication implements ShareAction {
	final String AD_UNIT_ID = "ca-app-pub-5519384153835422/2811367393";

	View gameView;
	AdView adView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hideSystemUI();

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = false;
		cfg.useCompass = false;

		RelativeLayout layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);

		AdView admobView = setAdView();
		layout.addView(admobView);

		View gameView = setGameView(cfg);
		layout.addView(gameView);

		setContentView(layout);
		startAdvertising(admobView);
	}

	private AdView setAdView() {
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(AD_UNIT_ID);
		adView.setId(0);
		adView.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		adView.setLayoutParams(params);
		adView.setBackgroundColor(Color.BLACK);
		return adView;
	}

	private View setGameView(AndroidApplicationConfiguration cfg) {
		gameView = initializeForView(new CykaGame(this), cfg);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.BELOW, adView.getId());
		gameView.setLayoutParams(params);
		return gameView;
	}

	private void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void hideSystemUI() {
		getWindow().getDecorView().setSystemUiVisibility(
				  		  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	@Override
	public void onResume() {
		super.onResume();
		hideSystemUI();
		if (adView != null) adView.resume();
	}

	@Override
	public void onPause() {
		hideSystemUI();
		if (adView != null) adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		hideSystemUI();
		if (adView != null) adView.destroy();
		super.onDestroy();
	}

	@Override
	public void shareScore(int score) {
		String share1 = getResources().getString(R.string.share_1);
		String share2 = getResources().getQuantityString(R.plurals.share_2, score);
		String share3 = getResources().getString(R.string.share_3);
		String title  = getResources().getString(R.string.send_to);
		String exception = getResources().getString(R.string.exception);

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		sendIntent.putExtra(Intent.EXTRA_TEXT, share1+" "+score+" "+share2+"\n"+share3);
		sendIntent.setType("text/plain");
		try {
			startActivity(Intent.createChooser(sendIntent, title));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(AndroidLauncher.this, exception, Toast.LENGTH_LONG).show();
		}
	}
}
