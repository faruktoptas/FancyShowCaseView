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

package me.toptas.fancyshowcase.internal

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import me.toptas.fancyshowcase.Calculator
import me.toptas.fancyshowcase.FocusShape

/**
 * ImageView with focus area animation
 */

class FancyImageView : AppCompatImageView {

    private lateinit var calculator: Calculator
    private lateinit var backgroundPaint: Paint
    private lateinit var erasePaint: Paint
    private var circleBorderPaint: Paint? = null
    private lateinit var path: Path
    private lateinit var rectF: RectF

    private var bitmap: Bitmap? = null
    var bgColor = Color.TRANSPARENT
    var focusBorderColor = Color.TRANSPARENT
        set(value) {
            field = value
            circleBorderPaint?.color = focusBorderColor
        }
    var focusBorderSize: Int = 0
        set(value) {
            field = value
            circleBorderPaint?.strokeWidth = field.toFloat()
        }

    private var animCounter = 0
    private var step = 1
    private var animMoveFactor = 1.0
    var focusAnimationMaxValue: Int = 0
    var focusAnimationStep: Int = 0

    var roundRectRadius = 20
    var focusAnimationEnabled = true
        set(value) {
            animCounter = if (value) DEFAULT_ANIM_COUNTER else 0
            field = value
        }


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    /**
     * Initializations for background and paints
     */
    private fun init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = bgColor
            alpha = 0xFF
        }
        erasePaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            alpha = 0xFF
            isAntiAlias = true
        }
        path = Path()
        circleBorderPaint = Paint().apply {
            isAntiAlias = true
            color = focusBorderColor
            strokeWidth = focusBorderSize.toFloat()
            style = Paint.Style.STROKE
        }
        rectF = RectF()
    }

    /**
     * Setting parameters for background an animation
     *
     * @param _calculator      calculator object for calculations
     */
    fun setParameters(_calculator: Calculator) {
        animMoveFactor = 1.0
        calculator = _calculator
    }


    /**
     * Draws background and moving focus area
     *
     * @param canvas draw canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(bgColor)
            }
        }
        canvas.drawBitmap(bitmap!!, 0f, 0f, backgroundPaint)
        if (calculator.hasFocus()) {
            if (calculator.focusShape == FocusShape.CIRCLE) {
                drawCircle(canvas)
            } else {
                drawRoundedRectangle(canvas)
            }
            if (focusAnimationEnabled && !DISABLE_ANIMATIONS_FOR_TESTING) {
                if (animCounter == focusAnimationMaxValue) {
                    step = -1 * focusAnimationStep
                } else if (animCounter == 0) {
                    step = focusAnimationStep
                }
                animCounter += step
                postInvalidate()
            }
        }
    }

    /**
     * Draws focus circle
     *
     * @param canvas canvas to draw
     */
    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(calculator.circleCenterX.toFloat(), calculator.circleCenterY.toFloat(),
                calculator.circleRadius(animCounter, animMoveFactor), erasePaint)

        if (focusBorderSize > 0) {
            path.apply {
                reset()
                moveTo(calculator.circleCenterX.toFloat(), calculator.circleCenterY.toFloat())
                addCircle(calculator.circleCenterX.toFloat(), calculator.circleCenterY.toFloat(),
                        calculator.circleRadius(animCounter, animMoveFactor), Path.Direction.CW)
                canvas.drawPath(this, circleBorderPaint)
            }
        }
    }

    /**
     * Draws focus rounded rectangle
     *
     * @param canvas canvas to draw
     */
    private fun drawRoundedRectangle(canvas: Canvas) {
        val left = calculator.roundRectLeft(animCounter, animMoveFactor)
        val top = calculator.roundRectTop(animCounter, animMoveFactor)
        val right = calculator.roundRectRight(animCounter, animMoveFactor)
        val bottom = calculator.roundRectBottom(animCounter, animMoveFactor)

        rectF.apply {
            set(left, top, right, bottom)
            canvas.drawRoundRect(this, roundRectRadius.toFloat(), roundRectRadius.toFloat(), erasePaint)
        }
        if (focusBorderSize > 0) {
            path.apply {
                reset()
                moveTo(calculator.circleCenterX.toFloat(), calculator.circleCenterY.toFloat())
                addRoundRect(rectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), Path.Direction.CW)
                canvas.drawPath(this, circleBorderPaint)
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (bitmap != null && bitmap?.isRecycled == false) {
            bitmap?.recycle()
            bitmap = null
        }
    }

    companion object {
        private const val DEFAULT_ANIM_COUNTER = 20

        @VisibleForTesting
        var DISABLE_ANIMATIONS_FOR_TESTING = false

        internal fun instance(activity: Activity, props: Properties, calc: Calculator) =
                FancyImageView(activity).apply {
                    setParameters(calc)
                    bgColor = props.backgroundColor
                    focusAnimationMaxValue = props.focusAnimationMaxValue
                    focusAnimationStep = props.focusAnimationStep
                    focusAnimationEnabled = props.focusAnimationEnabled
                    focusBorderColor = props.focusBorderColor
                    focusBorderSize = props.focusBorderSize
                    roundRectRadius = props.roundRectRadius

                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)

                }
    }
}
