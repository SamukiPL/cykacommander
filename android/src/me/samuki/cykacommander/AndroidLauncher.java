package me.samuki.cykacommander;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements ShareAction, PlayServices, RewardedVideoAdListener {
	//Normalne zakomentowane
	final String AD_UNIT_ID_BANNER = "ca-app-pub-3940256099942544/6300978111";
	final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
    final String AD_UNIT_ID_REWARDED = "ca-app-pub-3940256099942544/5224354917";

	private GameHelper gameHelper;
	private final static int requestCode = 1;

	AdView adView;
	private InterstitialAd interstitialAd;
    boolean isInterstitialLoaded;
    private RewardedVideoAd rewardedAd;

	RelativeLayout.LayoutParams paramsGame;
	RelativeLayout.LayoutParams paramsAds;
	int gameViewTopMargin;

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String genderResult = getResources().getString(R.string.gender_result);
			String exception = getResources().getString(R.string.exception);
			String youAlreadyGetThis = getResources().getString(R.string.youAlreadyGetThis);

			if(msg.arg1 == 1)
				Toast.makeText(getApplicationContext(),genderResult, Toast.LENGTH_LONG).show();
			if(msg.arg1 == 2)
				Toast.makeText(getApplicationContext(),exception, Toast.LENGTH_LONG).show();
			if(msg.arg1 == 3)
				Toast.makeText(AndroidLauncher.this, youAlreadyGetThis, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//GOOGLE PLAY GAME SERVICES
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){ }

			@Override
			public void onSignInSucceeded(){ }
		};

		gameHelper.setup(gameHelperListener);


		//ADS AND VIEW
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		final View gameView = initializeForView(new CykaGame(this, this), config);
		gameViewTopMargin = AdSize.SMART_BANNER.getHeightInPixels(this.getContext());
		setupAds();

		RelativeLayout layout = new RelativeLayout(this);
		paramsGame = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(gameView, paramsGame);
		paramsAds = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		paramsAds.setMargins(0, -gameViewTopMargin, 0, 0);
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
		//INTERSTITIAL AD
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
			}

			@Override
			public void onAdClosed() {
				super.onAdClosed();
			}
		});
        loadInterstitialAd();
        //REWARDED AD
        rewardedAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedAd.setRewardedVideoAdListener(this);

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    public void showRewardedAd() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(rewardedAd.isLoaded()) {
						rewardedAd.show();
					}
				}
			});
		} catch (Exception ignored) {

		}
    }

    @Override
	public void loadRewardedAd() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					rewardedAd.loadAd(AD_UNIT_ID_REWARDED, new AdRequest.Builder().build());
				}
			});
		} catch (Exception ignored) {}
	}

    @Override
    public void loadInterstitialAd() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!interstitialAd.isLoaded()) {
                        AdRequest interstitialRequest = new AdRequest.Builder().build();
                        interstitialAd.loadAd(interstitialRequest);
                        Toast.makeText(AndroidLauncher.this, "Loading Ad", Toast.LENGTH_SHORT).show();
                        isInterstitialLoaded = true;
                    }
                }
            });
        } catch (Exception ignored) {

        }
    }

    @Override
    public void showInterstitialAd() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(interstitialAd.isLoaded()) {
                        interstitialAd.show();
                        Toast.makeText(AndroidLauncher.this, "Showing Ad", Toast.LENGTH_SHORT).show();
                        isInterstitialLoaded = false;
                    }
                    else {
                        loadInterstitialAd();
                    }
                }
            });
        } catch (Exception ignored) {

        }

    }

    public void setupAds() {
		adView = new AdView(this);
		adView.setVisibility(View.VISIBLE);
		adView.setBackgroundColor(0xff000000); //d7d7d7
		adView.setAdUnitId(AD_UNIT_ID_BANNER);
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
        rewardedAd.resume(this);
		hideSystemUI();
		if (adView != null) adView.resume();
	}

	@Override
	public void onPause() {
		hideSystemUI();
        rewardedAd.pause(this);
		if (adView != null) adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		hideSystemUI();
        rewardedAd.destroy(this);
		if (adView != null) adView.destroy();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			delayedHide(300);
		} else {
			mHideHandler.removeMessages(0);
		}
	}

	private final Handler mHideHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			hideSystemUI();
		}
	};

	private void delayedHide(int delayMillis) {
		mHideHandler.removeMessages(0);
		mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
	}

	@Override
	public void shareScore(int score) {
		String share1 = getResources().getString(R.string.share_1);
		String share2 = getResources().getQuantityString(R.plurals.share_2, score);
		String share3 = getResources().getString(R.string.share_3);
		String link = getResources().getString(R.string.link);
		String title  = getResources().getString(R.string.send_to);

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		sendIntent.putExtra(Intent.EXTRA_TEXT, share1+" "+score+" "+share2+"\n"+share3+"\n"+link);
		sendIntent.setType("text/plain");
		try {
			startActivity(Intent.createChooser(sendIntent, title));
		} catch (android.content.ActivityNotFoundException ex) {
			Message msg = handler.obtainMessage();
			msg.arg1 = 2;
			handler.sendMessage(msg);
		}
	}
	@Override
	public void genderResult() {
		Message msg = handler.obtainMessage();
		msg.arg1 = 1;
		handler.sendMessage(msg);
	}
	@Override
	public void rewardTaken() {
		Message msg = handler.obtainMessage();
		msg.arg1 = 3;
		handler.sendMessage(msg);
	}

	//GOOGLE PLAY SERVICES
	@Override
	public void signIn()	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame()	{
		String str = "https://play.google.com/store/apps/details?id=me.samuki.cykacommander";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void unlockAchievement(int score, int gamesPlayed)	{
		if(score >= 10) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_10_points_or_more));
		}
		if(score >= 20) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_20_points_or_more));
		}
		if(score >= 30) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_30_points_or_more));
		}
		if(score >= 40) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_40_points_or_more));
		}
		if(score >= 50) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_50_points_or_more));
		}
		if(score >= 75) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_75_points_or_more));
		}
		if(score >= 100) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_100_points_or_more));
		}
		if(gamesPlayed >= 1) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_first_game_thank_you));
		}
		if(gamesPlayed >= 10) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_10_games));
		}
		if(gamesPlayed >= 100) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_100_games));
		}
		if(gamesPlayed >= 250) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_250_games));
		}
		if(gamesPlayed >= 500) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_500_games));
		}
		if(gamesPlayed >= 1000) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_1000_games));
		}
		if(gamesPlayed >= 2500) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_play_2500_games));
		}
	}

	@Override
	public void submitScore(int highScore)	{
		if (isSignedIn())
		{
			Games.Leaderboards.submitScore(gameHelper.getApiClient(),
					getString(R.string.leaderboard_best_score_from_all_around_the_world), highScore);
		}
	}

	@Override
	public void showAchievement() {
		if (isSignedIn())
		{
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public void showScore()	{
		if (isSignedIn())
		{
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
					getString(R.string.leaderboard_best_score_from_all_around_the_world)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn()
	{
		return gameHelper.isSignedIn();
	}

	@Override
	public void onRewarded(RewardItem rewardItem) {//MUST HAVE!!!
		MenuScreen.giveThatReward(rewardItem.getAmount());
	}

	@Override
	public void onRewardedVideoAdLeftApplication() {

	}

	@Override
	public void onRewardedVideoAdClosed() {
		rewardedAd.loadAd(AD_UNIT_ID_REWARDED, new AdRequest.Builder().build());
	}

	@Override
	public void onRewardedVideoAdFailedToLoad(int errorCode) {
		rewardedAd.loadAd(AD_UNIT_ID_REWARDED, new AdRequest.Builder().build());
	}

	@Override
	public void onRewardedVideoAdLoaded() {
		GameBasic.adIsReady = true;
		try {
			MenuScreen.giveVodka();
		} catch (Exception ignored) {}
	}

	@Override
	public void onRewardedVideoAdOpened() {

	}

	@Override
	public void onRewardedVideoStarted() {

	}
}
