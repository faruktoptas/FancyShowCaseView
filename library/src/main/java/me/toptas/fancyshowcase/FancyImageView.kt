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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View

/**
 * ImageView with focus area animation
 */

internal class FancyImageView : AppCompatImageView {

    private var mBitmap: Bitmap? = null
    private var mBackgroundPaint: Paint? = null
    private var mErasePaint: Paint? = null
    private var mCircleBorderPaint: Paint? = null
    private var mBackgroundColor = Color.TRANSPARENT
    private val mFocusBorderColor = Color.TRANSPARENT
    private var mFocusBorderSize: Int = 0
    private var mRoundRectRadius = 20
    private var mCalculator: Calculator? = null
    private var mAnimCounter: Int = 0
    private var mStep = 1
    private var mAnimMoveFactor = 1.0
    private var mAnimationEnabled = true
    private var mPath: Path? = null
    private var rectF: RectF? = null
    private var mFocusAnimationMaxValue: Int = 0
    private var mFocusAnimationStep: Int = 0

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
        mBackgroundPaint = Paint().apply {
            isAntiAlias = true
            color = mBackgroundColor
            alpha = 0xFF
        }
        mErasePaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            alpha = 0xFF
            isAntiAlias = true
        }
        mPath = Path()
        mCircleBorderPaint = Paint().apply {
            isAntiAlias = true
            color = mFocusBorderColor
            strokeWidth = mFocusBorderSize.toFloat()
            style = Paint.Style.STROKE
        }
        rectF = RectF()
    }

    /**
     * Setting parameters for background an animation
     *
     * @param backgroundColor background color
     * @param calculator      calculator object for calculations
     */
    fun setParameters(backgroundColor: Int, calculator: Calculator) {
        mBackgroundColor = backgroundColor
        mAnimMoveFactor = 1.0
        mCalculator = calculator
    }

    /**
     * Setting parameters for focus border
     *
     * @param focusBorderColor
     * @param focusBorderSize
     */
    fun setBorderParameters(focusBorderColor: Int, focusBorderSize: Int) {
        mFocusBorderSize = focusBorderSize
        mCircleBorderPaint!!.color = focusBorderColor
        mCircleBorderPaint!!.strokeWidth = focusBorderSize.toFloat()
    }

    /**
     * Setting round rectangle radius
     *
     * @param roundRectRadius
     */
    fun setRoundRectRadius(roundRectRadius: Int) {
        mRoundRectRadius = roundRectRadius
    }

    /**
     * Enable/disable animation
     *
     * @param animationEnabled
     */
    fun setAnimationEnabled(animationEnabled: Boolean) {
        mAnimationEnabled = animationEnabled
        mAnimCounter = if (mAnimationEnabled) DEFAULT_ANIM_COUNTER else 0
    }

    /**
     * Draws background and moving focus area
     *
     * @param canvas draw canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(mBackgroundColor)
            }
        }
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBackgroundPaint)
        if (mCalculator?.hasFocus() == true) {
            if (mCalculator!!.focusShape == FocusShape.CIRCLE) {
                drawCircle(canvas)
            } else {
                drawRoundedRectangle(canvas)
            }
            if (mAnimationEnabled) {
                if (mAnimCounter == mFocusAnimationMaxValue) {
                    mStep = -1 * mFocusAnimationStep
                } else if (mAnimCounter == 0) {
                    mStep = mFocusAnimationStep
                }
                mAnimCounter += mStep
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
        canvas.drawCircle(mCalculator!!.circleCenterX.toFloat(), mCalculator!!.circleCenterY.toFloat(),
                mCalculator!!.circleRadius(mAnimCounter, mAnimMoveFactor), mErasePaint!!)

        if (mFocusBorderSize > 0) {
            mPath?.reset()
            mPath?.moveTo(mCalculator!!.circleCenterX.toFloat(), mCalculator!!.circleCenterY.toFloat())
            mPath?.addCircle(mCalculator!!.circleCenterX.toFloat(), mCalculator!!.circleCenterY.toFloat(),
                    mCalculator!!.circleRadius(mAnimCounter, mAnimMoveFactor), Path.Direction.CW)
            canvas.drawPath(mPath!!, mCircleBorderPaint!!)
        }
    }

    /**
     * Draws focus rounded rectangle
     *
     * @param canvas canvas to draw
     */
    private fun drawRoundedRectangle(canvas: Canvas) {
        val left = mCalculator!!.roundRectLeft(mAnimCounter, mAnimMoveFactor)
        val top = mCalculator!!.roundRectTop(mAnimCounter, mAnimMoveFactor)
        val right = mCalculator!!.roundRectRight(mAnimCounter, mAnimMoveFactor)
        val bottom = mCalculator!!.roundRectBottom(mAnimCounter, mAnimMoveFactor)

        rectF!!.set(left, top, right, bottom)
        canvas.drawRoundRect(rectF!!, mRoundRectRadius.toFloat(), mRoundRectRadius.toFloat(), mErasePaint!!)

        if (mFocusBorderSize > 0) {
            mPath?.reset()
            mPath?.moveTo(mCalculator!!.circleCenterX.toFloat(), mCalculator!!.circleCenterY.toFloat())
            mPath?.addRoundRect(rectF, mRoundRectRadius.toFloat(), mRoundRectRadius.toFloat(), Path.Direction.CW)
            canvas.drawPath(mPath!!, mCircleBorderPaint!!)
        }
    }

    fun setFocusAnimationParameters(maxValue: Int, step: Int) {
        mFocusAnimationMaxValue = maxValue
        mFocusAnimationStep = step
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mBitmap != null && !mBitmap!!.isRecycled) {
            mBitmap!!.recycle()
            mBitmap = null
        }
    }

    companion object {
        private const val DEFAULT_ANIM_COUNTER = 20
    }
}
