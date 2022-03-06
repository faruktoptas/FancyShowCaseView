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
import android.graphics.DashPathEffect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import me.toptas.fancyshowcase.FocusShape

/**
 * ImageView with focus area animation
 */

class FancyImageView : AppCompatImageView {

    private lateinit var presenter: Presenter
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

    private var dashedLineInfo: DashInfo? = null
        set(value) {
            field = value
            value?.let { dashInfo ->
                circleBorderPaint?.pathEffect =
                    DashPathEffect(
                        floatArrayOf(dashInfo.intervalOnSize, dashInfo.intervalOffSize),
                        1f
                    )
            }
        }

    private var animCounter = 0.0
    private var step = 1.0
    private var animMoveFactor = 1.0
    var focusAnimationMaxValue: Double = 0.0
    var focusAnimationStep: Double = 0.0

    var roundRectRadius = 20
    var focusAnimationEnabled = true
        set(value) {
            animCounter = if (value) DEFAULT_ANIM_COUNTER.coerceAtMost(focusAnimationMaxValue) else 0.0
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

    internal fun setPresenter(_presenter: Presenter) {
        animMoveFactor = 1.0
        presenter = _presenter
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
        if (presenter.hasFocus) {
            if (presenter.focusShape == FocusShape.CIRCLE) {
                drawCircle(canvas)
            } else {
                drawRoundedRectangle(canvas)
            }
            if (focusAnimationEnabled && !DISABLE_ANIMATIONS_FOR_TESTING) {
                if (animCounter >= focusAnimationMaxValue) {
                    step = -1 * focusAnimationStep
                } else if (animCounter <= 0) {
                    step = focusAnimationStep
                }
                animCounter += step
                postInvalidate()
            }
        }
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(presenter.circleCenterX.toFloat(), presenter.circleCenterY.toFloat(),
                presenter.circleRadius(animCounter, animMoveFactor), erasePaint)

        if (focusBorderSize > 0) {
            path.apply {
                reset()
                moveTo(presenter.circleCenterX.toFloat(), presenter.circleCenterY.toFloat())
                addCircle(presenter.circleCenterX.toFloat(), presenter.circleCenterY.toFloat(),
                        presenter.circleRadius(animCounter, animMoveFactor), Path.Direction.CW)
                canvas.drawPath(this, circleBorderPaint!!)
            }
        }
    }

    private fun drawRoundedRectangle(canvas: Canvas) {
        val left = presenter.roundRectLeft(animCounter, animMoveFactor)
        val top = presenter.roundRectTop(animCounter, animMoveFactor)
        val right = presenter.roundRectRight(animCounter, animMoveFactor)
        val bottom = presenter.roundRectBottom(animCounter, animMoveFactor)

        rectF.apply {
            set(left, top, right, bottom)
            canvas.drawRoundRect(this, roundRectRadius.toFloat(), roundRectRadius.toFloat(), erasePaint)
        }
        if (focusBorderSize > 0) {
            path.apply {
                reset()
                moveTo(presenter.circleCenterX.toFloat(), presenter.circleCenterY.toFloat())
                addRoundRect(rectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), Path.Direction.CW)
                canvas.drawPath(this, circleBorderPaint!!)
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
        private const val DEFAULT_ANIM_COUNTER = 20.0

        @VisibleForTesting
        var DISABLE_ANIMATIONS_FOR_TESTING = false

        internal fun instance(activity: Activity, props: Properties, pre: Presenter) =
                FancyImageView(activity).apply {
                    setPresenter(pre)
                    bgColor = props.backgroundColor
                    focusAnimationMaxValue = props.focusAnimationMaxValue
                    focusAnimationStep = props.focusAnimationStep
                    focusAnimationEnabled = props.focusAnimationEnabled
                    focusBorderColor = props.focusBorderColor
                    focusBorderSize = props.focusBorderSize
                    roundRectRadius = props.roundRectRadius
                    dashedLineInfo = props.dashedLineInfo

                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)

                }
    }
}
