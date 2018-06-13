package github.com.st235.chiplayout;

import android.content.res.Resources;
import android.support.annotation.Dimension;
import android.support.annotation.Px;
import android.util.TypedValue;

import static android.support.annotation.Dimension.DP;

public final class Dimens {

    private Dimens() {
    }

    @Px
    public static int dpToPx(@Dimension(unit = DP) int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}