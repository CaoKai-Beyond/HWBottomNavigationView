package com.huawei.bottomnavigationview;

/**
 * Author：caokai on 2018/11/22 11:10
 * <p>
 * email：caokai@11td.com
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class HwTextView extends TextView {
    private StaticLayout staticLayout;
    private float hwAutoSizeStepGranularity;
    private TextPaint textPaint;
    private float textSize;
    private float hwAutoSizeMinTextSize;

    public HwTextView(Context context) {
        this(context, null);
    }

    public HwTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HwTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        AttributeSet attributeSet2 = attributeSet;
        Context context2 = context;
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet2, R.styleable.HwTextView, i, 0);
        this.hwAutoSizeMinTextSize = obtainStyledAttributes.getDimension(R.styleable.HwTextView_hwAutoSizeMinTextSize, 0.0f);
        this.hwAutoSizeStepGranularity = obtainStyledAttributes.getDimension(R.styleable.HwTextView_hwAutoSizeStepGranularity, 0.0f);
        obtainStyledAttributes.recycle();
        if (this.hwAutoSizeMinTextSize == 0.0f && this.hwAutoSizeStepGranularity == 0.0f && VERSION.SDK_INT >= 26) {
            this.hwAutoSizeMinTextSize = (float) getAutoSizeMinTextSize();
            this.hwAutoSizeStepGranularity = (float) getAutoSizeStepGranularity();
            setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_NONE);
        }
        this.textPaint = new TextPaint();
        this.textPaint.set(getPaint());
        this.textSize = getTextSize();
    }

    @SuppressLint("DrawAllocation")
    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int i3 = size;
        int maxWidth = getMaxWidth();
        int maxHeight = getMaxHeight();
        if (maxWidth != -1 && maxWidth < i3) {
            i3 = maxWidth;
        }
        if (maxHeight != -1 && maxHeight < size2) {
            size2 = maxHeight;
        }
        int totalPaddingLeft = (i3 - getTotalPaddingLeft()) - getTotalPaddingRight();
        maxWidth = totalPaddingLeft;
        if (totalPaddingLeft >= 0) {
            if (this.textPaint == null) {
                this.textPaint = new TextPaint();
            }
            this.textPaint.set(getPaint());
            if (this.hwAutoSizeMinTextSize > 0.0f && this.hwAutoSizeStepGranularity > 0.0f) {
                float f = this.textSize;
                CharSequence text = getText();
                this.textPaint.setTextSize(f);
                for (float measureText = this.textPaint.measureText(text.toString()); measureText > ((float) maxWidth) && f > this.hwAutoSizeMinTextSize; measureText = this.textPaint.measureText(text.toString())) {
                    f -= this.hwAutoSizeStepGranularity;
                    this.textPaint.setTextSize(f);
                }
                setTextSize(0, f);
                totalPaddingLeft = size2;
                size2 = i3;
                i3 = totalPaddingLeft;
                totalPaddingLeft = getMaxLines();
                maxWidth = totalPaddingLeft;
                if (totalPaddingLeft > 1) {
                    size2 = (size2 - getTotalPaddingLeft()) - getTotalPaddingRight();
                    maxHeight = 0;
                    int i4 = 0;
                    if (VERSION.SDK_INT >= 26) {
                        maxHeight = getExtendedPaddingBottom();
                        i4 = getExtendedPaddingTop();
                    }
                    totalPaddingLeft = (i3 - maxHeight) - i4;
                    i3 = totalPaddingLeft;
                    if (totalPaddingLeft > 0) {
                        this.staticLayout = new StaticLayout(getText(), getPaint(), size2, Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), false);
                        size2 = this.staticLayout.getLineCount();
                        if (this.staticLayout.getHeight() > i3 && size2 > 1 && size2 <= maxWidth + 1) {
                            setMaxLines(size2 - 1);
                        }
                    }
                }
            }
        }
        super.onMeasure(i, i2);
    }

    public void setAutoTextInfo(int i, int i2, int i3) {
        Resources system;
        Context context = getContext();
        if (context == null) {
            system = Resources.getSystem();
        } else {
            system = context.getResources();
        }
        this.hwAutoSizeMinTextSize = TypedValue.applyDimension(i3, (float) i, system.getDisplayMetrics());
        this.hwAutoSizeStepGranularity = TypedValue.applyDimension(i3, (float) i2, system.getDisplayMetrics());
    }

    public void setAutoTextSize(float f) {
        setAutoTextSize(2, f);
    }

    public void setAutoTextSize(int i, float f) {
        Resources system;
        Context context = getContext();
        if (context == null) {
            system = Resources.getSystem();
        } else {
            system = context.getResources();
        }
        this.textSize = TypedValue.applyDimension(i, f, system.getDisplayMetrics());
        super.setTextSize(i, f);
    }
}
