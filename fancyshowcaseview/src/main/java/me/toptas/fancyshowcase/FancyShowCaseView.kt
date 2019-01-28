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
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import me.toptas.fancyshowcase.listener.AnimationListener
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnQueueListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener

/**
 * FancyShowCaseView class
 */

class FancyShowCaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * Builder parameters
     */
    private lateinit var activity: Activity
    private var title: String? = null
    private var spannedTitle: Spanned? = null
    private var id: String? = null
    private var focusCircleRadiusFactor: Double = 1.0
    private var focusedView: View? = null
    private var clickableView: View? = null
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
    private var fitSystemWindows = false
    private var mFocusShape: FocusShape = FocusShape.CIRCLE
    private var viewInflateListener: OnViewInflateListener? = null
    private var delay: Long = 0
    private var autoPosText = false
    private val mAnimationDuration = 400
    private var mFocusAnimationMaxValue = 20
    private var mFocusAnimationStep: Int = 1
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mRoot: ViewGroup? = null
    private var sharedPreferences: SharedPreferences? = null
    var focusCalculator: Calculator? = null
    private var clickableCalculator: Calculator? = null
    private var mFocusPositionX: Int = 0
    private var mFocusPositionY: Int = 0
    private var mFocusCircleRadius: Int = 0
    private var mFocusRectangleWidth: Int = 0
    private var mFocusRectangleHeight: Int = 0
    private var focusAnimationEnabled: Boolean = true
    private var fancyImageView: FancyImageView? = null
    var dismissListener: DismissListener? = null
    var queueListener: OnQueueListener? = null

    /**
     * Constructor for FancyShowCaseView
     *
     * @param _activity                 Activity to show FancyShowCaseView in
     * @param _focusView                view to focus
     * @param _clickableView            view to be clickable
     * @param _id                       unique identifier for FancyShowCaseView
     * @param _title                    title text
     * @param _spannedTitle             title text if spanned text should be used
     * @param _titleGravity             title gravity
     * @param _titleStyle               title text style
     * @param _titleSize                title text size
     * @param _titleSizeUnit            title text size unit
     * @param _focusCircleRadiusFactor  focus circle radius factor (default value = 1)
     * @param _backgroundColor          background color of FancyShowCaseView
     * @param _focusBorderColor         focus border color of FancyShowCaseView
     * @param _focusBorderSize          focus border size of FancyShowCaseView
     * @param _customViewRes            custom view layout resource
     * @param _viewInflateListener      inflate listener for custom view
     * @param _enterAnimation           enter animation for FancyShowCaseView
     * @param _exitAnimation            exit animation for FancyShowCaseView
     * @param _closeOnTouch             closes on touch if enabled
     * @param _enableTouchOnFocusedView closes on touch of focused view if enabled
     * @param _fitSystemWindows         should be the same value of root view's fitSystemWindows value
     * @param _focusShape               shape of focus, can be circle or rounded rectangle
     * @param _dismissListener          listener that gets notified when showcase is dismissed
     * @param _roundRectRadius          round rectangle radius
     * @param _focusPositionX           focus at specific position X coordinate
     * @param _focusPositionY           focus at specific position Y coordinate
     * @param _focusCircleRadius        focus at specific position circle radius
     * @param _focusRectangleWidth      focus at specific position rectangle width
     * @param _focusRectangleHeight     focus at specific position rectangle height
     * @param _animationEnabled         flag to enable/disable animation
     */
    private constructor(_activity: Activity,
                        _focusView: View?,
                        _clickableView: View?,
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
                        _delay: Long,
                        _autoPosText: Boolean) : this(_activity) {

        requireNotNull(_activity)
        id = _id
        activity = _activity
        focusedView = _focusView
        clickableView = _clickableView
        title = _title
        spannedTitle = _spannedTitle
        focusCircleRadiusFactor = _focusCircleRadiusFactor
        mBackgroundColor = _backgroundColor
        mFocusBorderColor = _focusBorderColor
        mFocusBorderSize = _focusBorderSize
        mTitleGravity = _titleGravity
        mTitleStyle = _titleStyle
        mTitleSize = _titleSize
        mTitleSizeUnit = _titleSizeUnit
        mRoundRectRadius = _roundRectRadius
        mCustomViewRes = _customViewRes
        viewInflateListener = _viewInflateListener
        mEnterAnimation = _enterAnimation
        mExitAnimation = _exitAnimation
        mAnimationListener = _animationListener
        mCloseOnTouch = _closeOnTouch
        mEnableTouchOnFocusedView = _enableTouchOnFocusedView
        fitSystemWindows = _fitSystemWindows
        mFocusShape = _focusShape
        dismissListener = _dismissListener
        mFocusPositionX = _focusPositionX
        mFocusPositionY = _focusPositionY
        mFocusCircleRadius = _focusCircleRadius
        mFocusRectangleWidth = _focusRectangleWidth
        mFocusRectangleHeight = _focusRectangleHeight
        focusAnimationEnabled = _animationEnabled
        mFocusAnimationMaxValue = _focusAnimationMaxValue
        mFocusAnimationStep = _focusAnimationStep
        delay = _delay
        autoPosText = _autoPosText

        initializeParameters()
    }

    /**
     * Calculates and set initial parameters
     */
    private fun initializeParameters() {
        mBackgroundColor = if (mBackgroundColor != 0)
            mBackgroundColor
        else
            ContextCompat.getColor(activity, R.color.fancy_showcase_view_default_background_color)
        mTitleGravity = if (mTitleGravity >= 0) mTitleGravity else Gravity.CENTER
        mTitleStyle = if (mTitleStyle != 0) mTitleStyle else R.style.FancyShowCaseDefaultTitleStyle

        val displayMetrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        mCenterX = deviceWidth / 2
        mCenterY = deviceHeight / 2
        sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Shows FancyShowCaseView
     */
    fun show() {
        if (id != null && isShownBefore(context, id!!)) {
            dismissListener?.onSkipped(id)
            return
        }
        // if view is not laid out get width/height values in onGlobalLayout
        if (focusedView != null && focusedView?.width == 0 && focusedView?.height == 0) {
            focusedView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        } else {
            focus()
        }
    }

    private fun focus() {
        focusCalculator = Calculator(activity,
                mFocusShape,
                focusedView,
                focusCircleRadiusFactor,
                fitSystemWindows)

        clickableCalculator = Calculator(activity,
                mFocusShape,
                clickableView,
                focusCircleRadiusFactor,
                fitSystemWindows)

        val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
        mRoot = androidContent.parent.parent as ViewGroup?
        mRoot?.postDelayed(Runnable {
            if (activity.isFinishing) {
                return@Runnable
            }
            val visibleView = mRoot?.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView?
            isClickable = !mEnableTouchOnFocusedView
            if (visibleView == null) {
                tag = CONTAINER_TAG
                setId(R.id.fscv_id)

                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                mRoot?.addView(this)

                setupTouchListener()

                setCalculatorParams()

                addFancyImageView()

                inflateContent()

                startEnterAnimation()

                writeShown()
            }
        }, delay)
    }

    private fun setCalculatorParams() {
        focusCalculator?.apply {
            if (hasFocus()) {
                mCenterX = circleCenterX
                mCenterY = circleCenterY
            }
            if (mFocusRectangleWidth > 0 && mFocusRectangleHeight > 0) {
                setRectPosition(mFocusPositionX, mFocusPositionY, mFocusRectangleWidth, mFocusRectangleHeight)
            }
            if (mFocusCircleRadius > 0) {
                setCirclePosition(mFocusPositionX, mFocusPositionY, mFocusCircleRadius)
            }
        }
    }

    private fun addFancyImageView() {
        FancyImageView(activity).apply {
            setFocusAnimationParameters(mFocusAnimationMaxValue, mFocusAnimationStep)
            setParameters(mBackgroundColor, focusCalculator!!)
            focusAnimationEnabled = this@FancyShowCaseView.focusAnimationEnabled
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            if (mFocusBorderColor != 0 && mFocusBorderSize > 0) {
                setBorderParameters(mFocusBorderColor, mFocusBorderSize)
            }
            if (mRoundRectRadius > 0) {
                roundRectRadius = mRoundRectRadius
            }
            addView(this)
        }
    }

    private fun inflateContent() {
        if (mCustomViewRes == 0) {
            inflateTitleView()
        } else {
            inflateCustomView(mCustomViewRes, viewInflateListener)
        }
    }

    private fun setupTouchListener() {
        setOnTouchListener(OnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                when {
                    mEnableTouchOnFocusedView && isWithinZone(event, focusCalculator) -> {
                        // Check if there is a clickable view within the focusable view
                        // Let the touch event pass through to clickable zone only if clicking within, otherwise return true to ignore event
                        // If there is no clickable view we let through the click to the focusable view
                        clickableView?.let {
                            return@OnTouchListener !isWithinZone(event, clickableCalculator)
                        } ?: return@OnTouchListener false
                    }
                    mCloseOnTouch -> hide()
                }
            }
            true
        })
    }

    /**
     * Check whether the event is within the provided zone that was already computed with the provided calculator
     *
     * @param event         The event from onTouch callback
     * @param calculator    The calculator that holds the zone's position
     */
    private fun isWithinZone(event: MotionEvent, calculator: Calculator?): Boolean {
        var isWithin = false
        val x = event.x
        val y = event.y
        val focusCenterX = calculator?.circleCenterX ?: 0
        val focusCenterY = calculator?.circleCenterY ?: 0
        val focusWidth = calculator?.focusWidth ?: 0
        val focusHeight = calculator?.focusHeight ?: 0
        val focusRadius =
                if (FocusShape.CIRCLE == mFocusShape)
                    calculator?.circleRadius(0, 1.0) ?: 0f
                else 0f

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

        return isWithin
    }

    /**
     * Starts enter animation of FancyShowCaseView
     */
    private fun startEnterAnimation() {
        when {
            mEnterAnimation != null -> startAnimation(mEnterAnimation)
            shouldShowCircularAnimation() -> doCircularEnterAnimation()
            else -> {
                val fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fscv_fade_in)
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
            shouldShowCircularAnimation() -> doCircularExitAnimation()
            else -> {
                val fadeOut = AnimationUtils.loadAnimation(activity, R.anim.fscv_fade_out)
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
        activity.layoutInflater?.inflate(layout, this, false)?.apply {
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
                    textView.setTextAppearance(activity, mTitleStyle)
                }
                if (mTitleSize != -1) {
                    textView.setTextSize(mTitleSizeUnit, mTitleSize.toFloat())
                }
                textView.gravity = mTitleGravity
                if (fitSystemWindows) {
                    val params = textView.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0, Calculator.getStatusBarHeight(context), 0, 0)
                }
                if (spannedTitle != null) {
                    textView.text = spannedTitle
                } else {
                    textView.text = title
                }

                if (autoPosText) {
                    focusCalculator?.calcAutoTextPosition(textView)
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
                        if (focusedView != null) {
                            startRadius = focusedView!!.width / 2
                        } else if (mFocusCircleRadius > 0 || mFocusRectangleWidth > 0 || mFocusRectangleHeight > 0) {
                            mCenterX = mFocusPositionX
                            mCenterY = mFocusPositionY
                        }
                        ViewAnimationUtils.createCircularReveal(this@FancyShowCaseView,
                                mCenterX,
                                mCenterY,
                                startRadius.toFloat(),
                                revealRadius.toFloat()).apply {

                            duration = mAnimationDuration.toLong()
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    mAnimationListener?.onEnterAnimationEnd()
                                }
                            })
                            interpolator = AnimationUtils.loadInterpolator(activity,
                                    android.R.interpolator.accelerate_cubic)
                            start()
                        }
                    }
                })

    }

    /**
     * Circular reveal exit animation
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doCircularExitAnimation() {
        if (!isAttachedToWindow) return
        val revealRadius = Math.hypot(width.toDouble(), height.toDouble()).toInt()
        ViewAnimationUtils.createCircularReveal(this,
                mCenterX,
                mCenterY,
                revealRadius.toFloat(),
                0f).apply {
            duration = mAnimationDuration.toLong()
            interpolator = AnimationUtils.loadInterpolator(activity,
                    android.R.interpolator.decelerate_cubic)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeView()
                    mAnimationListener?.onExitAnimationEnd()
                }
            })
            start()
        }
    }

    /**
     * Saves the FancyShowCaseView id to SharedPreferences that is shown once
     */
    private fun writeShown() {
        sharedPreferences?.edit()?.apply {
            putBoolean(id, true)
            apply()
        }
    }

    fun isShownBefore() = if (id != null) isShownBefore(context, id!!) else false


    /**
     * Removes FancyShowCaseView view from activity root view
     */
    fun removeView() {
        if (fancyImageView != null) fancyImageView = null
        mRoot?.removeView(this)
        dismissListener?.onDismiss(id)
        queueListener?.onNext()
    }

    override fun onGlobalLayout() {
        if (Build.VERSION.SDK_INT < 16) {
            focusedView?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
        } else {
            focusedView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
        focus()
    }

    private fun shouldShowCircularAnimation(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }


    /**
     * Builder class for [FancyShowCaseView]
     */
    class Builder(private val activity: Activity) {
        private var focusedView: View? = null
        private var clickableView: View? = null
        private var mId: String? = null
        private var mTitle: String? = null
        private var mSpannedTitle: Spanned? = null
        private var focusCircleRadiusFactor = 1.0
        private var mBackgroundColor: Int = 0
        private var mFocusBorderColor: Int = 0
        private var mTitleGravity = -1
        private var mTitleSize = -1
        private var mTitleSizeUnit = -1
        private var mTitleStyle: Int = 0
        private var mCustomViewRes: Int = 0
        private var mRoundRectRadius: Int = 0
        private var viewInflateListener: OnViewInflateListener? = null
        private var mEnterAnimation: Animation? = null
        private var mExitAnimation: Animation? = null
        private var mAnimationListener: AnimationListener? = null
        private var mCloseOnTouch = true
        private var mEnableTouchOnFocusedView: Boolean = false
        private var fitSystemWindows: Boolean = false
        private var mFocusShape = FocusShape.CIRCLE
        private var mDismissListener: DismissListener? = null
        private var mFocusBorderSize: Int = 0
        private var mFocusPositionX: Int = 0
        private var mFocusPositionY: Int = 0
        private var mFocusCircleRadius: Int = 0
        private var mFocusRectangleWidth: Int = 0
        private var mFocusRectangleHeight: Int = 0
        private var focusAnimationEnabled = true
        private var mFocusAnimationMaxValue = 20
        private var mFocusAnimationStep = 1
        private var delay: Long = 0
        private var autoPosText = false


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
        fun clickableOn(view: View): Builder {
            clickableView = view
            return this
        }

        /**
         * @param view view to focus
         * @return Builder
         */
        fun focusOn(view: View): Builder {
            focusedView = view
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
         * @param factor focus circle radius factor (default value = 1)
         * @return Builder
         */
        fun focusCircleRadiusFactor(factor: Double): Builder {
            focusCircleRadiusFactor = factor
            return this
        }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        fun customView(@LayoutRes layoutResource: Int, listener: OnViewInflateListener?): Builder {
            mCustomViewRes = layoutResource
            viewInflateListener = listener
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
         * @param _fitSystemWindows fitSystemWindows value
         * @return Builder
         */
        fun fitSystemWindows(_fitSystemWindows: Boolean): Builder {
            fitSystemWindows = _fitSystemWindows
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
            focusAnimationEnabled = false
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
            delay = delayInMillis.toLong()
            return this
        }

        fun enableAutoTextPosition(): Builder {
            autoPosText = true
            return this
        }

        /**
         * builds the builder
         *
         * @return [FancyShowCaseView] with given parameters
         */
        fun build(): FancyShowCaseView {
            return FancyShowCaseView(activity, focusedView, clickableView, mId, mTitle, mSpannedTitle, mTitleGravity, mTitleStyle, mTitleSize, mTitleSizeUnit,
                    focusCircleRadiusFactor, mBackgroundColor, mFocusBorderColor, mFocusBorderSize, mCustomViewRes, viewInflateListener,
                    mEnterAnimation, mExitAnimation, mAnimationListener, mCloseOnTouch, mEnableTouchOnFocusedView, fitSystemWindows, mFocusShape, mDismissListener, mRoundRectRadius,
                    mFocusPositionX, mFocusPositionY, mFocusCircleRadius, mFocusRectangleWidth, mFocusRectangleHeight, focusAnimationEnabled,
                    mFocusAnimationMaxValue, mFocusAnimationStep, delay, autoPosText)
        }
    }

    companion object {

        // Tag for container view
        private const val CONTAINER_TAG = "ShowCaseViewTag"
        // SharedPreferences name
        private const val PREF_NAME = "PrefShowCaseView"

        /**
         * Resets the show once flag
         *
         * @param context context that should be used to create the shared preference instance
         * @param id      id of the show once flag that should be reset
         */
        @JvmStatic
        fun resetShowOnce(context: Context, id: String) = preferences(context)
                .edit()
                .remove(id)
                .apply()


        /**
         * Resets all show once flags
         *
         * @param context context that should be used to create the shared preference instance
         */
        @JvmStatic
        fun resetAllShowOnce(context: Context) = preferences(context)
                .edit()
                .clear()
                .apply()


        @JvmStatic
        fun isShownBefore(context: Context, id: String) = preferences(context).getBoolean(id, false)

        private fun preferences(context: Context) = context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        /**
         * Check is FancyShowCaseView visible
         *
         * @param activity should be used to find FancyShowCaseView inside it
         */
        @JvmStatic
        fun isVisible(activity: Activity): Boolean {
            val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val mRoot = androidContent.parent.parent as ViewGroup
            return mRoot.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView? != null
        }

        /**
         * Hide  FancyShowCaseView
         *
         * @param activity should be used to hide FancyShowCaseView inside it
         */
        @JvmStatic
        fun hideCurrent(activity: Activity) {
            val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val mRoot = androidContent.parent.parent as ViewGroup
            (mRoot.findViewWithTag<View>(CONTAINER_TAG) as FancyShowCaseView?)?.hide()
        }
    }
}
