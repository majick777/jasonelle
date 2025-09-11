package com.jasonette.seed.Core;

import com.jasonette.seed.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class JasonIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("JasonIntentActivity", "OnCreate Intent");
        handleIncomingIntent(getIntent(), false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
		Log.d("JasonIntentActivity", "New Intent");
        handleIncomingIntent(intent, true);
    }

    private void handleIncomingIntent(Intent intent, Boolean newintent) {

        if (intent == null) return;
        String action = intent.getAction();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
		    String scheme = data.getScheme();
            String host = data.getHost();
            String path = data.getPath();
            String full = data.toString();
            Log.d("JasonIntentActivity", "scheme="+scheme+" host="+host+" path="+path+" uri="+full);
        
		    Intent i = new Intent(this, JasonViewActivity.class);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(data);
			if (newintent) {
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			}
            startActivity(i);
			finish();
			return;
        }
		finish();
    }
}
