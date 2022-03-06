package me.toptas.fancyshowcase.internal

import android.graphics.Typeface
import android.text.Spanned
import android.view.ViewGroup
import android.view.animation.Animation
import me.toptas.fancyshowcase.FocusShape
import me.toptas.fancyshowcase.listener.AnimationListener
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnQueueListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener

internal data class Properties(
        var title: String? = null,
        var fancyId: String? = null,
        var focusCircleRadiusFactor: Double = 1.0,
        var focusRectSizeFactor: Double = 1.0,
        var backgroundColor: Int = 0,
        var focusBorderColor: Int = 0,
        var titleGravity: Int = -1,
        var titleStyle: Int = 0,
        var titleSize: Int = -1,
        var titleSizeUnit: Int = -1,
        var customViewRes: Int = 0,
        var focusBorderSize: Int = 0,
        var dashedLineInfo: DashInfo? = null,
        var roundRectRadius: Int = 20,
        var closeOnTouch: Boolean = true,
        var enableTouchOnFocusedView: Boolean = false,
        var fitSystemWindows: Boolean = false,
        var focusShape: FocusShape = FocusShape.CIRCLE,
        var delay: Long = 0,
        var autoPosText: Boolean = false,
        val animationDuration: Int = 400,
        var focusAnimationMaxValue: Double = 20.0,
        var focusAnimationStep: Double = 1.0,
        var centerX: Int = 0,
        var centerY: Int = 0,
        var focusPositionX: Int = 0,
        var focusPositionY: Int = 0,
        var focusCircleRadius: Int = 0,
        var focusRectangleWidth: Int = 0,
        var focusRectangleHeight: Int = 0,
        var focusAnimationEnabled: Boolean = true,
        var viewInflateListener: OnViewInflateListener? = null,
        var animationListener: AnimationListener? = null,
        var fancyImageView: FancyImageView? = null,
        var dismissListener: DismissListener? = null,
        var queueListener: OnQueueListener? = null,
        var focusedView: IFocusedView? = null,
        var clickableView: IFocusedView? = null
)

internal data class AndroidProperties(
        var spannedTitle: Spanned? = null,
        var mRoot: ViewGroup? = null,
        var enterAnimation: Animation? = FadeInAnimation(),
        var exitAnimation: Animation? = FadeOutAnimation(),
        var typeface: Typeface? = null
)

internal data class DashInfo(val intervalOnSize: Float, val intervalOffSize: Float)