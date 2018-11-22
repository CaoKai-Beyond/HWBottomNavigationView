package com.huawei.bottomnavigationview;

/**
 * Author：caokai on 2018/11/22 10:58
 * <p>
 * email：caokai@11td.com
 */
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator implements Interpolator {
    private float fromYDelta;
    private float fromXDelta;
    private float toXDelta;
    private float toYDelta;

    public CubicBezierInterpolator(float fromXDelta, float fromYDelta, float toXDelta) {
        this.fromXDelta = fromXDelta;
        this.fromYDelta = fromYDelta;
        this.toXDelta = toXDelta;
        this.toYDelta = 1.0f;
    }

    public CubicBezierInterpolator(Context context, AttributeSet attributeSet) {
        this(context.getResources(), context.getTheme(), attributeSet);
    }

    private CubicBezierInterpolator(Resources resources, Theme theme, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes;
        this.fromXDelta = 0.0f;
        this.fromYDelta = 0.0f;
        this.toXDelta = 0.0f;
        this.toYDelta = 0.0f;
        if (theme != null) {
            obtainStyledAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.TranslateAnimation, 0, 0);
        } else {
            obtainStyledAttributes = resources.obtainAttributes(attributeSet, R.styleable.TranslateAnimation);
        }
        this.fromXDelta = ॱ(obtainStyledAttributes.peekValue(R.styleable.TranslateAnimation_fromXDelta));
        this.fromYDelta = ॱ(obtainStyledAttributes.peekValue(R.styleable.TranslateAnimation_fromYDelta));
        this.toXDelta = ॱ(obtainStyledAttributes.peekValue(R.styleable.TranslateAnimation_toXDelta));
        this.toYDelta = ॱ(obtainStyledAttributes.peekValue(R.styleable.TranslateAnimation_toYDelta));
        obtainStyledAttributes.recycle();
    }

    private static float ॱ(TypedValue typedValue) {
        if (typedValue != null) {
            if (typedValue.type == 6) {
                return TypedValue.complexToFloat(typedValue.data);
            }
            if (typedValue.type == 4) {
                return typedValue.getFloat();
            }
            if (typedValue.type >= 16 && typedValue.type <= 31) {
                return (float) typedValue.data;
            }
        }
        return 1.0f;
    }

    public float getInterpolation(float f) {
        return getfromYDelta(((float) path(f)) * 2.5E-4f);
    }

    private float getfromXDelta(float f) {
        return ((((((1.0f - f) * 3.0f) * (1.0f - f)) * f) * this.fromXDelta) + (((((1.0f - f) * 3.0f) * f) * f) * this.toXDelta)) + ((f * f) * f);
    }

    protected final float getfromYDelta(float f) {
        return ((((((1.0f - f) * 3.0f) * (1.0f - f)) * f) * this.fromYDelta) + (((((1.0f - f) * 3.0f) * f) * f) * this.toYDelta)) + ((f * f) * f);
    }

    final long path(float f) {
        long j = 0;
        long j2 = 4000;
        while (j <= j2) {
            long j3 = (j + j2) >>> 1;
            float ˎ = getfromXDelta(((float) j3) * 2.5E-4f);
            float f2 = ˎ;
            if (ˎ < f) {
                j = j3 + 1;
            } else if (f2 <= f) {
                return j3;
            } else {
                j2 = j3 - 1;
            }
        }
        return j;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("CubicBezierInterpolator");
        stringBuffer.append("  mControlPoint1x = ").append(this.fromXDelta);
        stringBuffer.append(", mControlPoint1y = ").append(this.fromYDelta);
        stringBuffer.append(", mControlPoint2x = ").append(this.toXDelta);
        stringBuffer.append(", mControlPoint2y = ").append(this.toYDelta);
        return stringBuffer.toString();
    }
}