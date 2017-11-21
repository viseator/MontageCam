package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 图片旋转Fragment
 *
 * @author 潘易
 */
public class RotateFragment extends BaseEditFragment implements OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final String TAG = RotateFragment.class.getName();
    private View mainView;
    private View backToMenu;// 返回主菜单
    private ImageView leftButton;
    private ImageView rightButton;
    private RotateImageView mRotatePanel;// 旋转效果展示控件
    private int rotate = 0;

    public static RotateFragment newInstance() {
        RotateFragment fragment = new RotateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_rotate, container, false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        leftButton = mainView.findViewById(R.id.left_rotate);
        rightButton = mainView.findViewById(R.id.right_rotate);

        this.mRotatePanel = ensureEditActivity().mRotatePanel;
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        onShow();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onShow() {
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.mRotatePanel.addBit(activity.mainBitmap, activity.mainImage.getBitmapRect());
        activity.mRotatePanel.reset();
        activity.mRotatePanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == rightButton) {
            rotate = (rotate + 90) % 360;
        } else {
            rotate = (rotate - 90) % 360;
        }
        mRotatePanel.rotateImage(rotate);
    }


    /**
     * 返回按钮逻辑
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            applyRotateImage();
        }
    }// end class

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.backToMainMenu();
        activity.mainImage.setVisibility(View.VISIBLE);
        this.mRotatePanel.setVisibility(View.GONE);
    }

    /**
     * 保存旋转图片
     */
    public void applyRotateImage() {
        SaveRotateImageTask task = new SaveRotateImageTask();
        task.execute(activity.mainBitmap);
    }

    /**
     * 保存图片线程
     *
     * @author panyi
     */
    private final class SaveRotateImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        //private Dialog dialog;

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //dialog.dismiss();
        }

        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            //dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
            //        false);
            //dialog.show();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF imageRect = mRotatePanel.getImageNewRect();
            Bitmap originBit = params[0];
            Bitmap result = Bitmap.createBitmap((int) imageRect.width(), (int) imageRect.height()
                    , Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            int w = originBit.getWidth() >> 1;
            int h = originBit.getHeight() >> 1;
            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF dst = new RectF(left, top, left + originBit.getWidth(), top + originBit
                    .getHeight());
            canvas.save();
            canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(), imageRect.width() / 2,
                    imageRect.height() / 2);
            canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2, imageRect.height
                    () / 2);

            canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(), originBit.getHeight
                    ()), dst, null);
            canvas.restore();

            //saveBitmap(result, activity.saveFilePath);// 保存图片
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            if (result == null) return;

            // 切换新底图
            activity.changeMainBitmap(result);
            backToMain();
        }
    }// end inner class

    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bm
     */
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("保存文件--->" + f.getAbsolutePath());
    }
}// end class
