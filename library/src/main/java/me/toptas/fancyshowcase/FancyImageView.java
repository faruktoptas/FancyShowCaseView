package me.toptas.fancyshowcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private Paint mBackgroundPaint, mErasePaint;
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mRadius, mLeft, mTop;
    private int mAnimCounter = 0;
    private int mStep = 1;
    private double mAnimMoveFactor = 1;

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

    }

    /**
     * Setting parameters for background an animation
     * @param backgroundColor background color
     * @param left focus animation left
     * @param top focus animation top
     * @param radius focus animation radius
     * @param animMoveFactor focus animation move factor
     */
    public void setParameters(int backgroundColor, int left, int top, int radius, int animMoveFactor) {
        mBackgroundColor = backgroundColor;
        mLeft = left;
        mTop = top;
        mRadius = radius;
        mAnimMoveFactor = animMoveFactor;
    }

    /**
     * Draws background and moving focus area
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
        if (mRadius > 0) {
            mAnimCounter = mAnimCounter + mStep;
            canvas.drawCircle(mLeft, mTop, (float) (mRadius + mAnimCounter * mAnimMoveFactor), mErasePaint);

            if (mAnimCounter == ANIM_COUNTER_MAX) {
                mStep = -1;
            } else if (mAnimCounter == 0) {
                mStep = 1;
            }
            postInvalidate();
        }

    }
}
