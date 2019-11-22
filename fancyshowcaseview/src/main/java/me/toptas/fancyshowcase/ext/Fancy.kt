package me.toptas.fancyshowcase.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import me.toptas.fancyshowcase.FancyShowCaseView
import kotlin.math.hypot

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal fun FancyShowCaseView.circularExitAnimation(activity: Activity,
                                                     centerX: Int,
                                                     centerY: Int,
                                                     animDuration: Int,
                                                     animationEndListener: () -> Unit) {
    if (!isAttachedToWindow) return
    val revealRadius = hypot(width.toDouble(), height.toDouble()).toInt()
    ViewAnimationUtils.createCircularReveal(this,
            centerX,
            centerY,
            revealRadius.toFloat(),
            0f).apply {
        duration = animDuration.toLong()
        interpolator = AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.decelerate_cubic)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animationEndListener()
            }
        })
        start()
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal fun FancyShowCaseView.circularEnterAnimation(activity: Activity,
                                                      centerX: Int,
                                                      centerY: Int,
                                                      startRadius: Int,
                                                      revealRadius: Int,
                                                      animDuration: Int,
                                                      animationEndListener: () -> Unit) {
    ViewAnimationUtils.createCircularReveal(this,
            centerX,
            centerY,
            startRadius.toFloat(),
            revealRadius.toFloat()).apply {

        duration = animDuration.toLong()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animationEndListener()
            }
        })
        interpolator = AnimationUtils.loadInterpolator(activity,
                android.R.interpolator.accelerate_cubic)
        start()
    }
}