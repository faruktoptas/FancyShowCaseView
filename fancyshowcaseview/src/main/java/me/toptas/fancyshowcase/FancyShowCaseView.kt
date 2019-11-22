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

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.Spanned
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import me.toptas.fancyshowcase.ext.AnimationEndListener
import me.toptas.fancyshowcase.ext.attachedShowCase
import me.toptas.fancyshowcase.ext.circularEnterAnimation
import me.toptas.fancyshowcase.ext.circularExitAnimation
import me.toptas.fancyshowcase.ext.globalLayoutListener
import me.toptas.fancyshowcase.ext.rootView
import me.toptas.fancyshowcase.internal.AndroidProperties
import me.toptas.fancyshowcase.internal.DeviceParamsImpl
import me.toptas.fancyshowcase.internal.FadeInAnimation
import me.toptas.fancyshowcase.internal.FadeOutAnimation
import me.toptas.fancyshowcase.internal.FancyImageView
import me.toptas.fancyshowcase.internal.FocusedView
import me.toptas.fancyshowcase.internal.Presenter
import me.toptas.fancyshowcase.internal.Properties
import me.toptas.fancyshowcase.internal.SharedPrefImpl
import me.toptas.fancyshowcase.listener.AnimationListener
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnQueueListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * FancyShowCaseView class
 */

class FancyShowCaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {


    /**
     * Builder parameters
     */
    var focusCalculator: Calculator? = null

    private lateinit var activity: Activity
    private lateinit var presenter: Presenter
    private var clickableCalculator: Calculator? = null
    private var props = Properties()
    private var androidProps = AndroidProperties()

    private val mAnimationDuration = 400
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mRoot: ViewGroup? = null
    private var fancyImageView: FancyImageView? = null
    var queueListener: OnQueueListener? = null


    private constructor(_activity: Activity, _props: Properties, _androidProps: AndroidProperties) : this(_activity) {
        props = _props
        activity = _activity
        androidProps = _androidProps
        presenter = Presenter(preferences(activity), DeviceParamsImpl(activity, this), props)

        presenter.initialize()
        mCenterX = presenter.centerX
        mCenterY = presenter.centerY
    }

    /**
     * Shows FancyShowCaseView
     */
    fun show() {
        presenter.show { focus() }
    }

    internal fun focus() {
        focusCalculator = Calculator(activity,
                props.focusShape,
                props.focusedView,
                props.focusCircleRadiusFactor,
                props.fitSystemWindows)

        clickableCalculator = Calculator(activity,
                props.focusShape,
                androidProps.clickableView,
                props.focusCircleRadiusFactor,
                props.fitSystemWindows)


        mRoot = activity.rootView()
        mRoot?.postDelayed(Runnable {
            if (activity.isFinishing) {
                return@Runnable
            }
            val visibleView = activity.attachedShowCase()
            isClickable = !props.enableTouchOnFocusedView
            if (visibleView == null) {
                tag = CONTAINER_TAG
                id = R.id.fscv_id

                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                mRoot?.addView(this)

                setupTouchListener()

                setCalculatorParams()

                addView(FancyImageView.instance(activity, props, focusCalculator!!))

                inflateContent()

                startEnterAnimation()

                writeShown()
            }
        }, props.delay)
    }

    private fun setCalculatorParams() {
        focusCalculator?.apply {
            if (hasFocus()) {
                mCenterX = circleCenterX
                mCenterY = circleCenterY
            }
            if (props.focusRectangleWidth > 0 && props.focusRectangleHeight > 0) {
                setRectPosition(props.focusPositionX, props.focusPositionY, props.focusRectangleWidth, props.focusRectangleHeight)
            }
            if (props.focusCircleRadius > 0) {
                setCirclePosition(props.focusPositionX, props.focusPositionY, props.focusCircleRadius)
            }
        }
    }


    private fun inflateContent() {
        if (props.customViewRes == 0) {
            inflateTitleView()
        } else {
            inflateCustomView(props.customViewRes, props.viewInflateListener)
        }
    }

