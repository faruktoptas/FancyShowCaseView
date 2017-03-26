package me.toptas.fancyshowcase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
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
    public static void resetAllShowOnce(Context context, String id) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().commit();
        return;
    }

    /**
     * Builder parameters
     */
    private final Activity mActivity;
    private String mTitle;
    private String mId;
    private double mFocusCircleRadiusFactor;
    private View mView;
    private int mBackgroundColor;
    private int mTitleGravity;
    private int mTitleStyle;
    private int mCustomViewRes;
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
     * @param titleGravity            title gravity
     * @param titleStyle              title text style
     * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
     * @param backgroundColor         background color of FancyShowCaseView
     * @param customViewRes           custom view layout resource
     * @param viewInflateListener     inflate listener for custom view
     * @param enterAnimation          enter animation for FancyShowCaseView
     * @param exitAnimation           exit animation for FancyShowCaseView
     * @param closeOnTouch            closes on touch if enabled
     * @param fitSystemWindows        should be the same value of root view's fitSystemWindows value
     * @param focusShape              shape of focus, can be circle or rounded rectangle
     * @param dismissListener         listener that gets notified when showcase is dismissed
     */
    private FancyShowCaseView(Activity activity, View view, String id, String title,
                              int titleGravity, int titleStyle, double focusCircleRadiusFactor,
                              int backgroundColor, int customViewRes,
                              OnViewInflateListener viewInflateListener, Animation enterAnimation,
                              Animation exitAnimation, boolean closeOnTouch, boolean fitSystemWindows,
                              FocusShape focusShape, DismissListener dismissListener) {
        mId = id;
        mActivity = activity;
        mView = view;
        mTitle = title;
        mFocusCircleRadiusFactor = focusCircleRadiusFactor;
        mBackgroundColor = backgroundColor;
        mTitleGravity = titleGravity;
        mTitleStyle = titleStyle;
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
                textView.setGravity(mTitleGravity);
                textView.setText(mTitle);
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


    /**
     * Builder class for {@link FancyShowCaseView}
     */
    public static class Builder {

        private Activity mActivity;
        private View mView;
        private String mId;
        private String mTitle;
        private double mFocusCircleRadiusFactor = 1;
        private int mBackgroundColor;
        private int mTitleGravity = -1;
        private int mTitleStyle;
        private int mCustomViewRes;
        private OnViewInflateListener mViewInflateListener;
        private Animation mEnterAnimation, mExitAnimation;
        private boolean mCloseOnTouch = true;
        private boolean mFitSystemWindows;
        private FocusShape mFocusShape = FocusShape.CIRCLE;
        private DismissListener mDismissListener = null;

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

        /**
         * builds the builder
         *
         * @return {@link FancyShowCaseView} with given parameters
         */
        public FancyShowCaseView build() {
            return new FancyShowCaseView(mActivity, mView, mId, mTitle, mTitleGravity, mTitleStyle,
                    mFocusCircleRadiusFactor, mBackgroundColor, mCustomViewRes, mViewInflateListener,
                    mEnterAnimation, mExitAnimation, mCloseOnTouch, mFitSystemWindows, mFocusShape, mDismissListener);
        }
    }
}
