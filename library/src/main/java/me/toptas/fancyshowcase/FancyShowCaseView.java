package me.toptas.fancyshowcase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by faruktoptas on 05/03/17.
 * FancyShowCaseView class
 */

public class FancyShowCaseView {


    // Tag for container view
    private static final String CONTAINER_TAG = "ShowCaseViewTag";
    // SharedPreferences name
    private static final String PREF_NAME = "PrefShowCaseView";

    /**
     * resets the show once flag
     *
     * @param context context that should be used to create the shared preference instance
     * @param id id of the show once flag that should be reset
     */
    public static void resetShowOnce(Context context, String id) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.edit().remove(id).commit();
        return;
    }

    /**
     * resets all show once flags
     *
     * @param context context that should be used to create the shared preference instance
     */
    public static void resetAllShowOnce(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().commit();
        return;
    }

    /**
     * Builder parameters
     */
    private final Activity mActivity;
    private String mTitle;
    private Spanned mSpannedTitle;
    private String mId;
    private double mFocusCircleRadiusFactor;
    private View mView;
    private int mBackgroundColor;
    private int mFocusBorderColor;
    private int mTitleGravity;
    private int mTitleStyle;
    private int mTitleSize;
    private int mTitleSizeUnit;
    private int mCustomViewRes;
    private int mFocusBorderSize;
    private int mRoundRectRadius;
    private OnViewInflateListener mViewInflateListener;
    private Animation mEnterAnimation, mExitAnimation;
    private boolean mCloseOnTouch;
    private boolean mFitSystemWindows;
    private FocusShape mFocusShape;
    private DismissListener mDismissListener;


    private int mAnimationDuration = 400;
    private int mCenterX, mCenterY, mRadius;
    private FrameLayout mContainer;
    private ViewGroup mRoot;
    private SharedPreferences mSharedPreferences;
    private Calculator mCalculator;

    /**
     * Constructor for FancyShowCaseView
     *
     * @param activity                Activity to show FancyShowCaseView in
     * @param view                    view to focus
     * @param id                      unique identifier for FancyShowCaseView
     * @param title                   title text
     * @param spannedTitle            title text if spanned text should be used
     * @param titleGravity            title gravity
     * @param titleStyle              title text style
     * @param titleSize               title text size
     * @param titleSizeUnit           title text size unit
     * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
     * @param backgroundColor         background color of FancyShowCaseView
     * @param focusBorderColor        focus border color of FancyShowCaseView
     * @param focusBorderSize         focus border size of FancyShowCaseView
     * @param customViewRes           custom view layout resource
     * @param viewInflateListener     inflate listener for custom view
     * @param enterAnimation          enter animation for FancyShowCaseView
     * @param exitAnimation           exit animation for FancyShowCaseView
     * @param closeOnTouch            closes on touch if enabled
     * @param fitSystemWindows        should be the same value of root view's fitSystemWindows value
     * @param focusShape              shape of focus, can be circle or rounded rectangle
     * @param dismissListener         listener that gets notified when showcase is dismissed
     */
    private FancyShowCaseView(Activity activity, View view, String id, String title, Spanned spannedTitle,
                              int titleGravity, int titleStyle, int titleSize, int titleSizeUnit, double focusCircleRadiusFactor,
                              int backgroundColor, int focusBorderColor, int focusBorderSize, int customViewRes,
                              OnViewInflateListener viewInflateListener, Animation enterAnimation,
                              Animation exitAnimation, boolean closeOnTouch, boolean fitSystemWindows,
                              FocusShape focusShape, DismissListener dismissListener, int roundRectRadius) {
        mId = id;
        mActivity = activity;
        mView = view;
        mTitle = title;
        mSpannedTitle = spannedTitle;
        mFocusCircleRadiusFactor = focusCircleRadiusFactor;
        mBackgroundColor = backgroundColor;
        mFocusBorderColor = focusBorderColor;
        mFocusBorderSize = focusBorderSize;
        mTitleGravity = titleGravity;
        mTitleStyle = titleStyle;
        mTitleSize = titleSize;
        mTitleSizeUnit = titleSizeUnit;
        mRoundRectRadius = roundRectRadius;
        mCustomViewRes = customViewRes;
        mViewInflateListener = viewInflateListener;
        mEnterAnimation = enterAnimation;
        mExitAnimation = exitAnimation;
        mCloseOnTouch = closeOnTouch;
        mFitSystemWindows = fitSystemWindows;
        mFocusShape = focusShape;
        mDismissListener = dismissListener;

        initializeParameters();
    }

    /**
     * Calculates and set initial parameters
     */
    private void initializeParameters() {
        mBackgroundColor = mBackgroundColor != 0 ? mBackgroundColor :
                mActivity.getResources().getColor(R.color.fancy_showcase_view_default_background_color);
        mTitleGravity = mTitleGravity >= 0 ? mTitleGravity : Gravity.CENTER;
        mTitleStyle = mTitleStyle != 0 ? mTitleStyle : R.style.FancyShowCaseDefaultTitleStyle;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        mCenterX = deviceWidth / 2;
        mCenterY = deviceHeight / 2;
        mSharedPreferences = mActivity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Shows FancyShowCaseView
     */
    public void show() {
        if (mActivity == null || (mId != null && isShownBefore())) {
            if (mDismissListener != null) {
                mDismissListener.onSkipped(mId);
            }
            return;
        }

        mCalculator = new Calculator(mActivity, mFocusShape, mView, mFocusCircleRadiusFactor,
                mFitSystemWindows);
        Bitmap bitmap = Bitmap.createBitmap(mCalculator.getBitmapWidth(), mCalculator.getBitmapHeight(),
                Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(mBackgroundColor);

        ViewGroup androidContent = (ViewGroup) mActivity.findViewById(android.R.id.content);
        mRoot = (ViewGroup) androidContent.getParent().getParent();
        mContainer = (FrameLayout) mRoot.findViewWithTag(CONTAINER_TAG);
        if (mContainer == null) {
            mContainer = new FrameLayout(mActivity);
            mContainer.setTag(CONTAINER_TAG);
            if (mCloseOnTouch) {
                mContainer.setClickable(true);
                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hide();
                    }
                });
            }
            mContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mRoot.addView(mContainer);


            FancyImageView imageView = new FancyImageView(mActivity);
            if (mCalculator.hasFocus()) {
                //Utils.drawFocusCircle(bitmap, focusPoint, focusPoint[2]);
                mCenterX = mCalculator.getCircleCenterX();
                mCenterY = mCalculator.getCircleCenterY();
                mRadius = mCalculator.getViewRadius();
            }

            imageView.setParameters(mBackgroundColor, mCalculator);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (mFocusBorderColor != 0 && mFocusBorderSize > 0) {
                imageView.setBorderParameters(mFocusBorderColor, mFocusBorderSize);
            }
            if (mRoundRectRadius > 0) {
                imageView.setRoundRectRadius(mRoundRectRadius);
            }
            //imageView.setImageBitmap(bitmap);
            mContainer.addView(imageView);

            if (mCustomViewRes == 0) {
                inflateTitleView();
            } else {
                inflateCustomView(mCustomViewRes, mViewInflateListener);
            }

            startEnterAnimation();
            writeShown();
        }
    }

