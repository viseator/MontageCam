package com.xinlan.imageeditlibrary.editimage;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.cache.BitmapCache;
import com.xinlan.imageeditlibrary.editimage.fragment.AddTextFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.BaseEditFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.BeautyFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.CropFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.FilterListFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.HollowFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.MainMenuFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.RotateFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.xinlan.imageeditlibrary.editimage.view.UploadDialog;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

import java.io.File;

/**
 * 一个幽灵
 * 共产主义的幽灵
 * 在欧洲徘徊
 * 旧欧洲的一切势力，
 * 教皇和沙皇、
 * 梅特涅和基佐、
 * 法国的激进党人和德国的警察，
 * 都为驱除这个幽灵而结成了神圣同盟
 * -----《共产党宣言》
 * <p>
 * 图片编辑 主页面
 *
 * @author panyi
 *         <p>
 *         包含 1.贴图 2.滤镜 3.剪裁 4.底图旋转 功能
 */
public class EditImageActivity extends BaseActivity implements OnClickListener {
    public static final int OUT_HEIGHT = 2560;
    public static final int OUT_WIDTH = 1440;
    private static final String TAG = "@vir EditImageActivity";
    public static final String INTENT_START_CAMERA_ACTIVITY = "com.viseator.START_CAMERA_ACTIVITY";
    public static final String BITMAP_FILE = "bitmap";
    private UploadDialog mDialog;
    public static final String FILE_PATH = "file_path";
    public static final String CLIP_LABEL = "token";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String EXTRA_FRONT = "extra_front";
    public static final String EXTRA_ROTATION = "extra_rotation";

    public static final String SAVE_FILE_PATH = "save_file_path";

    public static final String IMAGE_IS_EDIT = "image_is_edit";

    public static final int MODE_NONE = 0;
    public static final int MODE_STICKERS = 1;// 贴图模式
    public static final int MODE_FILTER = 2;// 滤镜模式
    public static final int MODE_CROP = 3;// 剪裁模式
    public static final int MODE_ROTATE = 4;// 旋转模式
    public static final int MODE_TEXT = 5;// 文字模式
    public static final int MODE_PAINT = 6;//绘制模式
    public static final int MODE_BEAUTY = 7;//美颜模式
    public static final int MODE_HOLLOW = 8;

    public static final int CALL_EDIT_ACTIVITY = 0x11;
    public String filePath;// 需要编辑图片路径
    public String saveFilePath;// 生成的新图片路径
    private boolean isFront;
    private float rotation;
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    private LoadImageTask mLoadImageTask;

    public int mode = MODE_NONE;// 当前操作模式

    protected int mOpTimes = 0;
    private boolean shouldSave = false;
    protected boolean isBeenSaved = false;
    private FragmentManager mFragmentManager;
    private EditImageActivity mContext;
    public Bitmap mainBitmap;// 底层显示Bitmap

    public ImageViewTouch mainImage;
    public ImageView hollowButton;
    private ImageView backButton;  // v updated
    private ImageView saveButton;// 保存按钮 v updated
    private ImageView undoButton;
    private ConstraintLayout mConstraintLayout;

    public StickerView mStickerView;// 贴图层View
    public CropImageView mCropPanel;// 剪切操作控件
    public RotateImageView mRotatePanel;// 旋转操作控件
    public TextStickerView mTextStickerView;//文本贴图显示View
    public CustomPaintView mPaintView;//涂鸦模式画板
    public FrameLayout mFrameLayout;

    private MainMenuFragment mMainMenuFragment;// Menu
    public HollowFragment mHollowFragment;
    public StickerFragment mStickerFragment;// 贴图Fragment
    public FilterListFragment mFilterListFragment;// 滤镜FilterListFragment
    public CropFragment mCropFragment;// 图片剪裁Fragment
    public RotateFragment mRotateFragment;// 图片旋转Fragment
    public AddTextFragment mAddTextFragment;//图片添加文字
    public PaintFragment mPaintFragment;//绘制模式Fragment
    public BeautyFragment mBeautyFragment;//美颜模式Fragment

    private BaseEditFragment mCurrentPanelFragment;

    private SaveImageTask mSaveImageTask;

