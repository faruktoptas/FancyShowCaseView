package me.toptas.fancyshowcase

import android.view.Gravity
import com.nhaarman.mockitokotlin2.anyVararg
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import me.toptas.fancyshowcase.internal.DeviceParams
import me.toptas.fancyshowcase.internal.IFocusedView
import me.toptas.fancyshowcase.internal.Presenter
import me.toptas.fancyshowcase.internal.Properties
import me.toptas.fancyshowcase.internal.SharedPref
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Unit tests for Presenter
 */
class PresenterTest {

    private val pref: SharedPref = mock()
    private val device: DeviceParams = mock()
    private val props = Properties()

    /**
     * Set initial device values
     */
    @Before
    fun setup() {
        whenever(device.currentBackgroundColor()).thenReturn(100)
        whenever(device.deviceWidth()).thenReturn(1080)
        whenever(device.deviceHeight()).thenReturn(1920)
        whenever(device.getStatusBarHeight()).thenReturn(80)
    }

    /**
     * Check initial styles & properties
     */
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

    /**
     * Default background color
     */
    @Test
    fun testDefaultBgColor() {
        props.backgroundColor = 200
        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        assert(props.backgroundColor == 200)
    }

    /**
     * Title gravity
     */
    @Test
    fun testGravity() {
        val presenter = Presenter(pref, device, props)
        props.titleGravity = Gravity.TOP
        presenter.initialize()

        assert(props.titleGravity == Gravity.TOP)
    }

    /**
     * Title style
     */
    @Test
    fun testTitleStyle() {
        val presenter = Presenter(pref, device, props)
        props.titleStyle = 101
        presenter.initialize()

        assert(props.titleStyle == 101)
    }

    /**
     * If view with same id show before, don't show again
     */
    @Test
    fun testNotShownBefore() {
        whenever(pref.isShownBefore(anyVararg())).thenReturn(false)
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

    /**
     * Wait for laid out if try to show before laid out
     */
    @Test
    fun testViewNotLaidOut() {
        whenever(pref.isShownBefore(anyVararg())).thenReturn(false)
        props.focusedView = mock()
        props.dismissListener = mock()
        whenever(props.focusedView!!.cantFocus()).thenReturn(true)


        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.focusedView)!!.waitForLayout(anyVararg())
        assert(!onShowTriggered)
    }

    /**
     * Test dismissListener triggers if shown before
     */
    @Test
    fun testShownBefore() {
        whenever(pref.isShownBefore(anyVararg())).thenReturn(true)
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


    /**
     * Test dismissListener triggers if shown before
     */
    @Test
    fun `test dismissListener onSkipped is not called if not shown before`() {
        whenever(pref.isShownBefore(anyVararg())).thenReturn(false)
        props.focusedView = mock()
        props.dismissListener = mock()
        laidOutView(props.focusedView!!)

        val presenter = Presenter(pref, device, props)
        presenter.initialize()
        var onShowTriggered = false
        presenter.show { onShowTriggered = true }

        verify(props.dismissListener, never())!!.onSkipped(anyVararg())
        assert(onShowTriggered)
    }

    /**
     * Test calculated height before focus
     */
    @Test
    fun testCalculationsWithoutFocus() {
        val presenter = Presenter(pref, device, props)
        presenter.calculations()
        assert(!presenter.hasFocus)
        assert(presenter.bitmapWidth == 1080)
        assert(presenter.bitmapHeight == 1840)

        props.fitSystemWindows = true
        presenter.calculations()
        assert(presenter.bitmapHeight == 1920)
    }

    /**
     * Test calculated values
     */
    @Test
    fun testCalculationsWithFocus() {
        val presenter = Presenter(pref, device, props)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)

        props.fitSystemWindows = false
        whenever(device.isFullScreen()).thenReturn(false)

        presenter.calculations()
        assert(presenter.hasFocus)
        assert(presenter.circleCenterX == 200)
        assert(presenter.circleCenterY == 120)
    }

