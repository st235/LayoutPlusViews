package github.com.st235.chiplayout;

import static androidx.annotation.Dimension.DP;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.Px;

public final class Dimens {

    private Dimens() {
    }

    @Px
    public static int dpToPx(@Dimension(unit = DP) int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}