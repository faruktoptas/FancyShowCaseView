package me.toptas.fancyshowcase.internal

import android.view.Gravity
import me.toptas.fancyshowcase.R


internal class Presenter(private val pref: SharedPref,
                         private val device: DeviceParams,
                         private val props: Properties) {

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