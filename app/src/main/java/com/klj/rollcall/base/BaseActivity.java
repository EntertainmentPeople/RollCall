package com.klj.rollcall.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.klj.rollcall.R;
import com.klj.rollcall.utils.ToastUtils;
import com.klj.rollcall.view.LoadingDialogFragment;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zhy.autolayout.utils.AutoLayoutHelper;

import org.simple.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 娱乐人物 on 2017/5/18.
 */

public abstract class BaseActivity extends AutoLayoutActivity {
    protected Context mContext;
    protected BaseActivity mActivity;
    private LoadingDialogFragment mLoadingDialog;
    protected String[] requiredPermissions={};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimaryDark));
        AutoLayoutConifg.getInstance().init(this);
        ButterKnife.bind(this);
        mContext = this;
        mActivity = this;
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    protected abstract void init();

    protected abstract int getLayoutResID();

    public boolean checkNetworkAvailable() {
        hideLoading();
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network != null && network.isConnected()) {
            return true;
        } else {
            ToastUtils.showToastAtCenterOfScreen(mContext, "网络不可用,请检查网络设置");
            return false;
        }
    }

    /**
     * showEmpty 方法用于请求的数据为空的状态
     */
    public void showEmpty() {
        ToastUtils.showToastAtCenterOfScreen(mContext, R.string.empty_no_data);
    }

    public void showError(String msg) {
        ToastUtils.showToastAtCenterOfScreen(mContext, msg);
    }

    public void showLoading() {
        showLoading(R.string.msg_loading);
    }

    public void showLoading(int resId) {
        showLoading(mContext.getResources().getString(resId));
    }

    public void showLoading(String msg) {
        hideLoading();
        mLoadingDialog = LoadingDialogFragment.newInstance(msg);
        mLoadingDialog.show(((BaseActivity) mContext).getSupportFragmentManager(), "Loading");
    }

    public void hideLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : requiredPermissions) {
                int permissionGranted = ContextCompat.checkSelfPermission(this, permission);
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    startRequestPermission();
                }
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, requiredPermissions, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldManuallyOpenPermission();
            }
        }
    }

    private void shouldManuallyOpenPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : requiredPermissions) {
                int permissionGranted = ContextCompat.checkSelfPermission(this, permission);
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    showDialogTipUserGoToAppSetting();
                }
            }
        }
    }

    private void showDialogTipUserGoToAppSetting() {
        new AlertDialog.Builder(this)
                .setTitle("请设置权限")
                .setMessage("为了保证APP全部功能的正常使用,请允许我们使用全部权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }

    protected abstract void destroy();
}
