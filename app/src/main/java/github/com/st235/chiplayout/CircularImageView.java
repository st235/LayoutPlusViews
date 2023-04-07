package github.com.st235.chiplayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * Displays image resources, for example {@link android.graphics.drawable.Drawable} resources
 * with a circular mask.
 */
public class CircularImageView extends View {
    private static final String TAG = "CircularImageView";
    private static final String DEFAULT_PLACEHOLDER = "Ex";

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF center = new PointF();

    @NonNull
    private String extraText = DEFAULT_PLACEHOLDER;

    @DrawableRes
    private int drawableId = -1;

    @Nullable
    private Bitmap targetImage;

    @ColorInt
    private int textColor = Color.BLACK;

    @Px
    private int textSize = 0;

    @FloatRange(from = 0.0f)
    private float radius;

    /**
     * Creates new one from code
     */
    public CircularImageView(Context context) {
        this(context, null);
    }

    /**
     * Creates new one from xml
     * @param attrs an xml attributes set
     */
    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates new one from xml with style from theme attribute
     * @param attrs an xml attributes set
     * @param defStyleAttr a style from theme
     */
    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    /**
     * Creates new one from xml with a style from theme attribute or style resource
     * Api 21 and above
     * @param attrs an xml attributes set
     * @param defStyleAttr a style from theme
     * @param defStyleRes a style resource
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    /**
     * Initialize current {@link CircularImageView} with attributes from xml
     */
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView);

        final int drawableId = ta.getResourceId(R.styleable.CircularImageView_cl_foreground, -1);
        if (drawableId != -1) {
            loadDrawable(drawableId);
        }

        textColor = ta.getColor(R.styleable.CircularImageView_cl_text_color, Color.BLACK);
        textSize = ta.getDimensionPixelSize(R.styleable.CircularImageView_cl_text_size, 0);

        final String t = ta.getString(R.styleable.CircularImageView_cl_text);
        extraText = t == null ? extraText : t;

        ta.recycle();
    }

    /**
     * Set extra text
     * @param extraText is a text which will be displayed at center of image view if exists
     */
    public void setExtraText(@NonNull String extraText) {
        this.extraText = extraText;
        invalidate();
    }

    /**
     * Set current image drawable resource
     * @param drawableId is identifier of drawable which will be displayed at image view
     */
    public void setDrawableResource(@DrawableRes int drawableId) {
        loadDrawable(drawableId);
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        final float textWidth = paint.measureText(extraText);
        final float textHeight = -fontMetrics.top + fontMetrics.bottom;

        final int desiredWidth = Math.round(textWidth + getPaddingLeft() + getPaddingRight());
        final int desiredHeight = Math.round(textHeight * 2f + getPaddingTop()  + getPaddingBottom());

        final int measuredWidth = reconcileSize(desiredWidth, widthMeasureSpec);
        final int measuredHeight = reconcileSize(desiredHeight, heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * Reconcile a desired size for the view contents with a {@link android.view.View.MeasureSpec}
     * constraint passed by the parent.
     *
     * This is a simplified version of {@link View#resolveSize(int, int)}
     *
     * @param contentSize Size of the view's contents.
     * @param measureSpec A {@link android.view.View.MeasureSpec} passed by the parent.
     * @return A size that best fits {@code contentSize} while respecting the parent's constraints.
     */
    private int reconcileSize(int contentSize, int measureSpec) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch(mode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                if (contentSize < specSize) {
                    return contentSize;
                }
                return specSize;
            case MeasureSpec.UNSPECIFIED:
            default:
                return contentSize;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        radius = Math.min(getMeasuredWidth() / 2.0f - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() / 2.0f - getPaddingTop() - getPaddingBottom());
        center.set(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);

        if (targetImage == null && drawableId != -1) {
            loadDrawable(drawableId);
        }

        updateShader();
        canvas.drawCircle(center.x, center.y, radius, paint);

        paint.setShader(null);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        final float textWidth = paint.measureText(extraText);
        int textY = (int) (center.y - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(extraText, center.x - textWidth / 2, textY, paint);
    }

    /**
     * Loads targetImage to be shown into memory
     * @param drawableId which will be loaded as target
     */
    private void loadDrawable(@DrawableRes int drawableId) {
        this.drawableId = drawableId;
        Drawable drawable = getResources().getDrawable(drawableId);
        targetImage = drawableToBitmap(drawable);
        targetImage = cropBitmap(targetImage);
    }

    /**
     * Updates paint shader with custom targetImage target
     */
    private void updateShader() {
        if (targetImage == null) {
            return;
        }

        BitmapShader shader = new BitmapShader(targetImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Matrix matrix = new Matrix();
        matrix.setScale((float) getMeasuredWidth() / (float) targetImage.getWidth(),
                (float) getMeasuredHeight() / (float) targetImage.getHeight());
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
    }

    /**
     * Creates center cropped targetImage from origin
     * @param bitmap which need to be cropped
     * @return targetImage instance
     */
    @Nullable
    @CheckResult
    private Bitmap cropBitmap(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        if (bitmap.getWidth() >= bitmap.getHeight()) {
            return Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(), bitmap.getHeight());
        }

        return Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                bitmap.getWidth(), bitmap.getWidth());
    }

    /**
     * Converts drawable to targetImage.
     * If the drawable has no intrinsic
     * width or height the laid out sizes will be set up as current viewport.
     * @param drawable which need to be shown
     * @return targetImage instance of drawable
     */
    @Nullable
    @CheckResult
    private Bitmap drawableToBitmap(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (intrinsicWidth == -1 || intrinsicHeight == -1) {
            intrinsicWidth = getMeasuredWidth();
            intrinsicHeight = getMeasuredHeight();
        }

        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return null;
        }

        try {
            Bitmap bitmap =
                    Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemory while creating targetImage!");
            return null;
        }
    }
}
