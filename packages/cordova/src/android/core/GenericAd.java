package admob.plus.core;

import static admob.plus.core.Helper.NOT_IMPLEMENTED;

import android.app.Activity;

public interface GenericAd {
    default boolean isLoaded() {
        NOT_IMPLEMENTED();
        return false;
    }

    default void load(Context ctx) {
        NOT_IMPLEMENTED();
    }

    default void show(Context ctx, Activity activity) {
        NOT_IMPLEMENTED();
    }

    default void hide(Context ctx) {
        NOT_IMPLEMENTED();
    }

    String getType();

    int getId();
}