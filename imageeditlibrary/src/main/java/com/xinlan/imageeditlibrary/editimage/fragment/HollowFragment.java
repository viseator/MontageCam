package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.cache.BitmapCache;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.view.HollowCropCircleView;
import com.xinlan.imageeditlibrary.editimage.view.HollowCropRectView;
import com.xinlan.imageeditlibrary.editimage.view.HollowView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;

public class HollowFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_HOLLOW;
    private static final String TAG = "@vir HollowFragment";
    private View mainView;
    private View backToMenu;// 返回主菜单
    private PaintModeView mPaintModeView;
    private BitmapCache.CacheStateChangeListener mListener;

    private HollowView mHollowView;
    private HollowCropRectView mHollowCropRectView;
    private HollowCropCircleView mHollowCropCircleView;
    private SeekBar mStokenWidthSeekBar;
    private ImageButton mCropButton;
    private ImageButton mCropCircleButton;


    public boolean isEraser = false;//是否是擦除模式

    private SaveCustomPaintTask mSavePaintImageTask;

    public static HollowFragment newInstance() {
        HollowFragment fragment = new HollowFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_hollow, container, false);
        mHollowView = getActivity().findViewById(R.id.hollow_view);
        mHollowView.setCacheListener(mListener);
        mHollowCropRectView = getActivity().findViewById(R.id.hollow_crop_rect_panel);
        mHollowCropCircleView = getActivity().findViewById(R.id.hollow_crop_circle_panel);
        backToMenu = mainView.findViewById(R.id.hollow_back_to_main);
        mCropButton = mainView.findViewById(R.id.hollow_crop_rect_button);
        mCropCircleButton = mainView.findViewById(R.id.hollow_crop_circle_button);
        mPaintModeView = mainView.findViewById(R.id.hollow_paint_thumb);
        mStokenWidthSeekBar = mainView.findViewById(R.id.hollow_stoke_width_seekbar);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu.setOnClickListener(this);// 返回主菜单
        mCropButton.setOnClickListener(this);
        mCropCircleButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == backToMenu) {
            savePaintImage(true);
        } else if (v == mCropButton) {
            if (mHollowCropRectView.getVisibility() != View.VISIBLE) {
                mHollowCropRectView.setVisibility(View.VISIBLE);
                mCropButton.setImageResource(R.drawable.ic_action_tick);
                mHollowCropRectView.setCropRect(activity.mainImage.getBitmapRect());
            } else {
                mHollowView.hollowRect(mHollowCropRectView.getCropRect());
                mCropButton.setImageResource(R.drawable.image_edit_icon_crop);
                mHollowCropRectView.setVisibility(View.GONE);
            }
        } else if (v == mCropCircleButton) {
            if (mHollowCropCircleView.getVisibility() != View.VISIBLE) {
                mHollowCropCircleView.setVisibility(View.VISIBLE);
                mCropCircleButton.setImageResource(R.drawable.ic_action_tick);
                mHollowCropCircleView.setCropRect(activity.mainImage.getBitmapRect());
            } else {
                mHollowView.hollowCircle(mHollowCropCircleView.getCx(), mHollowCropCircleView
                        .getCy(), mHollowCropCircleView.getRadius());
                mCropCircleButton.setImageResource(R.drawable.ic_donut_large);
                mHollowCropCircleView.setVisibility(View.GONE);
            }
        }
    }

    public void backToMain() {
        activity.mainImage.setVisibility(View.VISIBLE);
        mHollowView.resetCache();
        activity.mode = EditImageActivity.MODE_NONE;
//        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mFrameLayout.setBackgroundColor(getResources().getColor(R.color.main_backgroud));

        mHollowView.setVisibility(View.GONE);
        mHollowView.reset();
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_HOLLOW;
        mPaintModeView.setPaintStrokeColor(Color.WHITE);
        mHollowView.setFragment(this);
        mHollowView.resetBitmap(activity.mainBitmap.copy(activity.mainBitmap.getConfig(), false),
                true);

//        activity.mainImage.setImageBitmap(activity.mainBitmap);
        this.mHollowView.setVisibility(View.VISIBLE);
    }


    private void updatePaintView() {
        isEraser = false;

        this.mHollowView.setWidth(mPaintModeView.getStokenWidth());
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(30);
        updatePaintView();
        if (mPaintModeView.getMeasuredHeight() == 0) {
            mainView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void savePaintImage(boolean isBack) {
        if (mHollowCropCircleView.getVisibility() == View.VISIBLE) {
            if (!isBack) {
                mHollowView.hollowCircle(mHollowCropCircleView.getCx(),mHollowCropCircleView
                        .getCy(),mHollowCropCircleView.getRadius());
            }
            mHollowCropCircleView.setVisibility(View.GONE);
        }
        if (mHollowCropRectView.getVisibility() == View.VISIBLE) {
            if (!isBack) {
                mHollowView.hollowRect(mHollowCropRectView.getCropRect());
            }
            mHollowCropRectView.setVisibility(View.GONE);
        }
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveCustomPaintTask(activity);
        mSavePaintImageTask.execute(activity.mainBitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
    }

    private final class SaveCustomPaintTask extends StickerTask {

        public SaveCustomPaintTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);

            if (mHollowView.getPaintBit() != null) {
                canvas.drawBitmap(mHollowView.getPaintBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            activity.changeMainBitmap(result);
            backToMain();
        }
    }//end inner class

    public void undo() {
        mHollowView.undo();
    }

    public void redo() {
        mHollowView.redo();
    }

    public void setCacheListener(BitmapCache.CacheStateChangeListener listener) {
        if (mHollowView == null) {
            mListener = listener;
        } else {
            mHollowView.setCacheListener(listener);
        }
    }

}// end class