    private fun setupTouchListener() {
        setOnTouchListener(OnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                when {
                    props.enableTouchOnFocusedView && isWithinZone(event, focusCalculator) -> {
                        // Check if there is a clickable view within the focusable view
                        // Let the touch event pass through to clickable zone only if clicking within, otherwise return true to ignore event
                        // If there is no clickable view we let through the click to the focusable view
                        androidProps.clickableView?.let {
                            return@OnTouchListener !isWithinZone(event, clickableCalculator)
                        } ?: return@OnTouchListener false
                    }
                    props.closeOnTouch -> hide()
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
                if (FocusShape.CIRCLE == props.focusShape)
                    calculator?.circleRadius(0, 1.0) ?: 0f
                else 0f

        when (props.focusShape) {
            FocusShape.CIRCLE -> {
                val distance = sqrt(
                        (focusCenterX - x).toDouble().pow(2.0) + Math.pow((focusCenterY - y).toDouble(), 2.0))

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
        if (androidProps.enterAnimation != null) {
            if (androidProps.enterAnimation is FadeInAnimation && shouldShowCircularAnimation()) {
                doCircularEnterAnimation()
            } else {
                startAnimation(androidProps.enterAnimation)
            }
        }
    }

    /**
     * Hides FancyShowCaseView with animation
     */
    fun hide() {
        if (androidProps.exitAnimation != null) {
            if (androidProps.exitAnimation is FadeOutAnimation && shouldShowCircularAnimation()) {
                doCircularExitAnimation()
            } else {
                androidProps.exitAnimation?.setAnimationListener(AnimationEndListener {
                    removeView()
                    props.animationListener?.onExitAnimationEnd()
                })
                startAnimation(androidProps.exitAnimation)
            }
        } else {
            removeView()
        }
    }

    /**
     * Inflates custom view
     *
     * @param layout              layout for custom view
     * @param viewInflateListener inflate listener for custom view
     */
    private fun inflateCustomView(@LayoutRes layout: Int, viewInflateListener: OnViewInflateListener?) {
        activity.layoutInflater.inflate(layout, this, false)?.apply {
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
                    textView.setTextAppearance(props.titleStyle)
                } else {
                    textView.setTextAppearance(activity, props.titleStyle)
                }
                if (props.titleSize != -1) {
                    textView.setTextSize(props.titleSizeUnit, props.titleSize.toFloat())
                }
                textView.gravity = props.titleGravity
                if (props.fitSystemWindows) {
                    val params = textView.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0, Calculator.getStatusBarHeight(context), 0, 0)
                }
                if (androidProps.spannedTitle != null) {
                    textView.text = androidProps.spannedTitle
                } else {
                    textView.text = props.title
                }

                if (props.autoPosText) {
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
        globalLayoutListener {
            val revealRadius = hypot(width.toDouble(), height.toDouble()).toInt()
            var startRadius = 0
            if (props.focusedView != null) {
                startRadius = props.focusedView!!.width() / 2
            } else if (props.focusCircleRadius > 0 || props.focusRectangleWidth > 0 || props.focusRectangleHeight > 0) {
                mCenterX = props.focusPositionX
                mCenterY = props.focusPositionY
            }
            circularEnterAnimation(activity, mCenterX, mCenterY, startRadius, revealRadius, mAnimationDuration) {
                props.animationListener?.onEnterAnimationEnd()
            }
        }
    }

    /**
     * Circular reveal exit animation
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doCircularExitAnimation() {
        circularExitAnimation(activity, mCenterX, mCenterY, mAnimationDuration) {
            removeView()
            props.animationListener?.onExitAnimationEnd()
        }
    }

    /**
     * Saves the FancyShowCaseView id to SharedPreferences that is shown once
     */
    private fun writeShown() {
        presenter.writeShown(props.fancyId)
    }

    fun isShownBefore() = if (props.fancyId != null) isShownBefore(context, props.fancyId!!) else false


    /**
     * Removes FancyShowCaseView view from activity root view
     */
    fun removeView() {
        if (fancyImageView != null) fancyImageView = null
        mRoot?.removeView(this)
        props.dismissListener?.onDismiss(props.fancyId)
        queueListener?.onNext()
    }

    private fun shouldShowCircularAnimation(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }


    /**
     * Builder class for [FancyShowCaseView]
     */
    class Builder(private val activity: Activity) {
        private val props = Properties()
        private val androidProps = AndroidProperties()

        /**
         * @param title title text
         * @return Builder
         */
        fun title(title: String) = apply {
            props.title = title
            androidProps.spannedTitle = null
        }

        /**
         * @param title title text
         * @return Builder
         */
        fun title(title: Spanned) = apply {
            androidProps.spannedTitle = title
            props.title = null
        }

        /**
         * @param style        title text style
         * @param titleGravity title gravity
         * @return Builder
         */
        fun titleStyle(@StyleRes style: Int, titleGravity: Int) = apply {
            props.titleGravity = titleGravity
            props.titleStyle = style
        }

        /**
         * @param focusBorderColor Border color for focus shape
         * @return Builder
         */
        fun focusBorderColor(focusBorderColor: Int) = apply { props.focusBorderColor = focusBorderColor }

        /**
         * @param focusBorderSize Border size for focus shape
         * @return Builder
         */
        fun focusBorderSize(focusBorderSize: Int) = apply { props.focusBorderSize = focusBorderSize }


        /**
         * @param titleGravity title gravity
         * @return Builder
         */
        fun titleGravity(titleGravity: Int) = apply { props.titleGravity = titleGravity }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param titleSize title size
         * @param unit      title text unit
         * @return Builder
         */
        fun titleSize(titleSize: Int, unit: Int) = apply {
            props.titleSize = titleSize
            props.titleSizeUnit = unit
        }

        /**
         * @param id unique identifier for FancyShowCaseView
         * @return Builder
         */
        fun showOnce(id: String) = apply { props.fancyId = id }

        /**
         * @param view view to focus
         * @return Builder
         */
        fun clickableOn(view: View) = apply { androidProps.clickableView = FocusedView(view) }

        /**
         * @param view view to focus
         * @return Builder
         */
        fun focusOn(view: View) = apply { props.focusedView = FocusedView(view) }

        /**
         * @param backgroundColor background color of FancyShowCaseView
         * @return Builder
         */
        fun backgroundColor(backgroundColor: Int) = apply { props.backgroundColor = backgroundColor }

        /**
         * @param factor focus circle radius factor (default value = 1)
         * @return Builder
         */
        fun focusCircleRadiusFactor(factor: Double) = apply { props.focusCircleRadiusFactor = factor }

        /**
         * @param layoutResource custom view layout resource
         * @param listener       inflate listener for custom view
         * @return Builder
         */
        fun customView(@LayoutRes layoutResource: Int, listener: OnViewInflateListener?) = apply {
            props.customViewRes = layoutResource
            props.viewInflateListener = listener
        }

        /**
         * @param enterAnimation enter animation for FancyShowCaseView
         * @return Builder
         */
        fun enterAnimation(enterAnimation: Animation?) = apply { androidProps.enterAnimation = enterAnimation }

        /**
         * Listener for enter/exit animations
         *
         * @param listener animation listener
         * @return Builder
         */
        fun animationListener(listener: AnimationListener) = apply { props.animationListener = listener }

        /**
         * @param exitAnimation exit animation for FancyShowCaseView
         * @return Builder
         */
        fun exitAnimation(exitAnimation: Animation?) = apply { androidProps.exitAnimation = exitAnimation }

        /**
         * @param closeOnTouch closes on touch if enabled
         * @return Builder
         */
        fun closeOnTouch(closeOnTouch: Boolean) = apply { props.closeOnTouch = closeOnTouch }

        /**
         * @param enableTouchOnFocusedView closes on touch of focused view if enabled
         * @return Builder
         */
        fun enableTouchOnFocusedView(enableTouchOnFocusedView: Boolean) = apply {
            props.enableTouchOnFocusedView = enableTouchOnFocusedView
        }

        /**
         * This should be the same as root view's fitSystemWindows value
         *
         * @param _fitSystemWindows fitSystemWindows value
         * @return Builder
         */
        fun fitSystemWindows(_fitSystemWindows: Boolean) = apply { props.fitSystemWindows = _fitSystemWindows }

        /**
         * Shape of focused animation
         */
        fun focusShape(focusShape: FocusShape) = apply { props.focusShape = focusShape }

        /**
         * Focus round rectangle at specific position
         *
         * @param positionX      focus at specific position Y coordinate
         * @param positionY      focus at specific position circle radius
         * @param positionWidth  focus at specific position rectangle width
         * @param positionHeight focus at specific position rectangle height
         * @return Builder
         */

        fun focusRectAtPosition(positionX: Int, positionY: Int, positionWidth: Int, positionHeight: Int) = apply {
            props.focusPositionX = positionX
            props.focusPositionY = positionY
            props.focusRectangleWidth = positionWidth
            props.focusRectangleHeight = positionHeight
        }

        /**
         * Focus circle at specific position
         *
         * @param positionX focus at specific position Y coordinate
         * @param positionY focus at specific position circle radius
         * @param radius    focus at specific position circle radius
         * @return Builder
         */

        fun focusCircleAtPosition(positionX: Int, positionY: Int, radius: Int) = apply {
            props.focusPositionX = positionX
            props.focusPositionY = positionY
            props.focusCircleRadius = radius
        }

        /**
         * @param dismissListener the dismiss listener
         * @return Builder
         */
        fun dismissListener(dismissListener: DismissListener) = apply {
            props.dismissListener = dismissListener
        }

        /**
         * Radius of round focused rectangle
         */
        fun roundRectRadius(roundRectRadius: Int) = apply { props.roundRectRadius = roundRectRadius }

        /**
         * Disables focus animation
         *
         * @return Builder
         */
        fun disableFocusAnimation() = apply { props.focusAnimationEnabled = false }


        fun focusAnimationMaxValue(focusAnimationMaxValue: Int) = apply {
            props.focusAnimationMaxValue = focusAnimationMaxValue
        }

        /**
         * Step for focus animation. Default value is 1.
         */
        fun focusAnimationStep(focusAnimationStep: Int) = apply { props.focusAnimationStep = focusAnimationStep }

        /**
         * Shows the FancyShowCaseView after a delay.
         */
        fun delay(delayInMillis: Int) = apply { props.delay = delayInMillis.toLong() }

        /**
         * Center text position vertically.
         */
        fun enableAutoTextPosition() = apply { props.autoPosText = true }

        /**
         * builds the builder
         *
         * @return [FancyShowCaseView] with given parameters
         */
        fun build(): FancyShowCaseView {
            return FancyShowCaseView(activity, props, androidProps)
        }
    }

    companion object {

        // Tag for container view
        internal const val CONTAINER_TAG = "ShowCaseViewTag"

        /**
         * Resets the show once flag
         *
         * @param context context that should be used to create the shared preference instance
         * @param id      id of the show once flag that should be reset
         */
        @JvmStatic
        fun resetShowOnce(context: Context, id: String) = preferences(context).reset(id)


        /**
         * Resets all show once flags
         *
         * @param context context that should be used to create the shared preference instance
         */
        @JvmStatic
        fun resetAllShowOnce(context: Context) = preferences(context).resetAll()


        @JvmStatic
        fun isShownBefore(context: Context, id: String) = SharedPrefImpl(context).isShownBefore(id)

        /**
         * Check is FancyShowCaseView visible
         *
         * @param activity should be used to find FancyShowCaseView inside it
         */
        @JvmStatic
        fun isVisible(activity: Activity) = activity.attachedShowCase() != null

        /**
         * Hide  FancyShowCaseView
         *
         * @param activity should be used to hide FancyShowCaseView inside it
         */
        @JvmStatic
        fun hideCurrent(activity: Activity) = activity.attachedShowCase()?.hide()

        private fun preferences(context: Context) = SharedPrefImpl(context)
    }
}
