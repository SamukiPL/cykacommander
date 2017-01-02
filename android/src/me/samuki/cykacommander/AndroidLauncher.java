package me.samuki.cykacommander;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication implements ShareAction {
	final String AD_UNIT_ID = "";

	AdView adView;

	RelativeLayout.LayoutParams paramsGame;
	RelativeLayout.LayoutParams paramsAds;
	int gameViewTopMargin;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Create a gameView and a bannerAd AdView
		final View gameView = initializeForView(new CykaGame(this), config);
		gameViewTopMargin = AdSize.SMART_BANNER.getHeightInPixels(this.getContext());
		setupAds();

		// Define the layout
		RelativeLayout layout = new RelativeLayout(this);
		paramsGame = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(gameView, paramsGame);
		paramsAds = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		paramsAds.addRule(RelativeLayout.ALIGN_TOP);
		paramsAds.addRule(RelativeLayout.ABOVE, gameView.getId());
		layout.addView(adView, paramsAds);

		setContentView(layout);
		startAdvertising(adView);

		adView.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
			}

			@Override
			public void onAdFailedToLoad(int i) {
				super.onAdFailedToLoad(i);
				hideSystemUI();
				paramsGame.setMargins(0, 0, 0, 0);
				gameView.setLayoutParams(paramsGame);
				paramsAds.setMargins(0, -gameViewTopMargin, 0, 0);
				adView.setLayoutParams(paramsAds);
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				hideSystemUI();
				paramsGame.setMargins(0, gameViewTopMargin, 0, 0);
				gameView.setLayoutParams(paramsGame);
				paramsAds.setMargins(0, 0, 0, 0);
				adView.setLayoutParams(paramsAds);
			}
		});
	}

	public void setupAds() {
		adView = new AdView(this);
		adView.setVisibility(View.VISIBLE);
		adView.setBackgroundColor(0xff000000); //d7d7d7
		adView.setAdUnitId(AD_UNIT_ID);
		adView.setAdSize(AdSize.SMART_BANNER);
	}

	private void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();
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
