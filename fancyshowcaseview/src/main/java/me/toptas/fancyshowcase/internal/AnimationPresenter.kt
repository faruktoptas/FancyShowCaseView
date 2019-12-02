package me.toptas.fancyshowcase.internal

import android.view.animation.Animation

internal class AnimationPresenter(private val androidProps: AndroidProperties, private val device: DeviceParams) {

    fun enterAnimation(doCircularEnterAnimation: () -> Unit, doCustomAnimation: (Animation?) -> Unit) {
        if (androidProps.enterAnimation != null) {
            if (androidProps.enterAnimation is FadeInAnimation && device.aboveAPI19()) {
                doCircularEnterAnimation()
            } else {
                doCustomAnimation(androidProps.enterAnimation)
            }
        }
    }


}