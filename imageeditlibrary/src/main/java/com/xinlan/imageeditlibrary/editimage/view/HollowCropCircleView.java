package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xinlan.imageeditlibrary.R;


public class HollowCropCircleView extends View {
    private static final String TAG = "@vir CircleView";
    private static int STATUS_IDLE = 1;// 空闲状态
    private static int STATUS_MOVE = 2;// 移动状态
    private static int STATUS_SCALE = 3;// 缩放状态

    private final int CIRCLE_WIDTH = 60;
    private Context mContext;
    private float oldx, oldy;
    private int status = STATUS_IDLE;
    private float mCx, mCy;
    private float mRadius;


    private Paint mBackgroundPaint = new Paint();// 背景Paint
    private Bitmap circleBit;
    private Rect circleRect = new Rect();
    private RectF handleCircleRect;

    private RectF imageRect = new RectF();// 存贮图片位置信息


    public HollowCropCircleView(Context context) {
        super(context);
        init(context);
    }

    public HollowCropCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HollowCropCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        Bitmap pattern = BitmapFactory.decodeResource(context.getResources(), R.drawable
                .fill_pattern);
        BitmapShader bitmapShader = new BitmapShader(pattern, Shader.TileMode.REPEAT, Shader
                .TileMode.REPEAT);
        mBackgroundPaint.setShader(bitmapShader);
        mBackgroundPaint.setAlpha(200);
        circleBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_rotate);
        circleRect.set(0, 0, circleBit.getWidth(), circleBit.getHeight());
        handleCircleRect = new RectF(0, 0, CIRCLE_WIDTH, CIRCLE_WIDTH);
    }

    /**
     * 重置剪裁面
     *
     * @param rect
     */
    public void setCropRect(RectF rect) {
        imageRect.set(rect);
        mCx = rect.centerX();
        mCy = rect.centerY();
        mRadius = Math.min(rect.height(), rect.width()) / 4;
        invalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        canvas.drawCircle(mCx, mCy, mRadius, mBackgroundPaint);

        // 绘制四个控制点
        int radius = CIRCLE_WIDTH >> 1;
        handleCircleRect.set(mCx + mRadius - radius, mCy + mRadius - radius, mCx + mRadius +
                radius, mCy + mRadius + radius);

        canvas.drawBitmap(circleBit, circleRect, handleCircleRect, null);
    }

    /**
     * 触摸事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = true;
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (handleCircleRect.contains(x, y)) {// 选择控制点
                    ret = true;
                    status = STATUS_SCALE;// 进入缩放状态
                } else if (Math.sqrt(Math.pow(x - mCx, 2) + Math.pow(y - mCy, 2)) <= mRadius) {//
                    // 选择缩放框内部
                    ret = true;
                    status = STATUS_MOVE;// 进入移动状态
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUS_SCALE) {// 缩放控制
                    scaleCropController(x, y);
                } else if (status == STATUS_MOVE) {// 移动控制
                    // System.out.println("移动控制");
                    translateCrop(x - oldx, y - oldy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                status = STATUS_IDLE;// 回归空闲状态
                break;
        }// end switch

        // 记录上一次动作点
        oldx = x;
        oldy = y;

        return ret;
    }

    /**
     * 移动剪切框
     *
     * @param dx
     * @param dy
     */
    private void translateCrop(float dx, float dy) {
        mCx += dx;
        mCy += dy;

        invalidate();
    }


    /**
     * 操作控制点 控制缩放
     *
     * @param x
     * @param y
     */
    private void scaleCropController(float x, float y) {
        float anchorX = mCx - mRadius;
        float anchorY = mCy - mRadius;
        mRadius = Math.min(Math.abs(x - anchorX), Math.abs(y - anchorY)) / 2;
        mCx = anchorX + (anchorX - x > 0 ? -mRadius : mRadius);
        mCy = anchorY + (anchorY - y > 0 ? -mRadius : mRadius);
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public float getCx() {
        return mCx;
    }

    public float getCy() {
        return mCy;
    }

    public float getRadius() {
        return mRadius;
    }
}// end class
