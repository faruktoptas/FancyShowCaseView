package me.toptas.fancyshowcase.internal

import android.view.animation.AlphaAnimation

internal class FadeInAnimation : AlphaAnimation(0f, 1f) {
    init {
        fillAfter = true
        duration = 400
    }
}