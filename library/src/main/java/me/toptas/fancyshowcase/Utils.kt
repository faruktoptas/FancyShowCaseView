/*
 * Copyright (c) 2018. Faruk Toptaş
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.toptas.fancyshowcase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.view.View


internal object Utils {

    /**
     * Circular reveal animation condition
     *
     * @return true if enabled
     */
    fun shouldShowCircularAnimation(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    /**
     * Calculates focus point values
     *
     * @param view               view to focus
     * @param circleRadiusFactor radius factor of circle
     * @return x, y, radius values for the circle
     */
    fun calculateFocusPointValues(view: View?, circleRadiusFactor: Double, adjustHeight: Int): IntArray? {
        val point = IntArray(3)
        if (view != null) {
            val viewPoint = IntArray(2)
            view.getLocationInWindow(viewPoint)

            point[0] = viewPoint[0] + view.width / 2
            point[1] = viewPoint[1] + view.height / 2 - adjustHeight
            val radius = ((Math.hypot(view.width.toDouble(), view.height.toDouble()) / 2).toInt() * circleRadiusFactor).toInt()
            point[2] = radius
            return point
        }
        return null
    }

    /**
     * Draws focus circle
     *
     * @param bitmap bitmap to draw
     * @param point  circle point
     * @param radius circle radius
     */
    fun drawFocusCircle(bitmap: Bitmap, point: IntArray, radius: Int) {
        val p = Paint()
        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val c = Canvas(bitmap)
        c.drawCircle(point[0].toFloat(), point[1].toFloat(), radius.toFloat(), p)
    }

    /**
     * Returns statusBar height
     *
     * @param context context to access resources
     * @return statusBar height
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

}
