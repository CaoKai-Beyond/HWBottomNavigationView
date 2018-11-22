package com.huawei.bottomnavigationview;

/**
 * Author：caokai on 2018/11/22 10:57
 * <p>
 * email：caokai@11td.com
 */

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HwBottomNavigationView extends LinearLayout {
    private ItemOnClick mItemOnClick;
    private MenuInflater menuInflater;
    private int iconActiveColor;
    public Menu menu;
    private BottomNavListener mBottomNavListener;
    private Context context;
    private Resources resources;
    private String TAG;
    public int ActiveIndex;
    private boolean IsPortLayout;
    private int itemTextMargin;
    public int menuSize;
    private int itemIconSize;
    private boolean BlurEnable;
    private int iconDefaultColor;
    private int messageBgColor;


    public Object inVoke(Object obj, String str, Class[] clsArr, Object[] objArr, Class<?> cls) {
        if (obj == null) {
            return null;
        }
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod.invoke(obj, objArr);
        } catch (NoSuchMethodException unused) {
            return null;
        } catch (IllegalArgumentException unused2) {
            return null;
        } catch (IllegalAccessException unused3) {
            return null;
        } catch (InvocationTargetException unused4) {
            return null;
        }
    }


    public class IconDrawable extends Drawable {
        private Rect rect;
        private int iconSize;
        private ValueAnimator zoomOut;
        private ValueAnimator zoomAnim;
        private int activeDrawableTintColor;
        int intValue = 0;
        private Path path;
        private Drawable activeDrawable;
        private Drawable defaultDrawable;
        private AnimatorUpdateListener animatorUpdateListener;
        private Context context;
        private int defaultDrawableTintColor;
        private int animDuation;

        public IconDrawable(Context context, Drawable drawable) {
            this.context = context;
            this.animDuation = context.getResources().getInteger(R.integer.hwbottomnav_icon_anim_duration);
            this.iconSize = context.getResources().getDimensionPixelSize(R.dimen.hwbottomnav_item_icon_size);
            this.rect = new Rect(0, 0, this.iconSize, this.iconSize);
            setDrawable(drawable);
            this.animatorUpdateListener = new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    Drawable drawable2 = IconDrawable.this;
                    IconDrawable.this.intValue = intValue;
                    drawable2.invalidateSelf();
                }
            };
            this.path = new Path();
            TimeInterpolator cubicBezierInterpolator = new CubicBezierInterpolator(0.4f, 0.0f, 0.2f);
            TimeInterpolator cubicBezierInterpolator2 = new CubicBezierInterpolator(0.2f, 0.0f, 0.2f);
            this.zoomAnim = ValueAnimator.ofInt(0, (int) (((float) this.iconSize) * 1.42f));
            this.zoomAnim.setDuration((long) this.animDuation);
            this.zoomAnim.addUpdateListener(this.animatorUpdateListener);
            this.zoomAnim.setInterpolator(cubicBezierInterpolator2);
            this.zoomOut = ValueAnimator.ofInt((int) (((float) this.iconSize) * 1.42f), 0);
            this.zoomOut.setDuration((long) this.animDuation);
            this.zoomOut.addUpdateListener(this.animatorUpdateListener);
            this.zoomOut.setInterpolator(cubicBezierInterpolator);
        }

        public final void draw(Canvas canvas) {
            this.path.reset();
            this.path.addCircle((float) (HwBottomNavigationView.isDirectionRtl(HwBottomNavigationView.this) ? this.iconSize : this.rect.left),
                    (float) this.rect.bottom, (float) this.intValue, Direction.CCW);
            canvas.save();
            canvas.clipPath(this.path, Op.DIFFERENCE);
            this.activeDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipPath(this.path);
            this.defaultDrawable.draw(canvas);
            canvas.restore();
        }

        public final void setAlpha(int i) {
            if (this.activeDrawable != null) {
                this.activeDrawable.setAlpha(i);
            }
        }

        public final void setColorFilter(ColorFilter colorFilter) {
            if (this.activeDrawable != null) {
                this.activeDrawable.setColorFilter(colorFilter);
            }
        }

        public final int getOpacity() {
            if (this.activeDrawable != null) {
                return this.activeDrawable.getOpacity();
            }
            return PixelFormat.TRANSLUCENT;
        }

        public final void setDrawable(Drawable drawable) {
            if (drawable instanceof StateListDrawable) {
                StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                int identifier = this.context.getResources().getIdentifier("state_selected", "attr", "android");
                int[] iArr = new int[0];
                int[] iArr2 = new int[]{identifier};
                Drawable drawable2 = null;
                Drawable drawable3 = null;
                int StateDrawableIndex = getStateDrawableIndex(stateListDrawable, new int[]{identifier ^ -1});
                identifier = StateDrawableIndex;
                if (StateDrawableIndex != -1) {
                    drawable2 = getStateDrawable(stateListDrawable, identifier);
                }
                StateDrawableIndex = getStateDrawableIndex(stateListDrawable, iArr2);
                identifier = StateDrawableIndex;
                if (StateDrawableIndex != -1) {
                    drawable3 = getStateDrawable(stateListDrawable, identifier);
                }
                if (!(drawable2 == null && drawable3 == null)) {
                    if (drawable2 == null || drawable3 == null) {
                        StateDrawableIndex = getStateDrawableIndex(stateListDrawable, iArr);
                        identifier = StateDrawableIndex;
                        if (StateDrawableIndex != -1) {
                            setActiveDefalutDrawable(drawable2 == null ? getStateDrawable(stateListDrawable, identifier)
                                    : drawable2, drawable3 == null ? getStateDrawable(stateListDrawable, identifier) : drawable3);
                            return;
                        }
                        throw new IllegalArgumentException("no resource available to provide");
                    }
                    setActiveDefalutDrawable(drawable2, drawable3);
                    return;
                }
            }
            setActiveDefalutDrawable(drawable, drawable.getConstantState().newDrawable().mutate());
        }

        private Drawable getStateDrawable(StateListDrawable stateListDrawable, int i) {
            Object StateDrawable = inVoke(stateListDrawable, "getStateDrawable",
                    new Class[]{Integer.TYPE}, new Object[]{i}, StateListDrawable.class);
            if (StateDrawable != null) {
                return (Drawable) StateDrawable;
            }
            return null;
        }

        private int getStateDrawableIndex(StateListDrawable stateListDrawable, int[] iArr) {
            Object StateDrawableIndex = inVoke(stateListDrawable, "getStateDrawableIndex", new Class[]{iArr.getClass()}, new Object[]{iArr}, StateListDrawable.class);
            if (StateDrawableIndex != null) {
                return (Integer) StateDrawableIndex;
            }
            return -1;
        }

        private void setActiveDefalutDrawable(Drawable activeDrawable, Drawable defaultDrawable) {
            if (activeDrawable != null && defaultDrawable != null) {
                this.activeDrawable = activeDrawable;
                this.activeDrawable.setBounds(this.rect);
                if (VERSION.SDK_INT < 21) {
                    this.activeDrawable = DrawableCompat.wrap(this.activeDrawable).mutate();
                    DrawableCompat.setTint(this.activeDrawable, this.activeDrawableTintColor);
                } else {
                    this.activeDrawable.setTint(this.activeDrawableTintColor);
                }
                this.defaultDrawable = defaultDrawable;
                this.defaultDrawable.setBounds(this.rect);
                if (VERSION.SDK_INT < 21) {
                    this.defaultDrawable = DrawableCompat.wrap(this.defaultDrawable).mutate();
                    DrawableCompat.setTint(this.defaultDrawable, this.defaultDrawableTintColor);
                } else {
                    this.defaultDrawable.setTint(this.defaultDrawableTintColor);
                }
                invalidateSelf();
            }
        }

        final void setDefaultDrawableTintColor(int color) {
            if (this.defaultDrawableTintColor != color) {
                this.defaultDrawableTintColor = color;
                if (this.defaultDrawable != null) {
                    DrawableCompat.setTint(this.defaultDrawable, this.defaultDrawableTintColor);
                }
                invalidateSelf();
            }
        }

        final void setActiveDrawableTintColor(int color) {
            if (this.activeDrawableTintColor != color) {
                this.activeDrawableTintColor = color;
                if (this.activeDrawable != null) {
                    DrawableCompat.setTint(this.activeDrawable, this.activeDrawableTintColor);
                }
                invalidateSelf();
            }
        }

        private void startZoomAnim(boolean z) {
            ValueAnimator valueAnimator = z ? this.zoomOut : this.zoomAnim;
            ValueAnimator valueAnimator2 = z ? this.zoomAnim : this.zoomOut;
            if (valueAnimator.isRunning()) {
                valueAnimator.reverse();
            } else {
                valueAnimator2.start();
            }
        }

        final void setStates(boolean IsActive, boolean Checked) {
            if (Checked) {
                startZoomAnim(IsActive);
                return;
            }
            int i = IsActive ? (int) (((float) this.iconSize) * 1.42f) : 0;
            this.intValue = i;
            invalidateSelf();
        }
    }

    class ItemOnClick implements OnClickListener {
        private ItemLayout itemLayout;

        public final void onClick(View view) {
            if (view instanceof ItemLayout) {
                this.itemLayout = (ItemLayout) view;
                ItemClick(this.itemLayout, true);
            }
        }
    }

    public interface BottomNavListener {
        void OnSelect();

        void Cancel();

        void OnActive(int index);
    }

    public class ItemLayout extends LinearLayout {
        public IconDrawable mIconDrawable1;
        private IconDrawable mIconDrawable3;
        int ActiveColor;
        private int itemMinTextsize;
        int selectIndex;
        private int textStepgranularity;
        private int itemLandTextsize;
        private int itemVerticalPadding;
        private int itemHorizontalPadding;
        private int itemRedDotRadius;
        ImageView imageView;
        private boolean IsActive;
        public boolean HasMessage;
        private int itemTopMargin;
        boolean isLandscape;
        private Paint paint;
        public IconDrawable mIconDrawable2;
        ImageView topIcon;
        public HwTextView hwTextView;
        float desiredWidth2;
        float desiredWidth;
        LinearLayout container;
        boolean aBoolean;
        private int itemPortTextsize;
        private int temLandMinheight;
        public MenuItem menuItem;
        private int itemPortMinheight;
        int DefaultColor;
        private int MsgBgColor;

        public ItemLayout(Context context, MenuItem menuItem, boolean isLandscape, int maxIndex) {
            super(context);
            this.menuItem = menuItem;
            inflate(context, R.layout.bottomnav_item_layout, this);
            imageView = findViewById(R.id.startIcon);
            topIcon = findViewById(R.id.topIcon);
            hwTextView = findViewById(R.id.content);
            container = findViewById(R.id.container);
            this.mIconDrawable1 = new IconDrawable(context, this.menuItem.getIcon());
            this.mIconDrawable2 = new IconDrawable(context, this.menuItem.getIcon());
            this.temLandMinheight = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_land_minheight);
            this.itemPortMinheight = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_port_minheight);
            this.itemPortTextsize = resources.getInteger(R.integer.hwbottomnav_item_port_textsize);
            this.itemLandTextsize = resources.getInteger(R.integer.hwbottomnav_item_land_textsize);
            this.textStepgranularity = resources.getInteger(R.integer.hwbottomnav_text_stepgranularity);
            this.itemMinTextsize = resources.getInteger(R.integer.hwbottomnav_item_min_textsize);
            this.itemVerticalPadding = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_vertical_padding);
            this.itemHorizontalPadding = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_horizontal_padding);
            this.itemTopMargin = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_top_margin);
            this.itemRedDotRadius = resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_red_dot_radius);
            this.hwTextView.setAutoTextInfo(this.itemMinTextsize, this.textStepgranularity, 1);
            this.isLandscape = isLandscape;
            this.selectIndex = maxIndex;
            this.imageView.setImageDrawable(this.mIconDrawable1);
            this.topIcon.setImageDrawable(this.mIconDrawable2);
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            setOrientation(LinearLayout.VERTICAL);
            setLayout(true, true);
        }

        public final void setChecked(boolean IsActive, boolean Checked) {
            if (IsActive != this.IsActive) {
                this.IsActive = IsActive;
                this.mIconDrawable3 = this.isLandscape ? this.mIconDrawable1 : this.mIconDrawable2;
                this.mIconDrawable3.setStates(this.IsActive, Checked);
                this.hwTextView.setTextColor(this.IsActive ? this.ActiveColor : this.DefaultColor);
            }
        }

        public final void setDirection(boolean Direction) {
            if (Direction != this.isLandscape) {
                this.isLandscape = Direction;
            }
            setLayout(true, false);
        }

        final void setLayout(boolean z, boolean z2) {
            if (z) {
                MarginLayoutParams marginLayoutParams;
                if (this.isLandscape) {
                    setGravity(Gravity.CENTER);
                    setMinimumHeight(this.temLandMinheight);
                    setPadding(this.itemHorizontalPadding, 0, this.itemHorizontalPadding, 0);
                    this.topIcon.setVisibility(View.GONE);
                    this.imageView.setVisibility(View.VISIBLE);
                    marginLayoutParams = (MarginLayoutParams) this.hwTextView.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, 0);
                    this.hwTextView.setLayoutParams(marginLayoutParams);
                    this.hwTextView.setAutoTextSize(1, (float) this.itemLandTextsize);
                    this.hwTextView.setGravity(GravityCompat.START);
                    this.mIconDrawable3 = this.mIconDrawable1;
                } else {
                    setGravity(0);
                    setMinimumHeight(this.itemPortMinheight);
                    setPadding(0, this.itemVerticalPadding + this.itemTopMargin, 0, this.itemVerticalPadding);
                    this.topIcon.setVisibility(View.VISIBLE);
                    this.imageView.setVisibility(View.GONE);
                    marginLayoutParams = (MarginLayoutParams) this.hwTextView.getLayoutParams();
                    marginLayoutParams.setMargins(HwBottomNavigationView.this.itemTextMargin, 0, HwBottomNavigationView.this.itemTextMargin, 0);
                    this.hwTextView.setLayoutParams(marginLayoutParams);
                    this.hwTextView.setAutoTextSize(1, (float) this.itemPortTextsize);
                    this.hwTextView.setGravity(1);
                    this.mIconDrawable3 = this.mIconDrawable2;
                }
                this.hwTextView.setText(this.menuItem.getTitle());
                this.mIconDrawable3.setStates(this.IsActive, false);
            }
            if (z2) {
                this.mIconDrawable1.setDefaultDrawableTintColor(this.ActiveColor);
                this.mIconDrawable1.setActiveDrawableTintColor(this.DefaultColor);
                this.mIconDrawable2.setDefaultDrawableTintColor(this.ActiveColor);
                this.mIconDrawable2.setActiveDrawableTintColor(this.DefaultColor);
                this.hwTextView.setTextColor(this.IsActive ? this.ActiveColor : this.DefaultColor);
            }
        }

        public final void setHasMessage(boolean HasMessage) {
            this.HasMessage = HasMessage;
            invalidate();
        }

        public final void setMsgBgColor(int color) {
            this.MsgBgColor = color;
            this.paint.setColor(this.MsgBgColor);
            invalidate();
        }

        protected final void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.HasMessage) {
                View view = this.isLandscape ? this.imageView : this.topIcon;
                Rect rect = new Rect();
                Rect rect2 = new Rect();
                getGlobalVisibleRect(rect);
                view.getGlobalVisibleRect(rect2);
                canvas.drawCircle((float) (HwBottomNavigationView.isDirectionRtl(HwBottomNavigationView.this) ? (rect2.left - rect.left) + this.itemRedDotRadius
                        : (rect2.right - rect.left) - this.itemRedDotRadius), (float) ((rect2.top - rect.top) + this.itemRedDotRadius), (float) this.itemRedDotRadius, this.paint);
            }
        }
    }

    public HwBottomNavigationView(Context context) {
        this(context, null);
    }

    public HwBottomNavigationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HwBottomNavigationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "HwBottomNavigationView";
        this.iconActiveColor = -16744961;
        this.iconDefaultColor = -8355712;
        this.messageBgColor = Color.RED;
        this.ActiveIndex = -1;
