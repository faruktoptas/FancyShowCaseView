package me.toptas.fancyshowcase.internal

import android.view.animation.AlphaAnimation

internal class FadeOutAnimation : AlphaAnimation(1f, 0f) {
    init {
        fillAfter = true
        duration = 400
    }
}