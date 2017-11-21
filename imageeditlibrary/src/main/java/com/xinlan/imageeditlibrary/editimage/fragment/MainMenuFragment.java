package com.xinlan.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;


/**
 * 工具栏主菜单
 *
 * @author panyi
 */
public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    private static final String TAG = "@vir MainMenuFragment";
    private View mainView;

    private ImageView rotateButton;
    private ImageView trimButton;
    private ImageView filterButton;
    private ImageView textButton;
    private ImageView brushButton;
    private ImageView beautiButton;

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rotateButton = mainView.findViewById(R.id.edit_panel_rotate);
        trimButton = mainView.findViewById(R.id.edit_panel_trim);
        filterButton = mainView.findViewById(R.id.edit_panel_filter);
        textButton = mainView.findViewById(R.id.edit_panel_text);
        brushButton = mainView.findViewById(R.id.edit_panel_brush);
        beautiButton = mainView.findViewById(R.id.edit_panel_beauty);

        rotateButton.setOnClickListener(this);
        trimButton.setOnClickListener(this);
        filterButton.setOnClickListener(this);
        textButton.setOnClickListener(this);
        brushButton.setOnClickListener(this);
        beautiButton.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {

    }

    @Override
    public void onClick(View v) {
        if (v == rotateButton) {
            activity.onRotateButtonClicked();
        } else if (v == trimButton) {
            activity.onTrimButtonClicked();
        } else if (v == filterButton) {
            activity.onFilterButtonClicked();
        } else if (v == textButton) {
            activity.onTextButtonClicked();
        } else if (v == brushButton) {
            activity.onBrushButtonClicked();
        } else if (v == beautiButton) {
            activity.onBeautyButtonClicked();
        }
    }

}// end class
