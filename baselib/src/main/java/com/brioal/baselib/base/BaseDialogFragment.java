package com.brioal.baselib.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.brioal.baselib.R;

import butterknife.ButterKnife;


/**
 * 底部弹窗Fragment
 * Created by Brioal on 2016/8/4.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    protected View mRootView;
    protected int mTheme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVar();
    }

    public abstract void initVar();

    public abstract void initView();

    public abstract void initWindow(WindowManager.LayoutParams params);

    public abstract void initAnimation(Dialog dialog);

    public abstract void changeTheme();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        mTheme = R.style.RoundDialog;
        changeTheme();
        Dialog dialog = new Dialog(getActivity(), mTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        initView();
        initAnimation(dialog);
        dialog.setContentView(mRootView);
        // 设置宽度为屏宽, 靠近屏幕底部。
        final Window window = dialog.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        initWindow(lp);
        window.setAttributes(lp);
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