    /**
     * Test calculated values for abobe 19 devices
     */
    @Test
    fun testCalculationsWithFocus2() {
        val presenter = Presenter(pref, device, props)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)

        props.fitSystemWindows = true
        whenever(device.isFullScreen()).thenReturn(false)
        whenever(device.aboveAPI19()).thenReturn(true)

        presenter.calculations()
        assert(presenter.hasFocus)
        assert(presenter.circleCenterX == 200)
        assert(presenter.circleCenterY == 200)
    }

    /**
     * Set pref
     */
    @Test
    fun testWriteShown() {
        val presenter = Presenter(pref, device, props)
        presenter.writeShown("id")
        verify(pref).writeShown("id")
    }

    /**
     * Circle focus position
     */
    @Test
    fun testFocusPositionsCircle() {
        val presenter = Presenter(pref, device, props)
        props.focusCircleRadius = 20
        props.focusPositionX = 70
        props.focusPositionY = 80

        presenter.setFocusPositions()

        assert(presenter.circleCenterX == 70)
        assert(presenter.circleCenterY == 80)
        assert(presenter.focusShape == FocusShape.CIRCLE)
        assert(presenter.hasFocus)
    }

    /**
     * Rectangle focus position
     */
    @Test
    fun testFocusPositionsRectangle() {
        val presenter = Presenter(pref, device, props)
        props.focusPositionX = 70
        props.focusPositionY = 80
        props.focusRectangleWidth = 120
        props.focusRectangleHeight = 40

        presenter.setFocusPositions()

        assert(presenter.circleCenterX == 70)
        assert(presenter.circleCenterY == 80)
        assert(presenter.focusWidth == 120)
        assert(presenter.focusHeight == 40)
        assert(presenter.focusShape == FocusShape.ROUNDED_RECTANGLE)
        assert(presenter.hasFocus)

    }

    /**
     * Auto text position calculations below
     */
    @Test
    fun testAutoTextPositionBelow() {
        val presenter = Presenter(pref, device, props)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)

        props.fitSystemWindows = true
        whenever(device.isFullScreen()).thenReturn(false)
        whenever(device.aboveAPI19()).thenReturn(true)

        presenter.calculations()
        val pos = presenter.calcAutoTextPosition()
        assert(pos.topMargin == 270)
        assert(pos.bottomMargin == 0)
        assert(pos.height == 1770)
    }

    /**
     * Auto text position calculations above
     */
    @Test
    fun testAutoTextPositionAbove() {
        val presenter = Presenter(pref, device, props)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)
        whenever(props.focusedView!!.getLocationInWindow(anyVararg())).thenReturn(viewPoint(y = 1200))

        props.fitSystemWindows = true
        whenever(device.isFullScreen()).thenReturn(false)
        whenever(device.aboveAPI19()).thenReturn(true)

        presenter.calculations()
        val pos = presenter.calcAutoTextPosition()
        assert(pos.topMargin == 0)
        assert(pos.bottomMargin == 600)
        assert(pos.height == 1200)
    }

    /**
     * Click in the focused zone
     */
    @Test
    fun testClickWithinZoneCircle() {
        val presenter = Presenter(pref, device, props)
        props.focusedView = mock()
        laidOutView(props.focusedView!!)

        presenter.calculations()
        assert(!presenter.isWithinZone(50f, 50f, props.focusedView!!))
        assert(presenter.isWithinZone(150f, 150f, props.focusedView!!))
        assert(presenter.isWithinZone(180f, 180f, props.focusedView!!))
        assert(presenter.isWithinZone(250f, 120f, props.focusedView!!))
        assert(!presenter.isWithinZone(270f, 120f, props.focusedView!!))
    }


}

private fun laidOutView(view: IFocusedView) {
    whenever(view.width()).thenReturn(100)
    whenever(view.height()).thenReturn(100)
    whenever(view.getLocationInWindow(anyVararg())).thenReturn(viewPoint())
}

private fun viewPoint(x: Int = 150, y: Int = 150): IntArray {
    val point = IntArray(2)
    point[0] = x
    point[1] = y

    return point

}