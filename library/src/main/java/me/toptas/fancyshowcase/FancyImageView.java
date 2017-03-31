package me.toptas.fancyshowcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ftoptas on 13/03/17.
 * ImageView with focus area animation
 */

class FancyImageView extends ImageView {

    private static final int ANIM_COUNTER_MAX = 20;
    private Bitmap mBitmap;
    private Paint mBackgroundPaint, mErasePaint, mCircleBorderPaint;
    private int mBackgroundColor = Color.TRANSPARENT;
    public int mFocusBorderColor = Color.TRANSPARENT;
    public int mFocusBorderSize = 5;
    private Calculator mCalculator;
    private int mAnimCounter = 0;
    private int mStep = 1;
    private double mAnimMoveFactor = 1;
    private Path mPath;

    public FancyImageView(Context context) {
        super(context);
        init();
    }

    public FancyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FancyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FancyImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initializations for background and paints
     */
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
        setWillNotDraw(false);
        setBackgroundColor(Color.TRANSPARENT);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAlpha(0xFF);

        mErasePaint = new Paint();
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setAlpha(0xFF);

        mPath = new Path();
        mCircleBorderPaint = new Paint();
        mCircleBorderPaint.setColor(mFocusBorderColor);
        mCircleBorderPaint.setStrokeWidth(mFocusBorderSize);
        mCircleBorderPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Setting parameters for background an animation
     *
     * @param backgroundColor background color
     * @param calculator      calculator object for calculations
     */
    public void setParameters(int backgroundColor, Calculator calculator) {
        mBackgroundColor = backgroundColor;
        mAnimMoveFactor = 1;
        mCalculator = calculator;
    }

    /**
     * Setting parameters for focus border
     *
     * @param focusBorderColor
     * @param focusBorderSize
     */
    public void setBorderParameters(int focusBorderColor, int focusBorderSize) {
        mCircleBorderPaint.setColor(focusBorderColor);
        mCircleBorderPaint.setStrokeWidth(focusBorderSize);
    }

    /**
     * Draws background and moving focus area
     *
     * @param canvas draw canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(mBackgroundColor);

        }
        canvas.drawBitmap(mBitmap, 0, 0, mBackgroundPaint);
        if (mCalculator.hasFocus()) {
            mAnimCounter = mAnimCounter + mStep;
            if (mCalculator.getFocusShape().equals(FocusShape.CIRCLE)) {
                drawCircle(canvas);
            } else {
                drawRoundedRectangle(canvas);
            }
            if (mAnimCounter == ANIM_COUNTER_MAX) {
                mStep = -1;
            } else if (mAnimCounter == 0) {
                mStep = 1;
            }
            postInvalidate();
        }
    }

    /**
     * Draws focus circle
     *
     * @param canvas canvas to draw
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCalculator.getCircleCenterX(), mCalculator.getCircleCenterY(),
                mCalculator.circleRadius(mAnimCounter, mAnimMoveFactor), mErasePaint);

        mPath.reset();
        mPath.moveTo(mCalculator.getCircleCenterX(), mCalculator.getCircleCenterY());
        mPath.addCircle(mCalculator.getCircleCenterX(), mCalculator.getCircleCenterY(),
                mCalculator.circleRadius(mAnimCounter, mAnimMoveFactor), Path.Direction.CW);
        canvas.drawPath(mPath, mCircleBorderPaint);
    }

    /**
     * Draws focus rounded rectangle
     *
     * @param canvas canvas to draw
     */
    private void drawRoundedRectangle(Canvas canvas) {
        float width = mCalculator.roundRectWidth();
        float left = mCalculator.roundRectLeft();
        float top = mCalculator.roundRectTop(mAnimCounter, mAnimMoveFactor);
        float right = mCalculator.roundRectRight();
        float bottom = mCalculator.roundRectBottom(mAnimCounter, mAnimMoveFactor);
        canvas.drawRect(left, top, right, bottom, mErasePaint);
        canvas.drawCircle(left, mCalculator.getCircleCenterY(),
                mCalculator.roundRectLeftCircleRadius(mAnimCounter, mAnimMoveFactor), mErasePaint);
        canvas.drawCircle(left + width, mCalculator.getCircleCenterY(),
                mCalculator.roundRectLeftCircleRadius(mAnimCounter, mAnimMoveFactor), mErasePaint);
    }
}
