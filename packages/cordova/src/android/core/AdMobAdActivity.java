package admob.plus.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import admob.plus.cordova.AdMob;
import admob.plus.cordova.ExecuteContext;

/**
 * Activity used as a container for displaying AdMob interstitials or Reward Videos.
 */
public class AdMobAdActivity extends Activity {
    private static final String TAG = "AdMob.AdActivity";

    public static Activity activity;
    private ExecuteContext ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        int adId = extras.getInt("adId", -1);
        if (adId == -1) {
            finish();
            return;
        }
        ctx = AdMob.getContextByAdId(adId);
        if (ctx == null) {
            finish();
            return;
        }

        GenericAd ad;
        try {
            ad = (GenericAd) Helper.getAd(adId);
        } catch (Exception e) {
            fail("Fail get ad by id " + adId + ": " + e.getMessage());
            return;
        }

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        activity = this;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundColor(0xB3000000);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        rlp.setMargins(0, 0, 0, 0);


        ProgressBar progressBar = crateBasicSpinner(this);
        relativeLayout.addView(progressBar);

        RelativeLayout.LayoutParams cancelButtonParams = new RelativeLayout.LayoutParams(500, 120);
        cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cancelButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cancelButtonParams.bottomMargin = 48;

        setContentView(relativeLayout, rlp);

        Log.d(TAG, "Displaying AdMob Interstitial");
        ad.show(ctx, activity);
    }

    /**
     * Method that shows a circular progress bar using the standard android layout.
     *
     * @param context The activity context.
     * @return The generated progress bar. {@link ProgressBar}
     */
    private ProgressBar crateBasicSpinner(Context context) {
        Log.d(TAG, "Showing basic spinner!!");

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams plp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        plp.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(plp);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        return progressBar;
    }

    public void fail(String failMessage) {
        if (ctx != null) {
            ctx.reject(failMessage);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Closing AdMob Activity");
        activity = null;
    }
}
