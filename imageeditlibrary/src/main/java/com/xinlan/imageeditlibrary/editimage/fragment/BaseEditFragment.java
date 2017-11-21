package com.xinlan.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

/**
 * Created by panyi on 2017/3/28.
 */

public abstract class BaseEditFragment extends Fragment {
    public EditImageActivity activity;

    protected EditImageActivity ensureEditActivity() {
        if (activity == null) {
            activity = (EditImageActivity) getActivity();
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureEditActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public abstract void onShow();

    public abstract void backToMain();
}//end class
