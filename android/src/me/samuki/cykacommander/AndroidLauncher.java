package me.samuki.cykacommander;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import me.samuki.cykacommander.CykaGame;

public class AndroidLauncher extends AndroidApplication implements ShareAction {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new CykaGame(this), config);
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
