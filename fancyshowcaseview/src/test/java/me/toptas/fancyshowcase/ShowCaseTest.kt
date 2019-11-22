package me.toptas.fancyshowcase

import android.view.Gravity
import com.nhaarman.mockitokotlin2.anyVararg
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import me.toptas.fancyshowcase.internal.DeviceParams
import me.toptas.fancyshowcase.internal.IFocusedView
import me.toptas.fancyshowcase.internal.Presenter
import me.toptas.fancyshowcase.internal.Properties
import me.toptas.fancyshowcase.internal.SharedPref
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ShowCaseTest {

    private val pref: SharedPref = mock()
    private val device: DeviceParams = mock()

    private val props = Properties()

    @Before
    fun setup() {
        Mockito.`when`(device.currentBackgroundColor()).thenReturn(100)
        Mockito.`when`(device.deviceWidth()).thenReturn(1080)
        Mockito.`when`(device.deviceHeight()).thenReturn(1920)
    }

    @Test
    fun testInitialValues() {
        val presenter = Presenter(pref, device, props)
        presenter.initialize()

        assert(props.backgroundColor == 100)
        assert(props.titleGravity == Gravity.CENTER)
        assert(props.titleStyle == R.style.FancyShowCaseDefaultTitleStyle)
        assert(presenter.centerX == 540)
        assert(presenter.centerY == 960)
    }

    @Test
    fun testDefaultBgColor() {
        props.backgroundColor = 200
        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        assert(props.backgroundColor == 200)
    }

    @Test
    fun testGravity() {
        val presenter = Presenter(pref, device, props)
        props.titleGravity = Gravity.TOP
        presenter.initialize()

        assert(props.titleGravity == Gravity.TOP)
    }

    @Test
    fun testTitleStyle() {
        val presenter = Presenter(pref, device, props)
        props.titleStyle = 101
        presenter.initialize()

        assert(props.titleStyle == 101)
    }

    @Test
    fun testNotShownBefore() {
        Mockito.`when`(pref.isShownBefore(anyVararg())).thenReturn(false)
        props.focusedView = mock()
        props.dismissListener = mock()
        laidOutView(props.focusedView!!)

        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.dismissListener, Mockito.never())!!.onSkipped(anyVararg())
        assert(onShowTriggered)
    }

    @Test
    fun testViewNotLaidOut() {
        Mockito.`when`(pref.isShownBefore(anyVararg())).thenReturn(false)
        props.focusedView = mock()
        props.dismissListener = mock()
        Mockito.`when`(props.focusedView!!.cantFocus()).thenReturn(true)


        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.focusedView)!!.waitForLayout(anyVararg())
        assert(!onShowTriggered)
    }

    @Test
    fun testShownBefore() {
        Mockito.`when`(pref.isShownBefore(anyVararg())).thenReturn(true)
        props.focusedView = mock()
        props.dismissListener = mock()
        laidOutView(props.focusedView!!)

        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.dismissListener)!!.onSkipped(anyVararg())
        assert(!onShowTriggered)
    }


    @Test
    fun testFocus() {
        Mockito.`when`(pref.isShownBefore(anyVararg())).thenReturn(false)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)

        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.dismissListener)!!.onSkipped(anyVararg())
        assert(!onShowTriggered)
    }
}

private fun laidOutView(view: IFocusedView) {
    Mockito.`when`(view.width()).thenReturn(100)
    Mockito.`when`(view.height()).thenReturn(100)
}