/**
* Check is FancyShowCaseView visible
*@param activity should be used to find FancyShowCaseView inside it
*
* */
    public static Boolean isVisible(Activity activity) {
        ViewGroup androidContent = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup mRoot = (ViewGroup) androidContent.getParent().getParent();
        FrameLayout mContainer = (FrameLayout) mRoot.findViewWithTag(CONTAINER_TAG);
        return mContainer != null;
    }
    /**
     * Hide  FancyShowCaseView
     *@param activity should be used to hide FancyShowCaseView inside it
     *
     * */
    public static void hideCurrent(Activity activity){
        ViewGroup androidContent = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup mRoot = (ViewGroup) androidContent.getParent().getParent();
        FrameLayout mContainer = (FrameLayout) mRoot.findViewWithTag(CONTAINER_TAG);
        mRoot.removeView(mContainer);
    }

    /**
     * Starts enter animation of FancyShowCaseView
     */
    private void startEnterAnimation() {
        if (mEnterAnimation != null) {
            mContainer.startAnimation(mEnterAnimation);
        } else if (Utils.shouldShowCircularAnimation()) {
            doCircularEnterAnimation();
        } else {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_in);
            fadeInAnimation.setFillAfter(true);
            mContainer.startAnimation(fadeInAnimation);
        }
    }

    /**
     * Hides FancyShowCaseView with animation
     */
    public void hide() {
        if (mExitAnimation != null) {
            mContainer.startAnimation(mExitAnimation);
        } else if (Utils.shouldShowCircularAnimation()) {
            doCircularExitAnimation();
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeView();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fadeOut.setFillAfter(true);
            mContainer.startAnimation(fadeOut);
        }
    }

    /**
     * Inflates custom view
     *
     * @param layout              layout for custom view
     * @param viewInflateListener inflate listener for custom view
     */
    private void inflateCustomView(@LayoutRes int layout, OnViewInflateListener viewInflateListener) {
        View view = mActivity.getLayoutInflater().inflate(layout, mContainer, false);
        mContainer.addView(view);
        if (viewInflateListener != null) {
            viewInflateListener.onViewInflated(view);
        }
    }

    /**
     * Inflates title view layout
     */
    private void inflateTitleView() {
        inflateCustomView(R.layout.fancy_showcase_view_layout_title, new OnViewInflateListener() {
            @Override
            public void onViewInflated(View view) {
                TextView textView = (TextView) view.findViewById(R.id.fscv_title);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(mTitleStyle);
                } else {
                    textView.setTextAppearance(mActivity, mTitleStyle);
                }
                if (mTitleSize != -1) {
                    textView.setTextSize(mTitleSizeUnit, mTitleSize);
                }
                textView.setGravity(mTitleGravity);
                if (mSpannedTitle != null) {
                    textView.setText(mSpannedTitle);
                } else {
                    textView.setText(mTitle);
                }
            }
        });

    }

    /**
     * Circular reveal enter animation
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void doCircularEnterAnimation() {
        mContainer.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                        final int revealRadius = (int) Math.hypot(
                                mContainer.getWidth(), mContainer.getHeight());
                        int startRadius = 0;
                        if (mView != null) {
                            startRadius = mView.getWidth() / 2;
                        }
                        Animator enterAnimator = ViewAnimationUtils.createCircularReveal(mContainer,
                                mCenterX, mCenterY, startRadius, revealRadius);
                        enterAnimator.setDuration(mAnimationDuration);
                        enterAnimator.setInterpolator(AnimationUtils.loadInterpolator(mActivity,
                                android.R.interpolator.accelerate_cubic));
                        enterAnimator.start();
                        return false;
                    }
                });

    }

    /**
     * Circular reveal exit animation
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doCircularExitAnimation() {
        final int revealRadius = (int) Math.hypot(mContainer.getWidth(), mContainer.getHeight());
        Animator exitAnimator = ViewAnimationUtils.createCircularReveal(mContainer,
                mCenterX, mCenterY, revealRadius, 0f);
        exitAnimator.setDuration(mAnimationDuration);
        exitAnimator.setInterpolator(AnimationUtils.loadInterpolator(mActivity,
                android.R.interpolator.decelerate_cubic));
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView();

            }
        });
        exitAnimator.start();


    }

    /**
     * Saves the FancyShowCaseView id to SharedPreferences that is shown once
     */
    private void writeShown() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mId, true);
        editor.apply();
    }

    /**
     * Returns if FancyShowCaseView with given id is shown before
     *
     * @return true if show before
     */
    public boolean isShownBefore() {
        return mSharedPreferences.getBoolean(mId, false);
    }

    /**
     * Removes FancyShowCaseView view from activity root view
     */
    public void removeView() {
        mRoot.removeView(mContainer);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(mId);
        }
    }

    /**
     * Returns if FancyShowCaseView is showing
     *
     * @return true if showing
     */
    public boolean isShowing() {
        return mContainer.isShown();
    }

    /**
     * Returns FrameLayout
     *
     * @return FrameLayout used to display {@link FancyShowCaseView}
     */
    public FrameLayout getContainerView() {
        return mContainer;
    }

    protected DismissListener getDismissListener() {
        return mDismissListener;
    }

    protected void setDismissListener(DismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    /**
     * Returns if current shape has a radius. Have to add more cases if new shapes doesn't have radius
     *
     * @return true if current shape has a radius
     */
    public boolean hasRadius(){

        if(mFocusShape.equals(FocusShape.CIRCLE))
            return true;

        return false;
    }

    /**
     * Returns mRadius or -1 if mRadius == 0 (i.e. Radius doesn't exist for the shape)
     *
     * @return mRadius or -1 if mRadius == 0
     */
    public int getFocusRadius(){
        if(hasRadius())
            return mRadius;
        else
            return -1;
    }

    public Point getFocusPosition(){
        return new Point(mCenterX,mCenterY);
    }


    /**
     * Builder class for {@link FancyShowCaseView}
     */
    public static class Builder {

        private Activity mActivity;
        private View mView;
        private String mId;
        private String mTitle;
        private Spanned mSpannedTitle;
        private double mFocusCircleRadiusFactor = 1;
        private int mBackgroundColor;
        private int mFocusBorderColor;
        private int mTitleGravity = -1;
        private int mTitleSize = -1;
        private int mTitleSizeUnit = -1;
        private int mTitleStyle;
        private int mCustomViewRes;
        private int mRoundRectRadius;
        private OnViewInflateListener mViewInflateListener;
        private Animation mEnterAnimation, mExitAnimation;
        private boolean mCloseOnTouch = true;
        private boolean mFitSystemWindows;
        private FocusShape mFocusShape = FocusShape.CIRCLE;
        private DismissListener mDismissListener = null;
        private int mFocusBorderSize;

        /**
         * Constructor for Builder class
         *
         * @param activity Activity to show FancyShowCaseView in
         */
        public Builder(Activity activity) {
            mActivity = activity;
        }


        /**
         * @param title title text
         * @return Builder
         */
        public Builder title(String title) {
            mTitle = title;
            mSpannedTitle = null;
            return this;
        }

        /**
         * @param title title text
         * @return Builder
         */
        public Builder title(Spanned title) {
            mSpannedTitle = title;
            mTitle = null;
            return this;
        }

        /**
         * @param style        title text style
         * @param titleGravity title gravity
         * @return Builder
         */
        public Builder titleStyle(@StyleRes int style, int titleGravity) {
            mTitleGravity = titleGravity;
            mTitleStyle = style;
            return this;
        }

        /**
         *
         * @param focusBorderColor
         * @return Builder
         */
        public Builder focusBorderColor(int focusBorderColor) {
            mFocusBorderColor = focusBorderColor;
            return this;
        }

        /**
         *
         * @param focusBorderSize
         * @return Builder
         */
        public Builder focusBorderSize(int focusBorderSize) {
            mFocusBorderSize = focusBorderSize;
            return this;
        }

        /**
         * @param titleGravity title gravity
         * @return Builder
         */
        public Builder titleGravity(int titleGravity) {
            mTitleGravity = titleGravity;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param titleSize title size
         * @param unit title text unit
         * @return Builder
         */
        public Builder titleSize(int titleSize, int unit) {
            mTitleSize = titleSize;
            mTitleSizeUnit = unit;
            return this;
        }

        /**
         * @param id unique identifier for FancyShowCaseView
         * @return Builder
         */
        public Builder showOnce(String id) {
            mId = id;
            return this;
        }

        /**
         * @param view view to focus
         * @return Builder
         */
        public Builder focusOn(View view) {
            mView = view;
            return this;
        }

        /**
         * @param backgroundColor background color of FancyShowCaseView
         * @return Builder
         */
        public Builder backgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        /**
         * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
         * @return Builder
         */
        public Builder focusCircleRadiusFactor(double focusCircleRadiusFactor) {
            mFocusCircleRadiusFactor = focusCircleRadiusFactor;
            return this;
        }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        public Builder customView(@LayoutRes int layoutResource, @Nullable OnViewInflateListener listener) {
            mCustomViewRes = layoutResource;
            mViewInflateListener = listener;
            return this;
        }

        /**
         * @param enterAnimation enter animation for FancyShowCaseView
         * @return Builder
         */
        public Builder enterAnimation(Animation enterAnimation) {
            mEnterAnimation = enterAnimation;
            return this;
        }

        /**
         * @param exitAnimation exit animation for FancyShowCaseView
         * @return Builder
         */
        public Builder exitAnimation(Animation exitAnimation) {
            mExitAnimation = exitAnimation;
            return this;
        }

        /**
         * @param closeOnTouch closes on touch if enabled
         * @return Builder
         */
        public Builder closeOnTouch(boolean closeOnTouch) {
            mCloseOnTouch = closeOnTouch;
            return this;
        }

        /**
         * This should be the same as root view's fitSystemWindows value
         *
         * @param fitSystemWindows fitSystemWindows value
         * @return Builder
         */
        public Builder fitSystemWindows(boolean fitSystemWindows) {
            mFitSystemWindows = fitSystemWindows;
            return this;
        }

        public Builder focusShape(FocusShape focusShape) {
            mFocusShape = focusShape;
            return this;
        }

        /**
         * @param dismissListener the dismiss listener
         * @return Builder
         */
        public Builder dismissListener(DismissListener dismissListener) {
            mDismissListener = dismissListener;
            return this;
        }

        public Builder roundRectRadius(int roundRectRadius) {
            mRoundRectRadius = roundRectRadius;
            return this;
        }

        /**
         * builds the builder
         *
         * @return {@link FancyShowCaseView} with given parameters
         */
        public FancyShowCaseView build() {
            return new FancyShowCaseView(mActivity, mView, mId, mTitle, mSpannedTitle, mTitleGravity, mTitleStyle, mTitleSize, mTitleSizeUnit,
                    mFocusCircleRadiusFactor, mBackgroundColor, mFocusBorderColor, mFocusBorderSize, mCustomViewRes, mViewInflateListener,
                    mEnterAnimation, mExitAnimation, mCloseOnTouch, mFitSystemWindows, mFocusShape, mDismissListener, mRoundRectRadius);
        }
    }
}
