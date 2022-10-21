package admob.plus.cordova.ads;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import admob.plus.cordova.AdMob;
import admob.plus.cordova.ExecuteContext;
import admob.plus.cordova.Generated.Events;
import admob.plus.core.AdMobAdActivity;
import admob.plus.core.Context;

public class Interstitial extends AdBase {
    private InterstitialAd mAd = null;

    public Interstitial(ExecuteContext ctx) {
        super(ctx, AdType.INTERSTITIAL);
    }

    @Override
    public void onDestroy() {
        clear();
        onCloseAd();
        super.onDestroy();
    }

    @Override
    public void load(Context ctx) {
        clear();

        InterstitialAd.load(getActivity(), adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mAd = interstitialAd;
                mAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        clear();
                        emit(Events.AD_DISMISS);
                        emit(Events.INTERSTITIAL_DISMISS);
                        onCloseAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        clear();
                        emit(Events.AD_SHOW_FAIL, adError);
                        emit(Events.INTERSTITIAL_SHOW_FAIL, adError);
                        onCloseAd();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        emit(Events.AD_SHOW);
                        emit(Events.INTERSTITIAL_SHOW);
                    }

                    @Override
                    public void onAdImpression() {
                        emit(Events.AD_IMPRESSION);
                        emit(Events.INTERSTITIAL_IMPRESSION);
                    }
                });

                emit(Events.AD_LOAD);
                emit(Events.INTERSTITIAL_LOAD);
                ctx.resolve();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mAd = null;
                emit(Events.AD_LOAD_FAIL, loadAdError);
                emit(Events.INTERSTITIAL_LOAD_FAIL, loadAdError);
                ctx.reject(loadAdError.toString());
            }
        });
    }

    @Override
    public boolean isLoaded() {
        return mAd != null;
    }

    @Override
    public void show(Context ctx, Activity activity) {
        if (isLoaded()) {
            mAd.show(activity);
            ctx.resolve();
        } else {
            ctx.reject("Ad is not loaded");
        }
    }

    private void clear() {
        if (mAd != null) {
            mAd.setFullScreenContentCallback(null);
            mAd = null;
        }
    }

    private void onCloseAd() {
        if (AdMobAdActivity.activity != null) {
            AdMobAdActivity.activity.finish();
        }
        AdMob.removeContextByAdId(id);
    }
}
