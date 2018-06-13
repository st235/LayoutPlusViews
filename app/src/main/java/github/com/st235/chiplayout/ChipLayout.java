package github.com.st235.chiplayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Layout for positioning chip components as a group.
 */
public class ChipLayout extends ViewGroup {

    /**
     * Creates new one from code
     */
    public ChipLayout(Context context) {
        this(context, null);
    }

    /**
     * Creates new one from xml
     * @param attrs an xml attributes set
     */
    public ChipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates new one from xml with style from theme attribute
     * @param attrs an xml attributes set
     * @param defStyleAttr a style from theme
     */
    public ChipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(true);
    }

    /**
     * Creates new one from xml with a style from theme attribute or style resource
     * Api 21 and above
     * @param attrs an xml attributes set
     * @param defStyleAttr a style from theme
     * @param defStyleRes a style resource
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChipLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setWillNotDraw(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof LinearLayout.LayoutParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LinearLayout.LayoutParams(p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        final int preMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingRight();

        int childState = 0;

        int width = 0;
        int height = 0;

        int allChildWidth = 0;

        int rowCount = 0;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LinearLayout.LayoutParams params = getChildLayoutParams(child);

            if (child.getVisibility() == GONE) continue;

            int horizontalMargins = params.leftMargin + params.rightMargin;
            int verticalMargins = params.topMargin + params.bottomMargin;
            measureChildWithMargins(child,
                    widthMeasureSpec, 0, heightMeasureSpec, 0);

            width += Math.max(width, child.getMeasuredWidth() + horizontalMargins);
            allChildWidth += child.getMeasuredWidth() + horizontalMargins;

            if ((allChildWidth / preMeasuredWidth) > rowCount) {
                height += child.getMeasuredHeight() + verticalMargins;
                rowCount++;
            } else {
                height = Math.max(height, child.getMeasuredHeight() + verticalMargins);
            }

            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        height = Math.max(height, getSuggestedMinimumHeight());
        width = Math.max(width, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        final int layoutLeft = getPaddingLeft();
        final int layoutRight = getMeasuredWidth() - getPaddingRight();
        final int layoutTop = getPaddingTop();
        final int layoutBottom = getMeasuredHeight() - getPaddingBottom();
        final int layoutWidth = layoutRight - layoutLeft;
        final int layoutHeight = layoutTop - layoutBottom;

        int maxHeight = 0;

        int width, height;
        int left = layoutLeft;
        int top = layoutTop;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            final LinearLayout.LayoutParams params = getChildLayoutParams(child);

            if (child.getVisibility() == GONE) continue;

            child.measure(MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.AT_MOST));

            width = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            height = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (left + width >= layoutRight) {
                left = layoutLeft;
                top += maxHeight;
                maxHeight = 0;
            }

            child.layout(left + params.leftMargin,
                    top + params.topMargin,
                    left + child.getMeasuredWidth() + params.leftMargin,
                    top + child.getMeasuredHeight() + params.topMargin);

            if (maxHeight < height)
                maxHeight = height;

            left += width;
        }
    }

    /**
     * Returns layout params from child if exists or generates new one otherwise
     * @param child a view from this view group
     * @return layout params for child view
     */
    @NonNull
    private LinearLayout.LayoutParams getChildLayoutParams(@NonNull View child) {
        final ViewGroup.LayoutParams params = child.getLayoutParams();
        return (LinearLayout.LayoutParams) (checkLayoutParams(params)
                ? params : generateDefaultLayoutParams());
    }
}
