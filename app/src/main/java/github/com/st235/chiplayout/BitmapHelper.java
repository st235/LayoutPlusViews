package github.com.st235.chiplayout;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class BitmapHelper {

    private BitmapHelper() {
    }

    @NonNull
    @CheckResult
    public static Bitmap decodeSampledBitmapFromResource(@NonNull Resources res,
                                                         @DrawableRes int resId,
                                                         @IntRange(from = 0) int reqWidth,
                                                         @IntRange(from = 0) int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @CheckResult
    private static int calculateInSampleSize(@NonNull BitmapFactory.Options options,
                                             @IntRange(from = 0) int reqWidth,
                                             @IntRange(from = 0) int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
