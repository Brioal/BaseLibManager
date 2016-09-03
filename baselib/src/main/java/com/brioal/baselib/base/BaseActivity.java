package com.brioal.baselib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.brioal.baselib.util.NetWorkUtil;
import com.brioal.baselib.util.klog.KLog;


/**
 * Activity的基类
 * Created by mm on 2016/6/4.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = "BaseActivity";
    protected final int TYPE_SET_VIEW = 0;
    protected final int TYPE_UPDATE_VIEW = 1;
    protected Activity mContext;
    protected Runnable mThreadLocal = new Runnable() {
        @Override
        public void run() {
            LOADDATALOCAL();
        }
    };
    protected Runnable mThreadNet = new Runnable() {
        @Override
        public void run() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onCreate");
        }
        INITVAR();
        INITVIEW(savedInstanceState);
        new Thread(mThreadLocal).start();
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            new Thread(mThreadNet).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onResume");
        }
        initTheme();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onStart");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onStop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "onDestory");
        }
        SAVEDATALOCAL();
    }


    public static void startActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    private void INITVAR() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "initVar");
        }
        initVar();
    }

    private void INITTHEME() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "initTheme");
        }
        initTheme();
    }


    private void INITVIEW(Bundle savedInstanceState) {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "initView");
        }
        initView(savedInstanceState);
    }

    private void LOADDATALOCAL() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "loadDataLocal");
        }
        loadDataLocal();
    }

    private void LOADDATANET() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "loadDataNet");
        }
        loadDataNet();
    }

    private void SETVIEW() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "setView");
        }
        setView();
    }

    private void UPDATEVIEW() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "updateView");
        }
        updateView();
    }

    private void SAVEDATALOCAL() {
        if (!BaseApplication.isRelease) {
            KLog.i(TAG, "saveDataLocal");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveDataLocal();
            }
        }).start();
    }

    public abstract void initVar(); //初始化参数

    public abstract void initView(Bundle savedInstanceState); //初始化View相关

    public abstract void initTheme(); //主题设置相关

    public abstract void loadDataLocal(); //从本地获取数据(线程中处理)

    public abstract void loadDataNet(); //从网络获取数据(线程中处理)

    public abstract void setView(); //内容显示到界面

    public abstract void updateView(); //更新数据到界面

    public abstract void saveDataLocal(); //保存数据到本地(线程中处理)

    //重写此方法可以对收到的消息进行事件处理
    protected void messageReceived(Message message) {
        if (message.what == TYPE_SET_VIEW) {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "SetView");
            }
            SETVIEW();
        } else if (message.what == TYPE_UPDATE_VIEW) {
            if (!BaseApplication.isRelease) {
                KLog.i(TAG, "UpdateView");
            }
            UPDATEVIEW();
        }
    }
}
