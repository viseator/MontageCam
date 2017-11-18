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
import android.widget.ImageView;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.view.HollowView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;

public class HollowFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_HOLLOW;
    private static final String TAG = "@vir HollowFragment";
    private View mainView;
    private View backToMenu;// 返回主菜单
    private PaintModeView mPaintModeView;

    private HollowView mHollowView;


    private SeekBar mStokenWidthSeekBar;


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
        mHollowView = (HollowView) getActivity().findViewById(R.id.hollow_view);
        backToMenu = mainView.findViewById(R.id.hollow_back_to_main);
        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.hollow_paint_thumb);
        mStokenWidthSeekBar = (SeekBar) mainView.findViewById(R.id.hollow_stoke_width_seekbar);
        mHollowView.setFragment(this);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        backToMenu.setOnClickListener(this);// 返回主菜单

        mPaintModeView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        savePaintImage();
    }

    public void backToMain() {
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mFrameLayout.setBackgroundColor(getResources().getColor(R.color.main_backgroud));

        this.mHollowView.setVisibility(View.GONE);
        this.mHollowView.setInited(false);

    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_HOLLOW;
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

    public void savePaintImage() {
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
            mHollowView.reset();
            activity.changeMainBitmap(result);
            backToMain();
        }
    }//end inner class


}// end class
