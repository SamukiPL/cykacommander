package me.samuki.cykacommander;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements ShareAction, PlayServices {
	private GameHelper gameHelper;
	private final static int requestCode = 1;

	RelativeLayout.LayoutParams paramsGame;
	int gameViewTopMargin;

	@SuppressLint("HandlerLeak")
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


		//VIEW
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		final View gameView = initializeForView(new CykaGame(this, this), config);

		RelativeLayout layout = new RelativeLayout(this);
		paramsGame = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layout.addView(gameView, paramsGame);

		setContentView(layout);
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
	}

	@Override
	public void onPause() {
		hideSystemUI();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		hideSystemUI();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
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
	public void unlockAchievement(int score, int gamesPlayed, int shipUnlocked, String shipsNation)	{
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
		if(shipUnlocked == 10 && shipsNation.equals("ger")) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_for_magdalene));
		}
		if(shipUnlocked == 9 && shipsNation.equals("ger")) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_im_watching_you));

		}
		if(shipUnlocked == 15 && shipsNation.equals("")) {
			Games.Achievements.unlock(gameHelper.getApiClient(),
					getString(R.string.achievement_im_watching_you));

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
}
