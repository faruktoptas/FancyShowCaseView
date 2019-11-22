package me.toptas.fancyshowcase.internal

import android.view.View
import me.toptas.fancyshowcase.ext.globalLayoutListener

interface IFocusedView {
    fun width(): Int
    fun height(): Int
    fun getLocationInWindow(viewPoint: IntArray)
    fun waitForLayout(onLayout: () -> Unit)

    fun cantFocus() = width() == 0 && height() == 0
}

class FocusedView(private val view: View) : IFocusedView {

    override fun width() = view.width

    override fun height() = view.height

    override fun getLocationInWindow(viewPoint: IntArray) {
        view.getLocationInWindow(viewPoint)
    }

    override fun waitForLayout(onLayout: () -> Unit) {
        view.globalLayoutListener { onLayout() }
    }

    fun view() = view

}