package github.com.st235.chiplayout;

import android.content.res.Resources;
import android.util.TypedValue;

public final class Dimens {

    private Dimens() {
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}