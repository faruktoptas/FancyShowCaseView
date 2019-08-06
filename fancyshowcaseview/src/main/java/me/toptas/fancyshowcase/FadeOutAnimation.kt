package me.toptas.fancyshowcase

import android.view.animation.AlphaAnimation

internal class FadeOutAnimation : AlphaAnimation(1f, 0f) {
    init {
        fillAfter = true
        duration = 400
    }
}