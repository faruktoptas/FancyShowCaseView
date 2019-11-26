package me.toptas.fancyshowcase.internal

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import me.toptas.fancyshowcase.R

internal interface DeviceParams {
    fun currentBackgroundColor(): Int
    fun deviceWidth(): Int
    fun deviceHeight(): Int
    fun getStatusBarHeight(): Int
    fun isFullScreen(): Boolean
    fun aboveAPI19(): Boolean
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

    override fun getStatusBarHeight() = getStatusBarHeight(activity)

    override fun isFullScreen(): Boolean {
        val windowFlags = activity.window.attributes.flags
        return (windowFlags and WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0
    }

    override fun aboveAPI19() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}

internal fun getStatusBarHeight(context: Context): Int {
    var result = 0
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = context.resources.getDimensionPixelSize(resourceId)
    }
    return result
}