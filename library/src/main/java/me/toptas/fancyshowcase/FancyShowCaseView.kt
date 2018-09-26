/*
 * Copyright (c) 2018. Faruk Toptaş
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.toptas.fancyshowcase

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.text.Spanned
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * FancyShowCaseView class
 */

class FancyShowCaseView : FrameLayout, ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * Builder parameters
     */
    private var mActivity: Activity? = null
    private var title: String? = null
    private var spannedTitle: Spanned? = null
    private var id: String? = null
    private var mFocusCircleRadiusFactor: Double = 1.0
    private var mView: View? = null
    private var mBackgroundColor: Int = 0
    private var mFocusBorderColor: Int = 0
    private var mTitleGravity: Int = -1
    private var mTitleStyle: Int = 0
    private var mTitleSize: Int = -1
    private var mTitleSizeUnit: Int = -1
    private var mCustomViewRes: Int = 0
    private var mFocusBorderSize: Int = 0
    private var mRoundRectRadius: Int = 0
    private var mEnterAnimation: Animation? = null
    private var mExitAnimation: Animation? = null
    private var mAnimationListener: AnimationListener? = null
    private var mCloseOnTouch: Boolean = false
    private var mEnableTouchOnFocusedView: Boolean = false
    private var mFitSystemWindows = false
    private var mFocusShape: FocusShape = FocusShape.CIRCLE
    private var mViewInflateListener: OnViewInflateListener? = null
    private var mDelay: Long = 0
    private val mAnimationDuration = 400
    private var mFocusAnimationMaxValue = 20
    private var mFocusAnimationStep: Int = 1
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mRoot: ViewGroup? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mCalculator: Calculator? = null
    private var mFocusPositionX: Int = 0
    private var mFocusPositionY: Int = 0
    private var mFocusCircleRadius: Int = 0
    private var mFocusRectangleWidth: Int = 0
    private var mFocusRectangleHeight: Int = 0
    private var mFocusAnimationEnabled: Boolean = true
    private var mImageView: FancyImageView? = null
    var dismissListener: DismissListener? = null

    val focusCenterX = mCalculator?.circleCenterX ?: 0

    val focusCenterY = mCalculator?.circleCenterY ?: 0

    val focusRadius = if (FocusShape.CIRCLE == mFocusShape)
        mCalculator?.circleRadius(0, 1.0) ?: 0f
    else 0f

    val focusWidth = mCalculator?.focusWidth ?: 0

    val focusHeight = mCalculator?.focusHeight ?: 0

    internal constructor(context: Context) : super(context)

    internal constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    internal constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    internal constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * Constructor for FancyShowCaseView
     *
     * @param activity                 Activity to show FancyShowCaseView in
     * @param view                     view to focus
     * @param id                       unique identifier for FancyShowCaseView
     * @param title                    title text
     * @param spannedTitle             title text if spanned text should be used
     * @param titleGravity             title gravity
     * @param titleStyle               title text style
     * @param titleSize                title text size
     * @param titleSizeUnit            title text size unit
     * @param focusCircleRadiusFactor  focus circle radius factor (default value = 1)
     * @param backgroundColor          background color of FancyShowCaseView
     * @param focusBorderColor         focus border color of FancyShowCaseView
     * @param focusBorderSize          focus border size of FancyShowCaseView
     * @param customViewRes            custom view layout resource
     * @param viewInflateListener      inflate listener for custom view
     * @param enterAnimation           enter animation for FancyShowCaseView
     * @param exitAnimation            exit animation for FancyShowCaseView
     * @param closeOnTouch             closes on touch if enabled
     * @param enableTouchOnFocusedView closes on touch of focused view if enabled
     * @param fitSystemWindows         should be the same value of root view's fitSystemWindows value
     * @param focusShape               shape of focus, can be circle or rounded rectangle
     * @param dismissListener          listener that gets notified when showcase is dismissed
     * @param roundRectRadius          round rectangle radius
     * @param focusPositionX           focus at specific position X coordinate
     * @param focusPositionY           focus at specific position Y coordinate
     * @param focusCircleRadius        focus at specific position circle radius
     * @param focusRectangleWidth      focus at specific position rectangle width
     * @param focusRectangleHeight     focus at specific position rectangle height
     * @param animationEnabled         flag to enable/disable animation
     */
    private constructor(_activity: Activity,
                        _view: View?,
                        _id: String?,
                        _title: String?,
                        _spannedTitle: Spanned?,
                        _titleGravity: Int,
                        _titleStyle: Int,
                        _titleSize: Int,
                        _titleSizeUnit: Int,
                        _focusCircleRadiusFactor: Double,
                        _backgroundColor: Int,
                        _focusBorderColor: Int,
                        _focusBorderSize: Int,
                        _customViewRes: Int,
                        _viewInflateListener: OnViewInflateListener?,
                        _enterAnimation: Animation?,
                        _exitAnimation: Animation?,
                        _animationListener: AnimationListener?,
                        _closeOnTouch: Boolean,
                        _enableTouchOnFocusedView: Boolean,
                        _fitSystemWindows: Boolean,
                        _focusShape: FocusShape,
                        _dismissListener: DismissListener?,
                        _roundRectRadius: Int,
                        _focusPositionX: Int,
                        _focusPositionY: Int,
                        _focusCircleRadius: Int,
                        _focusRectangleWidth: Int,
                        _focusRectangleHeight: Int,
                        _animationEnabled: Boolean,
                        _focusAnimationMaxValue: Int,
                        _focusAnimationStep: Int,
                        _delay: Long) : super(_activity) {
        id = _id
        mActivity = _activity
        mView = _view
        title = _title
        spannedTitle = _spannedTitle
        mFocusCircleRadiusFactor = _focusCircleRadiusFactor
        mBackgroundColor = _backgroundColor
        mFocusBorderColor = _focusBorderColor
        mFocusBorderSize = _focusBorderSize
        mTitleGravity = _titleGravity
        mTitleStyle = _titleStyle
        mTitleSize = _titleSize
        mTitleSizeUnit = _titleSizeUnit
        mRoundRectRadius = _roundRectRadius
        mCustomViewRes = _customViewRes
        mViewInflateListener = _viewInflateListener
        mEnterAnimation = _enterAnimation
        mExitAnimation = _exitAnimation
        mAnimationListener = _animationListener
        mCloseOnTouch = _closeOnTouch
        mEnableTouchOnFocusedView = _enableTouchOnFocusedView
        mFitSystemWindows = _fitSystemWindows
        mFocusShape = _focusShape
        dismissListener = _dismissListener
        mFocusPositionX = _focusPositionX
        mFocusPositionY = _focusPositionY
        mFocusCircleRadius = _focusCircleRadius
        mFocusRectangleWidth = _focusRectangleWidth
        mFocusRectangleHeight = _focusRectangleHeight
        mFocusAnimationEnabled = _animationEnabled
        mFocusAnimationMaxValue = _focusAnimationMaxValue
        mFocusAnimationStep = _focusAnimationStep
        mDelay = _delay

        initializeParameters()
    }

    /**
     * Calculates and set initial parameters
     */
    private fun initializeParameters() {
        mBackgroundColor = if (mBackgroundColor != 0)
            mBackgroundColor
        else
            ContextCompat.getColor(mActivity!!, R.color.fancy_showcase_view_default_background_color)
        mTitleGravity = if (mTitleGravity >= 0) mTitleGravity else Gravity.CENTER
        mTitleStyle = if (mTitleStyle != 0) mTitleStyle else R.style.FancyShowCaseDefaultTitleStyle

        val displayMetrics = DisplayMetrics()
        mActivity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        mCenterX = deviceWidth / 2
        mCenterY = deviceHeight / 2
        mSharedPreferences = mActivity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Shows FancyShowCaseView
     */
    fun show() {
        if (mActivity == null || id != null && isShownBefore(context, id!!)) {
            dismissListener?.onSkipped(id)
            return
        }
        // if view is not laid out get width/height values in onGlobalLayout
        if (mView != null && mView?.width == 0 && mView?.height == 0) {
            mView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        } else {
            focus()
        }
    }

    private fun focus() {
        mCalculator = Calculator(mActivity!!, mFocusShape, mView, mFocusCircleRadiusFactor,
                mFitSystemWindows)

        val androidContent = mActivity!!.findViewById<View>(android.R.id.content) as ViewGroup
        mRoot = androidContent.parent.parent as ViewGroup?
        mRoot?.postDelayed(Runnable {
            if (mActivity?.isFinishing == true) {
                return@Runnable
            }
            val visibleView = mRoot?.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView?
            isClickable = !mEnableTouchOnFocusedView
            if (visibleView == null) {
                tag = CONTAINER_TAG
                if (mCloseOnTouch) {
                    setupTouchListener()
                }
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                mRoot?.addView(this)

                mImageView = FancyImageView(mActivity!!)
                mImageView!!.setFocusAnimationParameters(mFocusAnimationMaxValue, mFocusAnimationStep)
                if (mCalculator?.hasFocus() == true) {
                    mCenterX = mCalculator!!.circleCenterX
                    mCenterY = mCalculator!!.circleCenterY
                }
                mImageView!!.setParameters(mBackgroundColor, mCalculator!!)
                if (mFocusRectangleWidth > 0 && mFocusRectangleHeight > 0) {
                    mCalculator?.setRectPosition(mFocusPositionX, mFocusPositionY, mFocusRectangleWidth, mFocusRectangleHeight)
                }
                if (mFocusCircleRadius > 0) {
                    mCalculator?.setCirclePosition(mFocusPositionX, mFocusPositionY, mFocusCircleRadius)
                }
                mImageView!!.setAnimationEnabled(mFocusAnimationEnabled)
                mImageView!!.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                if (mFocusBorderColor != 0 && mFocusBorderSize > 0) {
                    mImageView!!.setBorderParameters(mFocusBorderColor, mFocusBorderSize)
                }
                if (mRoundRectRadius > 0) {
                    mImageView!!.setRoundRectRadius(mRoundRectRadius)
                }
                addView(mImageView)


                if (mCustomViewRes == 0) {
                    inflateTitleView()
                } else {
                    inflateCustomView(mCustomViewRes, mViewInflateListener)
                }

                startEnterAnimation()
                writeShown()
            }
        }, mDelay)
    }

    private fun setupTouchListener() {
        if (mEnableTouchOnFocusedView) {
            setOnTouchListener(OnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    var isWithin = false
                    val x = event.x
                    val y = event.y

                    when (mFocusShape) {
                        FocusShape.CIRCLE -> {
                            val distance = Math.sqrt(
                                    Math.pow((focusCenterX - x).toDouble(), 2.0) + Math.pow((focusCenterY - y).toDouble(), 2.0))

                            isWithin = Math.abs(distance) < focusRadius
                        }
                        FocusShape.ROUNDED_RECTANGLE -> {
                            val rect = Rect()
                            val left = focusCenterX - focusWidth / 2
                            val right = focusCenterX + focusWidth / 2
                            val top = focusCenterY - focusHeight / 2
                            val bottom = focusCenterY + focusHeight / 2
                            rect.set(left, top, right, bottom)
                            isWithin = rect.contains(x.toInt(), y.toInt())
                        }
                    }

                    // let the touch event pass on to whoever needs it
                    if (isWithin) {
                        return@OnTouchListener false
                    } else {
                        if (mCloseOnTouch) {
                            hide()
                        }
                    }
                }
                true
            })
        } else {
            setOnClickListener { hide() }
        }

    }

    /**
     * Starts enter animation of FancyShowCaseView
     */
    private fun startEnterAnimation() {
        when {
            mEnterAnimation != null -> startAnimation(mEnterAnimation)
            Utils.shouldShowCircularAnimation() -> doCircularEnterAnimation()
            else -> {
                val fadeInAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_in)
                fadeInAnimation.fillAfter = true
                fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationEnd(animation: Animation) {
                        mAnimationListener?.onEnterAnimationEnd()
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationStart(p0: Animation?) {}
                })
                startAnimation(fadeInAnimation)
            }
        }
    }

    /**
     * Hides FancyShowCaseView with animation
     */
    fun hide() {
        when {
            mExitAnimation != null -> startAnimation(mExitAnimation)
            Utils.shouldShowCircularAnimation() -> doCircularExitAnimation()
            else -> {
                val fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.fscv_fade_out)
                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation) {
                        removeView()
                        mAnimationListener?.onExitAnimationEnd()
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationStart(p0: Animation?) {}
                })
                fadeOut.fillAfter = true
                startAnimation(fadeOut)
            }
        }
    }

    /**
     * Inflates custom view
     *
     * @param layout              layout for custom view
     * @param viewInflateListener inflate listener for custom view
     */
    private fun inflateCustomView(@LayoutRes layout: Int, viewInflateListener: OnViewInflateListener?) {
        mActivity?.layoutInflater?.inflate(layout, this, false)?.apply {
            addView(this)
            viewInflateListener?.onViewInflated(this)
        }

    }

    /**
     * Inflates title view layout
     */
    private fun inflateTitleView() {
        inflateCustomView(R.layout.fancy_showcase_view_layout_title, object : OnViewInflateListener {
            override fun onViewInflated(view: View) {
                val textView = view.findViewById<View>(R.id.fscv_title) as TextView

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(mTitleStyle)
                } else {
                    textView.setTextAppearance(mActivity, mTitleStyle)
                }
                if (mTitleSize != -1) {
                    textView.setTextSize(mTitleSizeUnit, mTitleSize.toFloat())
                }
                textView.gravity = mTitleGravity
                if (mFitSystemWindows) {
                    val params = textView.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0, Utils.getStatusBarHeight(context), 0, 0)
                }
                if (spannedTitle != null) {
                    textView.text = spannedTitle
                } else {
                    textView.text = title
                }
            }
        })

    }

    /**
     * Circular reveal enter animation
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun doCircularEnterAnimation() {
        viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < 16) {
                            viewTreeObserver.removeGlobalOnLayoutListener(this)
                        } else {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }

                        val revealRadius = Math.hypot(width.toDouble(), height.toDouble()).toInt()
                        var startRadius = 0
                        if (mView != null) {
                            startRadius = mView!!.width / 2
                        } else if (mFocusCircleRadius > 0 || mFocusRectangleWidth > 0 || mFocusRectangleHeight > 0) {
                            mCenterX = mFocusPositionX
                            mCenterY = mFocusPositionY
                        }
                        val enterAnimator = ViewAnimationUtils.createCircularReveal(this@FancyShowCaseView,
                                mCenterX, mCenterY, startRadius.toFloat(), revealRadius.toFloat())
                        enterAnimator.duration = mAnimationDuration.toLong()
                        if (mAnimationListener != null) {
                            enterAnimator.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    mAnimationListener?.onEnterAnimationEnd()
                                }
                            })

                        }
                        enterAnimator.interpolator = AnimationUtils.loadInterpolator(mActivity,
                                android.R.interpolator.accelerate_cubic)

                        enterAnimator.start()
                    }
                })

    }

    /**
     * Circular reveal exit animation
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doCircularExitAnimation() {
        val revealRadius = Math.hypot(width.toDouble(), height.toDouble()).toInt()
        val exitAnimator = ViewAnimationUtils.createCircularReveal(this,
                mCenterX, mCenterY, revealRadius.toFloat(), 0f)
        exitAnimator.duration = mAnimationDuration.toLong()
        exitAnimator.interpolator = AnimationUtils.loadInterpolator(mActivity,
                android.R.interpolator.decelerate_cubic)
        exitAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                removeView()
                mAnimationListener?.onExitAnimationEnd()
            }
        })
        exitAnimator.start()


    }

    /**
     * Saves the FancyShowCaseView id to SharedPreferences that is shown once
     */
    private fun writeShown() {
        val editor = mSharedPreferences!!.edit()
        editor.putBoolean(id, true)
        editor.apply()
    }

    /**
     * Removes FancyShowCaseView view from activity root view
     */
    fun removeView() {
        if (mImageView != null)
            mImageView = null
        mRoot?.removeView(this)
        dismissListener?.onDismiss(id)
    }

    override fun onGlobalLayout() {
        if (Build.VERSION.SDK_INT < 16) {
            mView?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
        } else {
            mView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
        focus()
    }


    /**
     * Builder class for [FancyShowCaseView]
     */
    class Builder(private val mActivity: Activity) {
        private var mView: View? = null
        private var mId: String? = null
        private var mTitle: String? = null
        private var mSpannedTitle: Spanned? = null
        private var mFocusCircleRadiusFactor = 1.0
        private var mBackgroundColor: Int = 0
        private var mFocusBorderColor: Int = 0
        private var mTitleGravity = -1
        private var mTitleSize = -1
        private var mTitleSizeUnit = -1
        private var mTitleStyle: Int = 0
        private var mCustomViewRes: Int = 0
        private var mRoundRectRadius: Int = 0
        private var mViewInflateListener: OnViewInflateListener? = null
        private var mEnterAnimation: Animation? = null
        private var mExitAnimation: Animation? = null
        private var mAnimationListener: AnimationListener? = null
        private var mCloseOnTouch = true
        private var mEnableTouchOnFocusedView: Boolean = false
        private var mFitSystemWindows: Boolean = false
        private var mFocusShape = FocusShape.CIRCLE
        private var mDismissListener: DismissListener? = null
        private var mFocusBorderSize: Int = 0
        private var mFocusPositionX: Int = 0
        private var mFocusPositionY: Int = 0
        private var mFocusCircleRadius: Int = 0
        private var mFocusRectangleWidth: Int = 0
        private var mFocusRectangleHeight: Int = 0
        private var mFocusAnimationEnabled = true
        private var mFocusAnimationMaxValue = 20
        private var mFocusAnimationStep = 1
        private var mDelay: Long = 0


        /**
         * @param title title text
         * @return Builder
         */
        fun title(title: String): Builder {
            mTitle = title
            mSpannedTitle = null
            return this
        }

        /**
         * @param title title text
         * @return Builder
         */
        fun title(title: Spanned): Builder {
            mSpannedTitle = title
            mTitle = null
            return this
        }

        /**
         * @param style        title text style
         * @param titleGravity title gravity
         * @return Builder
         */
        fun titleStyle(@StyleRes style: Int, titleGravity: Int): Builder {
            mTitleGravity = titleGravity
            mTitleStyle = style
            return this
        }

        /**
         * @param focusBorderColor Border color for focus shape
         * @return Builder
         */
        fun focusBorderColor(focusBorderColor: Int): Builder {
            mFocusBorderColor = focusBorderColor
            return this
        }

        /**
         * @param focusBorderSize Border size for focus shape
         * @return Builder
         */
        fun focusBorderSize(focusBorderSize: Int): Builder {
            mFocusBorderSize = focusBorderSize
            return this
        }

        /**
         * @param titleGravity title gravity
         * @return Builder
         */
        fun titleGravity(titleGravity: Int): Builder {
            mTitleGravity = titleGravity
            return this
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param titleSize title size
         * @param unit      title text unit
         * @return Builder
         */
        fun titleSize(titleSize: Int, unit: Int): Builder {
            mTitleSize = titleSize
            mTitleSizeUnit = unit
            return this
        }

        /**
         * @param id unique identifier for FancyShowCaseView
         * @return Builder
         */
        fun showOnce(id: String): Builder {
            mId = id
            return this
        }

        /**
         * @param view view to focus
         * @return Builder
         */
        fun focusOn(view: View): Builder {
            mView = view
            return this
        }

        /**
         * @param backgroundColor background color of FancyShowCaseView
         * @return Builder
         */
        fun backgroundColor(backgroundColor: Int): Builder {
            mBackgroundColor = backgroundColor
            return this
        }

        /**
         * @param focusCircleRadiusFactor focus circle radius factor (default value = 1)
         * @return Builder
         */
        fun focusCircleRadiusFactor(focusCircleRadiusFactor: Double): Builder {
            mFocusCircleRadiusFactor = focusCircleRadiusFactor
            return this
        }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        fun customView(@LayoutRes layoutResource: Int, listener: OnViewInflateListener?): Builder {
            mCustomViewRes = layoutResource
            mViewInflateListener = listener
            return this
        }

        /**
         * @param enterAnimation enter animation for FancyShowCaseView
         * @return Builder
         */
        fun enterAnimation(enterAnimation: Animation): Builder {
            mEnterAnimation = enterAnimation
            return this
        }

        /**
         * Listener for enter/exit animations
         *
         * @param listener animation listener
         * @return Builder
         */
        fun animationListener(listener: AnimationListener): Builder {
            mAnimationListener = listener
            return this
        }

        /**
         * @param exitAnimation exit animation for FancyShowCaseView
         * @return Builder
         */
        fun exitAnimation(exitAnimation: Animation): Builder {
            mExitAnimation = exitAnimation
            return this
        }

        /**
         * @param closeOnTouch closes on touch if enabled
         * @return Builder
         */
        fun closeOnTouch(closeOnTouch: Boolean): Builder {
            mCloseOnTouch = closeOnTouch
            return this
        }

        /**
         * @param enableTouchOnFocusedView closes on touch of focused view if enabled
         * @return Builder
         */
        fun enableTouchOnFocusedView(enableTouchOnFocusedView: Boolean): Builder {
            mEnableTouchOnFocusedView = enableTouchOnFocusedView
            return this
        }

        /**
         * This should be the same as root view's fitSystemWindows value
         *
         * @param fitSystemWindows fitSystemWindows value
         * @return Builder
         */
        fun fitSystemWindows(fitSystemWindows: Boolean): Builder {
            mFitSystemWindows = fitSystemWindows
            return this
        }

        fun focusShape(focusShape: FocusShape): Builder {
            mFocusShape = focusShape
            return this
        }

        /**
         * Focus round rectangle at specific position
         *
         * @param positionX      focus at specific position Y coordinate
         * @param positionY      focus at specific position circle radius
         * @param positionWidth  focus at specific position rectangle width
         * @param positionHeight focus at specific position rectangle height
         * @return Builder
         */

        fun focusRectAtPosition(positionX: Int, positionY: Int, positionWidth: Int, positionHeight: Int): Builder {
            mFocusPositionX = positionX
            mFocusPositionY = positionY
            mFocusRectangleWidth = positionWidth
            mFocusRectangleHeight = positionHeight
            return this
        }

        /**
         * Focus circle at specific position
         *
         * @param positionX focus at specific position Y coordinate
         * @param positionY focus at specific position circle radius
         * @param radius    focus at specific position circle radius
         * @return Builder
         */

        fun focusCircleAtPosition(positionX: Int, positionY: Int, radius: Int): Builder {
            mFocusPositionX = positionX
            mFocusPositionY = positionY
            mFocusCircleRadius = radius
            return this
        }

        /**
         * @param dismissListener the dismiss listener
         * @return Builder
         */
        fun dismissListener(dismissListener: DismissListener): Builder {
            mDismissListener = dismissListener
            return this
        }

        fun roundRectRadius(roundRectRadius: Int): Builder {
            mRoundRectRadius = roundRectRadius
            return this
        }

        /**
         * disable Focus Animation
         *
         * @return Builder
         */
        fun disableFocusAnimation(): Builder {
            mFocusAnimationEnabled = false
            return this
        }

        fun focusAnimationMaxValue(focusAnimationMaxValue: Int): Builder {
            mFocusAnimationMaxValue = focusAnimationMaxValue
            return this
        }

        fun focusAnimationStep(focusAnimationStep: Int): Builder {
            mFocusAnimationStep = focusAnimationStep
            return this
        }

        fun delay(delayInMillis: Int): Builder {
            mDelay = delayInMillis.toLong()
            return this
        }

        /**
         * builds the builder
         *
         * @return [FancyShowCaseView] with given parameters
         */
        fun build(): FancyShowCaseView {
            return FancyShowCaseView(mActivity, mView, mId, mTitle, mSpannedTitle, mTitleGravity, mTitleStyle, mTitleSize, mTitleSizeUnit,
                    mFocusCircleRadiusFactor, mBackgroundColor, mFocusBorderColor, mFocusBorderSize, mCustomViewRes, mViewInflateListener,
                    mEnterAnimation, mExitAnimation, mAnimationListener, mCloseOnTouch, mEnableTouchOnFocusedView, mFitSystemWindows, mFocusShape, mDismissListener, mRoundRectRadius,
                    mFocusPositionX, mFocusPositionY, mFocusCircleRadius, mFocusRectangleWidth, mFocusRectangleHeight, mFocusAnimationEnabled,
                    mFocusAnimationMaxValue, mFocusAnimationStep, mDelay)
        }
    }

    companion object {

        // Tag for container view
        private val CONTAINER_TAG = "ShowCaseViewTag"
        // SharedPreferences name
        private val PREF_NAME = "PrefShowCaseView"

        /**
         * Resets the show once flag
         *
         * @param context context that should be used to create the shared preference instance
         * @param id      id of the show once flag that should be reset
         */
        fun resetShowOnce(context: Context, id: String) {
            val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPrefs.edit().remove(id).commit()
        }

        /**
         * Resets all show once flags
         *
         * @param context context that should be used to create the shared preference instance
         */
        fun resetAllShowOnce(context: Context) {
            val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().commit()
        }

        /**
         * Check is FancyShowCaseView visible
         *
         * @param activity should be used to find FancyShowCaseView inside it
         */
        fun isVisible(activity: Activity): Boolean {
            val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val mRoot = androidContent.parent.parent as ViewGroup
            val mContainer = mRoot.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView?
            return mContainer != null
        }

        /**
         * Hide  FancyShowCaseView
         *
         * @param activity should be used to hide FancyShowCaseView inside it
         */
        fun hideCurrent(activity: Activity) {
            val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val mRoot = androidContent.parent.parent as ViewGroup
            val mContainer = mRoot.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView
            mContainer.hide()
        }

        fun isShownBefore(context: Context, id: String): Boolean {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return pref.getBoolean(id, false)
        }
    }
}
