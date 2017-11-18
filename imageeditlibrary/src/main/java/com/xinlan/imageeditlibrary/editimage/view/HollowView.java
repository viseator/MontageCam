package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.cache.BitmapCache;
import com.xinlan.imageeditlibrary.editimage.fragment.HollowFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by panyi on 17/2/11.
 */

public class HollowView extends View {
    private static final String TAG = "@vir HollowView";
    private Context mContext;
    private Paint mPaint;
    private Bitmap mDrawBit;
    private Bitmap mPendingBitmap = null;

    private HollowFragment mFragment;
    private BitmapCache mBitmapCache = new BitmapCache();
    private Matrix mMatrix = null;

    private Canvas mPaintCanvas = null;

    private float last_x;
    private float last_y;


    public HollowView(Context context) {
        super(context);
        init(context);
    }

    public HollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HollowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HollowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //System.out.println("width = "+getMeasuredWidth()+"     height = "+getMeasuredHeight());
//        if (mDrawBit == null) {
//            generatorBit();
//        }
//    }

//    private void generatorBit() {
//        mDrawBit = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config
//                .ARGB_8888);
//        mDrawBit.setHasAlpha(true);
//        mPaintCanvas = new Canvas(mDrawBit);
//        if (mPendingBitmap != null) {
//            mPaintCanvas.drawBitmap(mPendingBitmap, mMatrix, null);
//            mPendingBitmap = null;
//        }
//    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawBit != null) {
            canvas.drawBitmap(mDrawBit, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                last_x = x;
                last_y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                mPaintCanvas.drawLine(last_x, last_y, x, y, mPaint);
                last_x = x;
                last_y = y;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mBitmapCache.push(mDrawBit.copy(mDrawBit.getConfig(), true));
                ret = false;
                break;
        }
        return ret;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
        mBitmapCache.reset();
    }

    public void resetBitmap(Bitmap bitmap, boolean initial) {
        if (initial) {
            mBitmapCache.push(bitmap);
        }

        if (mDrawBit == null) {
            mDrawBit = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap
                    .getConfig());
            mDrawBit.setHasAlpha(true);
            mPaintCanvas = new Canvas(mDrawBit);
        }
        if (mPaintCanvas != null) {
            mPaintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPaintCanvas.drawBitmap(bitmap, mMatrix, null);
            postInvalidate();
        } else {
            mPendingBitmap = bitmap;
        }
    }


    public Bitmap getPaintBit() {
        return mDrawBit;
    }

    public void reset() {
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            mDrawBit.recycle();
        }
        mBitmapCache.reset();
    }

    public void setFragment(HollowFragment hollowFragment) {
        mFragment = hollowFragment;
        mFragment.activity.mainImage.setVisibility(GONE);
        mFragment.activity.mFrameLayout.setBackground(mContext.getDrawable(R.drawable
                .repeat_fill_pattern));
        mMatrix = new Matrix(mFragment.activity.mainImage.getImageViewMatrix());
    }

    public void undo() {
        resetBitmap(mBitmapCache.undo(), false);
    }

    public void redo() {
        resetBitmap(mBitmapCache.redo(), false);
    }
}//end class