//        this.itemPortTextsize = bne.hwTextView();
        this.BlurEnable = false;
        this.context = context;
        this.resources = context.getResources();
        try {
            this.menu = (Menu) Class.forName("com.android.internal.view.menu.MenuBuilder").getConstructor(Context.class).newInstance(new Object[]{context});
        } catch (ClassNotFoundException unused) {
            Log.e(this.TAG, "HwBottomNavigationView: MenuBuilder init failed");
        } catch (NoSuchMethodException unused2) {
            Log.e(this.TAG, "HwBottomNavigationView: MenuBuilder init failed");
        } catch (IllegalAccessException unused3) {
            Log.e(this.TAG, "HwBottomNavigationView: MenuBuilder init failed");
        } catch (InstantiationException unused4) {
            Log.e(this.TAG, "HwBottomNavigationView: MenuBuilder init failed");
        } catch (InvocationTargetException unused5) {
            Log.e(this.TAG, "HwBottomNavigationView: MenuBuilder init failed");
        }
        this.menuInflater = new MenuInflater(this.context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HwBottomNavigationView, i, R.style.HwBottomNavigationView);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.HwBottomNavigationView_bottomNavMenu, 0);
        this.iconDefaultColor = obtainStyledAttributes.getColor(R.styleable.HwBottomNavigationView_iconDefaultColor, this.iconDefaultColor);
        this.iconActiveColor = obtainStyledAttributes.getColor(R.styleable.HwBottomNavigationView_iconActiveColor, this.iconActiveColor);
        this.messageBgColor = obtainStyledAttributes.getColor(R.styleable.HwBottomNavigationView_messageBgColor, this.messageBgColor);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.HwBottomNavigationView_android_background);
        obtainStyledAttributes.recycle();
        if (drawable != null) {
            setBackgroundDrawable(drawable);
        }
        this.itemTextMargin = this.resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_text_margin);
        this.itemIconSize = this.resources.getDimensionPixelSize(R.dimen.hwbottomnav_item_icon_size);
        if (resourceId > 0) {
            this.menuInflater.inflate(resourceId, this.menu);
        }
        this.mItemOnClick = new ItemOnClick();
        MenuSizeBig(this.menu);
        initItems(this.menu);
    }

    private boolean MenuSizeBig(Menu menu) {
        if (menu.size() > 5) {
            this.menuSize = 5;
            return false;
        }
        this.menuSize = menu.size();
        return true;
    }

    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = this.menuSize;
        for (int i2 = 0; i2 < i; i2++) {
            ItemLayout eVar = (ItemLayout) getChildAt(i2);
            boolean z = !this.IsPortLayout && this.context.getResources().getConfiguration().orientation == 2;
            eVar.setDirection(z);
        }
    }

    public void setPortLayout(boolean PortLayout) {
        if (this.IsPortLayout != PortLayout) {
            this.IsPortLayout = PortLayout;
            requestLayout();
        }
    }

    public final boolean initItems(CharSequence charSequence, Drawable drawable) {
        MenuItem icon = this.menu.add(0, 0, 0, charSequence).setIcon(drawable);
        if (!MenuSizeBig(this.menu)) {
            return false;
        }
        addItem(icon, this.menuSize - 1);
        return true;
    }

    public void setMessageBgColor(int color) {
        this.messageBgColor = color;
        for (int i = 0; i < this.menuSize; i++) {
            ((ItemLayout) getChildAt(i)).setMsgBgColor(color);
        }
    }

    private void initItems(Menu menu) {
        int i = this.menuSize;
        for (int i2 = 0; i2 < i; i2++) {
            addItem(menu.getItem(i2), i2);
        }
    }

    private void addItem(MenuItem menuItem, int maxIndex) {
        Context context = this.context;
        boolean isLandscape = !this.IsPortLayout && this.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        ItemLayout eVar = new ItemLayout(context, menuItem, isLandscape, maxIndex);
        ItemLayout view = eVar;
        ItemLayout view2 = eVar;
        eVar.ActiveColor = this.iconActiveColor;
        view2.setLayout(false, true);
        view2 = view;
        view.DefaultColor = this.iconDefaultColor;
        view2.setLayout(false, true);
        view.setMsgBgColor(this.messageBgColor);
        view.setOnClickListener(this.mItemOnClick);
        addView(view);
    }

    public void setItemHasMessage(int index,boolean has){
        ItemLayout itemLayout= (ItemLayout) getChildAt(index);
        itemLayout.setHasMessage(has);
    }

    private void setViewLayoutParams(View view, int left, int right, MarginLayoutParams marginLayoutParams) {
        Object obj = VERSION.SDK_INT >= 17 ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL ? 1 : null : null;
        if (obj != null) {
            marginLayoutParams.setMargins(right, marginLayoutParams.topMargin, left, marginLayoutParams.bottomMargin);
        } else {
            marginLayoutParams.setMargins(left, marginLayoutParams.topMargin, right, marginLayoutParams.bottomMargin);
        }
        view.setLayoutParams(marginLayoutParams);
    }

    public void setBottomNavListener(BottomNavListener listener) {
        this.mBottomNavListener = listener;
    }

    public void setItemChecked(int index) {
        int childCount = getChildCount();
        if (index >= 0 && index < childCount) {
            ItemLayout eVar = (ItemLayout) getChildAt(index);
            eVar.setChecked(true, this.ActiveIndex != -1);
            ItemClick(eVar, false);
        }
    }

    public void draw(Canvas canvas) {
//        bne.context();
        super.draw(canvas);
    }

    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
