package me.toptas.fancyshowcase.ext

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation

internal fun View.globalLayoutListener(onLayout: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (Build.VERSION.SDK_INT < 16) {
                viewTreeObserver?.removeGlobalOnLayoutListener(this)
            } else {
                viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
            onLayout()
        }
    })
}

internal class AnimationEndListener(private val animationEnd: () -> Unit) : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {
        // No-op
    }

    override fun onAnimationEnd(p0: Animation?) {
        animationEnd()
    }

    override fun onAnimationStart(p0: Animation?) {
        // No-op
    }

}