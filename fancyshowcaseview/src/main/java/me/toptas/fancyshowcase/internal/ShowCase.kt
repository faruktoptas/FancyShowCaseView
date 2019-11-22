package me.toptas.fancyshowcase.internal

import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import me.toptas.fancyshowcase.R


interface DeviceParams {
    fun currentBackgroundColor(): Int
    fun deviceWidth(): Int
    fun deviceHeight(): Int
}

class DeviceParamsImpl(private val activity: Activity, view: View) : DeviceParams {

    private val metrics = DisplayMetrics()

    init {
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }

    override fun currentBackgroundColor() = ContextCompat
            .getColor(activity, R.color.fancy_showcase_view_default_background_color)

    override fun deviceWidth() = metrics.widthPixels

    override fun deviceHeight() = metrics.heightPixels


}


internal class Presenter(private val pref: SharedPref,
                         private val device: DeviceParams,
                         private val props: Properties/*,
                         private val androidProps: AndroidProperties*/) {

    var centerX: Int = 0
    var centerY: Int = 0

    fun initialize() {
        props.backgroundColor = if (props.backgroundColor != 0)
            props.backgroundColor
        else
            device.currentBackgroundColor()
        props.titleGravity = if (props.titleGravity >= 0) props.titleGravity else Gravity.CENTER
        props.titleStyle = if (props.titleStyle != 0) props.titleStyle else R.style.FancyShowCaseDefaultTitleStyle


        centerX = device.deviceWidth() / 2
        centerY = device.deviceHeight() / 2
    }


    fun show(onShow: () -> Unit/*, waitForLayout: () -> Unit*/) {
        if (pref.isShownBefore(props.fancyId)) {
            props.dismissListener?.onSkipped(props.fancyId)
            return
        }
        // if view is not laid out get, width/height values in onGlobalLayout
        if (props.focusedView?.cantFocus() == true) {
            props.focusedView?.waitForLayout { onShow() }
        } else {
            onShow()
        }
    }

    fun writeShown(fancyId: String?) {
        fancyId?.let {
            pref.writeShown(it)
        }
    }
}