//        if (i == 0) {
//            bne.zoomAnim("LightBlurWithGray");
//        }
    }

    public void setBlurEnable(boolean Enable) {
        this.BlurEnable = Enable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Object obj = (this.IsPortLayout || this.context.getResources().getConfiguration().orientation != 2) ? null : 1;
        int i3;
        int childMeasureSpec;
        int i4;
        View view;
        int measuredHeight;
        int i5;
        View view2;
        ViewGroup.LayoutParams layoutParams;
        if (obj != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            i3 = 0;
            if (VERSION.SDK_INT >= 17) {
                getLayoutDirection();
            }
            childMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), -2);
            int childCount = getChildCount();
            for (i4 = 0; i4 < childCount; i4++) {
                float f = ((float) width) / ((float) childCount);
                ItemLayout view3 = (ItemLayout) getChildAt(i4);
                view3.measure(MeasureSpec.makeMeasureSpec((int) f, MeasureSpec.EXACTLY), childMeasureSpec);
                view = view3.container;
                setViewLayoutParams(view, 0, 0, (MarginLayoutParams) view.getLayoutParams());
                measuredHeight = view3.getMeasuredHeight();
                int i6 = measuredHeight;
                if (measuredHeight > i3) {
                    i3 = i6;
                }
                i5 = (int) f;
                view2 = view3;
                layoutParams = view3.getLayoutParams();
                layoutParams.width = i5;
                view2.setLayoutParams(layoutParams);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(i3, MeasureSpec.EXACTLY));
            return;
        }
        List arrayList = new ArrayList();
        i3 = 0;
        int i7 = 0;
        widthMeasureSpec = MeasureSpec.getSize(widthMeasureSpec);
        measuredHeight = getChildCount();
        childMeasureSpec = measuredHeight;
        if (measuredHeight == 0) {
            setMeasuredDimension(widthMeasureSpec, 0);
            return;
        }
        float f2 = ((float) widthMeasureSpec) / ((float) childMeasureSpec);
        heightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), -2);
        int i8;
        if (childMeasureSpec == 2 || childMeasureSpec == 1) {
            for (i8 = 0; i8 < childMeasureSpec; i8++) {
                view = (ItemLayout) getChildAt(i8);
                view.measure(MeasureSpec.makeMeasureSpec((int) f2, MeasureSpec.EXACTLY), heightMeasureSpec);
                i5 = (int) f2;
                view2 = view;
                layoutParams = view.getLayoutParams();
                layoutParams.width = i5;
                view2.setLayoutParams(layoutParams);
                View view4 = ((ItemLayout) view).container;
                ViewGroup.LayoutParams layoutParams2 = view4.getLayoutParams();
                if (layoutParams2 instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) layoutParams2;
                    layoutParams3.gravity = 1;
                    view4.setLayoutParams(layoutParams3);
                }
                measuredHeight = view.getMeasuredHeight();
                int i9 = measuredHeight;
                if (measuredHeight > i7) {
                    i7 = i9;
                }
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(i7, MeasureSpec.EXACTLY));
            return;
        }
        float desiredWidth;
        float f3;
        float f4;
        ItemLayout eVar;
        View view5;
        for (i8 = 0; i8 < childMeasureSpec; i8++) {
            int i10 = i8;
            List list = arrayList;
            desiredWidth = f2 - (Layout.getDesiredWidth(this.menu.getItem(i10).getTitle(), ((ItemLayout) getChildAt(i10)).hwTextView.getPaint()) + ((float) (this.itemTextMargin * 2)));
            f3 = desiredWidth;
            if (desiredWidth > 0.0f) {
                list.add(f3 / 2.0f);
            } else {
                list.add(f3);
            }
        }
        i8 = 0;
        while (i8 < childMeasureSpec) {
            view = getChildAt(i8);
            desiredWidth = (Float) arrayList.get(i8);
            f4 = desiredWidth;
            if (desiredWidth < 0.0f) {
                eVar = (ItemLayout) view;
                view5 = ((ItemLayout) view).isLandscape ? eVar.imageView : eVar.topIcon;
                ViewGroup.LayoutParams layoutParams4 = view5.getLayoutParams();
                if (layoutParams4 instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) layoutParams4;
                    layoutParams5.gravity = 1;
                    setViewLayoutParams(view5, 0, 0, layoutParams5);
                }
                View view6 = ((ItemLayout) view).container;
                setViewLayoutParams(view6, 0, 0, (MarginLayoutParams) view6.getLayoutParams());
                if (i8 == 0 || i8 == childMeasureSpec - 1) {
                    view.measure(MeasureSpec.makeMeasureSpec((int) f2, MeasureSpec.EXACTLY), heightMeasureSpec);
                    i4 = (int) f2;
                } else {
                    f3 = (Float) arrayList.get(i8 - 1);
                    float floatValue = (Float) arrayList.get(i8 + 1);
                    if (f3 < 0.0f || floatValue < 0.0f) {
                        view.measure(MeasureSpec.makeMeasureSpec((int) f2, MeasureSpec.EXACTLY), heightMeasureSpec);
                        i4 = (int) f2;
                    } else {
                        desiredWidth = f3 > floatValue ? floatValue : f3;
                        f3 = desiredWidth;
                        eVar = (ItemLayout) getChildAt(i8 - 1);
                        ItemLayout eVar2 = (ItemLayout) getChildAt(i8 + 1);
                        if (desiredWidth + (f4 / 2.0f) > 0.0f) {
                            view.measure(MeasureSpec.makeMeasureSpec((int) (f2 - f4), MeasureSpec.EXACTLY), heightMeasureSpec);
                            eVar.desiredWidth = (-f4) / 2.0f;
                            eVar2.desiredWidth2 = (-f4) / 2.0f;
                            i4 = (int) (f2 - f4);
                        } else {
                            view.measure(MeasureSpec.makeMeasureSpec((int) ((2.0f * f3) + f2), MeasureSpec.EXACTLY), heightMeasureSpec);
                            eVar.desiredWidth = f3;
                            eVar2.desiredWidth2 = f3;
                            i4 = (int) ((2.0f * f3) + f2);
                        }
                    }
                }
                ((ItemLayout) view).aBoolean = true;
                i5 = i4;
                view2 = view;
                layoutParams = view.getLayoutParams();
                layoutParams.width = i5;
                view2.setLayoutParams(layoutParams);
                i3 += view.getMeasuredWidth();
                measuredHeight = view.getMeasuredHeight();
                widthMeasureSpec = measuredHeight;
                if (measuredHeight > i7) {
                    i7 = widthMeasureSpec;
                }
            }
            i8++;
        }
        for (i8 = 0; i8 < childMeasureSpec; i8++) {
            view = getChildAt(i8);
            if (((ItemLayout) view).aBoolean) {
                ((ItemLayout) view).aBoolean = false;
            } else {
                f4 = ((Float) arrayList.get(i8)).floatValue();
                view5 = ((ItemLayout) view).container;
                setViewLayoutParams(view5, (int) (f4 - ((ItemLayout) view).desiredWidth2), (int) (f4 - ((ItemLayout) view).desiredWidth), (MarginLayoutParams) view5.getLayoutParams());
                eVar = ((ItemLayout) view);
                view5 = ((ItemLayout) view).isLandscape ? eVar.imageView : eVar.topIcon;
                ViewGroup.LayoutParams layoutParams6 = view5.getLayoutParams();
                if (layoutParams6 instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams layoutParams7 = (LinearLayout.LayoutParams) layoutParams6;
                    layoutParams7.gravity = 0;
                    setViewLayoutParams(view5, (int) (((f2 - ((float) this.itemIconSize)) / 2.0f) - ((ItemLayout) view).desiredWidth2), (int) (((f2 - ((float) this.itemIconSize)) / 2.0f) - ((ItemLayout) view).desiredWidth), layoutParams7);
                }
                view.measure(MeasureSpec.makeMeasureSpec((int) ((f2 - ((ItemLayout) view).desiredWidth2) - ((ItemLayout) view).desiredWidth), MeasureSpec.EXACTLY), heightMeasureSpec);
                i5 = (int) ((f2 - ((ItemLayout) view).desiredWidth2) - ((ItemLayout) view).desiredWidth);
                view2 = view;
                layoutParams = view.getLayoutParams();
                layoutParams.width = i5;
                view2.setLayoutParams(layoutParams);
                ((ItemLayout) view).desiredWidth2 = 0.0f;
                ((ItemLayout) view).desiredWidth = 0.0f;
                i3 += view.getMeasuredWidth();
                measuredHeight = view.getMeasuredHeight();
                widthMeasureSpec = measuredHeight;
                if (measuredHeight > i7) {
                    i7 = widthMeasureSpec;
                }
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(i3, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(i7, MeasureSpec.EXACTLY));
    }

    private void ItemClick(ItemLayout itemLayout, boolean fromUser) {
        int selectIndex = itemLayout.selectIndex;
        int index = selectIndex;
        if (selectIndex != this.ActiveIndex || this.mBottomNavListener == null) {
            if (index != this.ActiveIndex) {
                if (this.ActiveIndex < this.menuSize && this.ActiveIndex >= 0) {
                    ((ItemLayout) getChildAt(this.ActiveIndex)).setChecked(false, true);
                    if (this.mBottomNavListener != null) {
                        this.menu.getItem(this.ActiveIndex);
                        mBottomNavListener.OnSelect();
                    }
                }
                this.ActiveIndex = index;
                if (fromUser) {
                    itemLayout.setChecked(true, true);
                }
                if (this.mBottomNavListener != null) {
                    this.menu.getItem(this.ActiveIndex);
                    mBottomNavListener.OnActive(this.ActiveIndex);
                }
            }
            return;
        }
        this.menu.getItem(index);
        mBottomNavListener.Cancel();
    }

    static boolean isDirectionRtl(HwBottomNavigationView hwBottomNavigationView) {
        if (VERSION.SDK_INT >= 17) {
            return hwBottomNavigationView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            return false;
        }
    }
}

