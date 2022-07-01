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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
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
import me.toptas.fancyshowcase.internal.AnimationPresenter
import me.toptas.fancyshowcase.internal.DashInfo
import me.toptas.fancyshowcase.internal.DeviceParamsImpl
import me.toptas.fancyshowcase.internal.FadeOutAnimation
import me.toptas.fancyshowcase.internal.FancyImageView
import me.toptas.fancyshowcase.internal.FocusedView
import me.toptas.fancyshowcase.internal.Presenter
import me.toptas.fancyshowcase.internal.Properties
import me.toptas.fancyshowcase.internal.SharedPrefImpl
import me.toptas.fancyshowcase.internal.getStatusBarHeight
import me.toptas.fancyshowcase.listener.AnimationListener
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnQueueListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener
import kotlin.math.hypot

/**
 * FancyShowCaseView class
 */

class FancyShowCaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var activity: Activity
    private lateinit var presenter: Presenter
    private lateinit var animationPresenter: AnimationPresenter
    private var props = Properties()
    private var androidProps = AndroidProperties()

    private val mAnimationDuration = 400
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mRoot: ViewGroup? = null
    private var fancyImageView: FancyImageView? = null
    private var isHiding = false

    val focusCenterX: Int
        get() = presenter.circleCenterX

    val focusCenterY: Int
        get() = presenter.circleCenterY

    val focusWidth: Int
        get() = presenter.focusWidth

    val focusHeight: Int
        get() = presenter.focusHeight

    val focusShape: FocusShape
        get() = presenter.focusShape

    var queueListener: OnQueueListener?
        set(value) {
            props.queueListener = value
        }
        get() = props.queueListener

    private constructor(_activity: Activity, _props: Properties, _androidProps: AndroidProperties) : this(_activity) {
        props = _props
        activity = _activity
        androidProps = _androidProps
        val deviceParams = DeviceParamsImpl(activity, this)
        presenter = Presenter(preferences(activity), deviceParams, props)
        animationPresenter = AnimationPresenter(androidProps, deviceParams)

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

    private fun focus() {
        presenter.calculations()
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

                addView(FancyImageView.instance(activity, props, presenter))

                inflateContent()

                startEnterAnimation()

                writeShown()
            }
        }, props.delay)
    }

    private fun setCalculatorParams() {
        if (presenter.hasFocus) {
            mCenterX = presenter.circleCenterX
            mCenterY = presenter.circleCenterY
        }
        presenter.setFocusPositions()
    }


    private fun inflateContent() {
        if (props.customViewRes == 0) {
            inflateTitleView()
        } else {
            inflateCustomView(props.customViewRes, props.viewInflateListener)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        setOnTouchListener(OnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                when {
                    props.enableTouchOnFocusedView && presenter.isWithinZone(event.x, event.y, props.focusedView!!) -> {
                        // Check if there is a clickable view within the focusable view
                        // Let the touch event pass through to clickable zone only if clicking within, otherwise return true to ignore event
                        // If there is no clickable view we let through the click to the focusable view
                        props.clickableView?.let {
                            return@OnTouchListener !presenter.isWithinZone(event.x, event.y, it)
                        } ?: return@OnTouchListener false
                    }
                    props.closeOnTouch -> hide()
                }
            }
            true
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startEnterAnimation() {
        animationPresenter.enterAnimation(
                { doCircularEnterAnimation() },
                { animation -> startAnimation(animation) }
        )
    }

    /**
     * Hides FancyShowCaseView with animation
     */
    fun hide() {
        if(isHiding) return
        isHiding = true

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
                val textContainer = view.findViewById<RelativeLayout>(R.id.fcsv_title_container)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(props.titleStyle)
                } else {
                    textView.setTextAppearance(activity, props.titleStyle)
                }
                androidProps.typeface?.let {
                    textView.typeface = it
                }
                if (props.titleSize != -1) {
                    textView.setTextSize(props.titleSizeUnit, props.titleSize.toFloat())
                }
                textContainer.gravity = props.titleGravity
                if (props.fitSystemWindows) {
                    val params = textView.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0, getStatusBarHeight(context), 0, 0)
                }
                if (androidProps.spannedTitle != null) {
                    textView.text = androidProps.spannedTitle
                } else {
                    textView.text = props.title
                }

                if (props.autoPosText) {
                    val pos = presenter.calcAutoTextPosition()
                    val params = textView.layoutParams as RelativeLayout.LayoutParams
                    params.apply {
                        topMargin = pos.topMargin
                        bottomMargin = pos.bottomMargin
                        height = pos.height
                    }
                    textView.layoutParams = params
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


    private fun writeShown() {
        presenter.writeShown(props.fancyId)
    }

    /**
     * Returns true if FancyShowCaseView is shown once
     */
    fun isShownBefore() = if (props.fancyId != null) isShownBefore(context, props.fancyId!!) else false


    /**
     * Removes FancyShowCaseView view from activity root view
     */
    fun removeView() {
        if (fancyImageView != null) fancyImageView = null
        mRoot?.removeView(this)
        props.dismissListener?.onDismiss(props.fancyId)
        queueListener?.onNext()
        isHiding = false
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
         * @param typeface title typeface
         * @return Builder
         */
        fun typeface(typeface: Typeface?) = apply {
            androidProps.typeface = typeface
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
         * @param intervalOnSize size of the dashed part of the line
         * @param intervalOffSize size of the blank part of the line
         * @return Builder
         */
        fun focusDashedBorder(intervalOnSize: Float, intervalOffSize: Float) = apply {
            props.dashedLineInfo = DashInfo(intervalOnSize, intervalOffSize)
        }

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
        fun clickableOn(view: View) = apply { props.clickableView = FocusedView(view) }

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
         * @param factor focus rectangle size factor (default value = 1)
         * @return Builder
         */
        fun focusRectSizeFactor(factor: Double) = apply { props.focusRectSizeFactor = factor }

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

        /**
         * Focus animation max value. Bigger value makes larger focus area
         */
        fun focusAnimationMaxValue(focusAnimationMaxValue: Double) = apply {
            props.focusAnimationMaxValue = focusAnimationMaxValue
        }

        /**
         * Step for focus animation. Default value is 1.
         */
        fun focusAnimationStep(focusAnimationStep: Double) = apply { props.focusAnimationStep = focusAnimationStep }

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


        /**
         * Returns true if FancyShowCaseView with given id is shown once
         */
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
