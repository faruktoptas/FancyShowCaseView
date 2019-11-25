package me.toptas.fancyshowcase.internal

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import me.toptas.fancyshowcase.R

internal interface DeviceParams {
    fun currentBackgroundColor(): Int
    fun deviceWidth(): Int
    fun deviceHeight(): Int
}

internal class DeviceParamsImpl(private val activity: Activity, view: View) : DeviceParams {

    private val metrics = DisplayMetrics()

    init {
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }

    override fun currentBackgroundColor() = ContextCompat
            .getColor(activity, R.color.fancy_showcase_view_default_background_color)

    override fun deviceWidth() = metrics.widthPixels

    override fun deviceHeight() = metrics.heightPixels


}