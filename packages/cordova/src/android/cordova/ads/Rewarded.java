package admob.plus.cordova.ads;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;

import admob.plus.cordova.AdMob;
import admob.plus.cordova.ExecuteContext;
import admob.plus.cordova.Generated.Events;
import admob.plus.core.AdMobAdActivity;
import admob.plus.core.Context;

public class Rewarded extends AdBase {
    private RewardedAd mAd = null;

    public Rewarded(ExecuteContext ctx) {
        super(ctx, AdType.REWARDED);
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

        RewardedAd.load(getActivity(), adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mAd = null;
                emit(Events.AD_LOAD_FAIL, loadAdError);
                emit(Events.REWARDED_LOAD_FAIL, loadAdError);
                ctx.reject(loadAdError.toString());
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mAd = rewardedAd;
                ServerSideVerificationOptions ssv = ctx.optServerSideVerificationOptions();
                if (ssv != null) {
                    mAd.setServerSideVerificationOptions(ssv);
                }
                mAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        clear();
                        emit(Events.AD_DISMISS);
                        emit(Events.REWARDED_DISMISS);
                        onCloseAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        clear();
                        emit(Events.AD_SHOW_FAIL, adError);
                        emit(Events.REWARDED_SHOW_FAIL, adError);
                        onCloseAd();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        emit(Events.AD_SHOW);
                        emit(Events.REWARDED_SHOW);
                    }

                    @Override
                    public void onAdImpression() {
                        emit(Events.AD_IMPRESSION);
                        emit(Events.REWARDED_IMPRESSION);
                    }
                });

                emit(Events.AD_LOAD);
                emit(Events.REWARDED_LOAD);
                ctx.resolve();
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
            mAd.show(activity, rewardItem -> {
                emit(Events.AD_REWARD, rewardItem);
                emit(Events.REWARDED_REWARD, rewardItem);
            });
            ctx.resolve();
        } else {
            ctx.reject("Ad is not loaded");
        }
    }

    private void clear() {
        if (mAd != null) {
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