    private BitmapCache mBitmapCache = new BitmapCache();
    private BitmapCache.CacheStateChangeListener mHollowViewCacheListener = new BitmapCache
            .CacheStateChangeListener() {

        @Override
        public void canUndo(boolean b) {
            if (mode == MODE_HOLLOW) {
                undoButton.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void canRedo(boolean b) {
        }
    };

    private BitmapCache.CacheStateChangeListener mCacheListener = new BitmapCache
            .CacheStateChangeListener() {
        @Override
        public void canUndo(boolean b) {
            undoButton.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        @Override
        public void canRedo(boolean b) {
        }
    };

    /**
     * @param context
     * @param editImagePath
     * @param outputPath
     * @param requestCode
     */
    public static void start(Activity context, final String editImagePath, final String
            outputPath, boolean isFront, float rotation, final int requestCode) {
        if (TextUtils.isEmpty(editImagePath)) {
            Toast.makeText(context, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(context, EditImageActivity.class);
        it.putExtra(EditImageActivity.FILE_PATH, editImagePath);
        it.putExtra(EditImageActivity.EXTRA_OUTPUT, outputPath);
        it.putExtra(EditImageActivity.EXTRA_FRONT, isFront);
        it.putExtra(EditImageActivity.EXTRA_ROTATION, rotation);

        context.startActivityForResult(it, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
    }

    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);// 保存图片路径
        isFront = getIntent().getBooleanExtra(EXTRA_FRONT, false);
        rotation = getIntent().getFloatExtra(EXTRA_ROTATION, 0f);

        loadImage(filePath);
    }

    private void initView() {
        mContext = this;
        mBitmapCache.setListener(mCacheListener);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        mConstraintLayout = findViewById(R.id.image_edit_relativelayout);
        mFrameLayout = findViewById(R.id.work_space);
        saveButton = findViewById(R.id.edit_main_confirm);
        saveButton.setOnClickListener(new SaveBtnClick());

        mainImage = findViewById(R.id.main_image);
        backButton = findViewById(R.id.edit_image_main_back);// 退出按钮
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        hollowButton = findViewById(R.id.edit_blank_button);
        hollowButton.setOnClickListener(this);

        mStickerView = findViewById(R.id.sticker_panel);
        mCropPanel = findViewById(R.id.crop_panel);
        mRotatePanel = findViewById(R.id.rotate_panel);
        mTextStickerView = findViewById(R.id.text_sticker_panel);
        mPaintView = findViewById(R.id.custom_paint_view);

        mMainMenuFragment = MainMenuFragment.newInstance();
        mHollowFragment = HollowFragment.newInstance();
        mStickerFragment = StickerFragment.newInstance();
        mFilterListFragment = FilterListFragment.newInstance();
        mCropFragment = CropFragment.newInstance();
        mRotateFragment = RotateFragment.newInstance();
        mAddTextFragment = AddTextFragment.newInstance();
        mPaintFragment = PaintFragment.newInstance();
        mBeautyFragment = BeautyFragment.newInstance();

        mHollowFragment.setCacheListener(mHollowViewCacheListener);

        undoButton = findViewById(R.id.edit_main_undo);
        undoButton.setOnClickListener(this);


        mainImage.setFlingListener(new ImageViewTouch.OnImageFlingListener() {
            @Override
            public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if (velocityY > 1) {
                    closeInputMethod();
                }
            }
        });
        initMainMenuFragment();
    }

    /**
     * 关闭输入法
     */
    private void closeInputMethod() {
        if (mAddTextFragment.isAdded()) {
            mAddTextFragment.hideInput();
        }
    }

    // TODO: 11/21/17 handle new fragment logic

    private void initMainMenuFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.edit_panel_container, mMainMenuFragment);
        mCurrentPanelFragment = mMainMenuFragment;
        fragmentTransaction.commit();
    }

    public void switchPanelFragment(BaseEditFragment baseEditFragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.remove(mCurrentPanelFragment);
        fragmentTransaction.add(R.id.edit_panel_container, baseEditFragment);
        mCurrentPanelFragment = baseEditFragment;
        fragmentTransaction.commit();
    }

    public void backToMainMenu() {
        mode = MODE_NONE;
        hollowButton.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.remove(mCurrentPanelFragment);
        fragmentTransaction.add(R.id.edit_panel_container, mMainMenuFragment);
        mCurrentPanelFragment = mMainMenuFragment;
        fragmentTransaction.commit();
    }

    /**
     * 异步载入编辑图片
     *
     * @param filepath
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    @Override
    public void onClick(View v) {
        if (v.getVisibility() != View.VISIBLE) {
            return;
        }
        if (v == undoButton) {
            if (mode == MODE_HOLLOW) {
                mHollowFragment.undo();
            } else {
                changeMainBitmap(mBitmapCache.undo(), true);
            }
        } else if (v == hollowButton) {
            mode = MODE_HOLLOW;
            hollowButton.setVisibility(View.INVISIBLE);
            switchPanelFragment(mHollowFragment);
        } else {
            undoButton.setVisibility(View.GONE);

        }
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            BitmapFactory.Options option = new BitmapFactory.Options();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                option.outConfig = Bitmap.Config.ARGB_8888;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(params[0], option);
            Bitmap scaledBitmap = BitmapUtils.scaleBitmap(bitmap, OUT_HEIGHT, OUT_WIDTH);
            Bitmap rotatedBitmap = BitmapUtils.rotateBitmap(scaledBitmap, rotation);
            return rotatedBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (isFront) {
                result = BitmapUtils.xMirrorBitmap(result);
            }
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            mainBitmap.setHasAlpha(true);
            mBitmapCache.push(mainBitmap.copy(mainBitmap.getConfig(), false));
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }// end inner class

    @Override
    public void onBackPressed() {
        if (mode == MODE_NONE) {
            if (mDialog != null && mDialog.isVisible()) {
                mDialog.dismiss();
            }
            finish();
        }
        fragmentBackToMain();
    }

    public void fragmentBackToMain() {
        if (mode != MODE_NONE) {
            mCurrentPanelFragment.backToMain();
        }
    }

    public void applyChange() {
        switch (mode) {
            case MODE_STICKERS:
                mStickerFragment.applyStickers();// 保存贴图
                break;
            case MODE_FILTER:// 滤镜编辑状态
                mFilterListFragment.applyFilterImage();// 保存滤镜贴图
                break;
            case MODE_CROP:// 剪切图片保存
                mCropFragment.applyCropImage();
                break;
            case MODE_ROTATE:// 旋转图片保存
                mRotateFragment.applyRotateImage();
                break;
            case MODE_TEXT://文字贴图 图片保存
                mAddTextFragment.applyTextImage();
                break;
            case MODE_PAINT://保存涂鸦
                mPaintFragment.savePaintImage();
                break;
            case MODE_BEAUTY://保存美颜后的图片
                mBeautyFragment.applyBeauty();
                break;
            case MODE_HOLLOW:
                mHollowFragment.savePaintImage();
                break;
            default:
                break;
        }
    }

    /**
     * 保存按钮 点击退出
     *
     * @author panyi
     */
    private final class SaveBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mode != MODE_NONE) {
                applyChange();
            } else {
                doSaveImage();
            }
        }
    }

    protected void doSaveImage() {

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
    }

    public void changeMainBitmap(Bitmap newBit, boolean changeByCache) {
        if (newBit == null) {
            return;
        }

        mainBitmap = newBit;
        mainImage.setImageBitmap(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    public void changeMainBitmap(Bitmap newBit) {
        if (newBit == null) return;

        mainBitmap = newBit;
        mBitmapCache.push(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mainImage.setImageBitmap(mainBitmap);

        increaseOpTimes();
        if (shouldSave) {
            shouldSave = false;
            doSaveImage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
        mBitmapCache.reset();
    }

    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    protected void onSaveTaskDone() {
        uploadImage();
    }

    private StringRequestListener mUploadListener = new StringRequestListener() {
        @Override
        public void onResponse(String response) {
            if (!mDialog.isAdded()) {
                return;
            }
            mDialog.progressEnd();
            String resultString = mDialog.genShareText(response);
            mDialog.setResultText(resultString);
            mDialog.setTitle(getResources().getString(R.string.your_token));
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context
                    .CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(CLIP_LABEL, resultString);
            clipboardManager.setPrimaryClip(data);
            mDialog.setListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(INTENT_START_CAMERA_ACTIVITY);
                    intent.putExtra(BITMAP_FILE, saveFilePath);
                    LocalBroadcastManager.getInstance(EditImageActivity.this).sendBroadcast(intent);
                    finish();
                }
            });
            Log.d(TAG, String.valueOf(response));
        }

        @Override
        public void onError(ANError anError) {
            // TODO: 11/21/17 handle network error here
            Log.e(TAG, anError.getErrorDetail());
            Log.e(TAG, anError.getErrorBody());
            new AlertDialog.Builder(EditImageActivity.this).setCustomTitle(null).setMessage(R
                    .string.upload_error).setPositiveButton(R.string.return_, null).create().show();
        }
    };

    private void uploadImage() {
        File file = new File(saveFilePath);
        mDialog = new UploadDialog();
        mDialog.show(getFragmentManager(), TAG);
        AndroidNetworking.upload(getResources().getString(R.string.server_upload)).setPriority
                (Priority.HIGH).addMultipartFile("img", file).build().setUploadProgressListener
                (new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
//                Log.d(TAG, String.valueOf(bytesUploaded / (float) totalBytes));
                mDialog.setProgress((int) (bytesUploaded / (float) totalBytes * 100));
            }
        }).getAsString(mUploadListener);
    }

    /**
     * 保存图像
     * 完成后退出
     */
    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = EditImageActivity.getLoadingDialog(mContext, R.string.saving_image, false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath)) return false;
//            return BitmapUtils.saveBitmap(BitmapUtils.scaleBitmap(params[0], OUT_HEIGHT,
//                    OUT_WIDTH), saveFilePath);
            return BitmapUtils.saveBitmap(params[0], saveFilePath);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result) {
                resetOpTimes();
                onSaveTaskDone();
            } else {
                Toast.makeText(mContext, R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
            dialog.dismiss();
        }


    }//end inner class


    public void onRotateButtonClicked() {
        switchPanelFragment(mRotateFragment);
        hollowButton.setVisibility(View.INVISIBLE);
        mode = MODE_ROTATE;
    }

    public void onTrimButtonClicked() {
        switchPanelFragment(mCropFragment);
        hollowButton.setVisibility(View.INVISIBLE);
        mode = MODE_CROP;
    }

    public void onFilterButtonClicked() {
        switchPanelFragment(mFilterListFragment);
        hollowButton.setVisibility(View.INVISIBLE);
        mode = MODE_FILTER;
    }

    public void onTextButtonClicked() {
        switchPanelFragment(mAddTextFragment);
        hollowButton.setVisibility(View.INVISIBLE);
        mode = MODE_TEXT;
    }
}// end class
