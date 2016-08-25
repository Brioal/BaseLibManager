package com.brioal.baselib.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.baselib.util.klog.KLog;


/**
 * Fragment基类
 * Created by mm on 2016/6/4.
 */

public abstract class BaseFragment extends Fragment {


    protected String TAG = "BaseFragment";
    protected final int TYPE_SET_VIEW = 0;
    protected final int TYPE_UPDATE_VIEW = 1;
    protected Activity mContext;
    protected View mRootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected Bundle saveInstanceState;
    protected Runnable mRunnableLocal = new Runnable() {
        @Override
        public void run() {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "Run loadDataLocal");
            }
            LOADDATALOCAL();
        }
    };

    protected Runnable mRunnableNet = new Runnable() {
        @Override
        public void run() {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "Run loadDataNet");
            }
            LOADDATANET();
        }
    };

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            messageReceived(msg);
        }
    };


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mContext = context;
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onAttach");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onCreate");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onResume");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onDestory");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onCreateView");
        }
        this.inflater = inflater;
        this.container = container;
        this.saveInstanceState = savedInstanceState;

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onViewCreated");
        }
        INITVAR();
        INITVIEW(savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        new Thread(mRunnableLocal).start();
        new Thread(mRunnableNet).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onDestroyView");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SAVEDATALOCAL();
            }
        }).start();
    }


    private void INITVAR() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "initVar");
        }
        initVar();
    }

    public void INITVIEW(Bundle savedInstanceState) {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "initView");
        }
        initView(savedInstanceState);
    }

    public void LOADDATALOCAL() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "loadDataLocal");
        }
        loadDataLocal();
    }

    public void LOADDATANET() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "loadDataNet");
        }
        loadDataNet();
    }

    public void SETVIEW() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "setView");
        }
        setView();
    }

    public void UPDATEVIEW() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "updateView");
        }
        updateView();

    }

    public void SAVEDATALOCAL() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "saveDataLocal");
        }
        saveDataLocal();
    }

    protected void messageReceived(Message message) {
        if (message.what == TYPE_SET_VIEW) {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "setView");
            }
            SETVIEW();
        } else if (message.what == TYPE_UPDATE_VIEW) {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "updateView");
            }
            UPDATEVIEW();
        }
    }

    public abstract void initVar(); //初始化参数

    public abstract void initView(Bundle saveInstanceState); //实例化View相关

    public abstract void loadDataLocal(); //加载本地数据

    public abstract void loadDataNet(); //加载网络数据

    public abstract void setView(); //数据显示到界面

    public abstract void updateView(); //更新数据到界面

    public abstract void saveDataLocal(); //保存数据到本地
}
