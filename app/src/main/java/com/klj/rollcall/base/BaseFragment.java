package com.klj.rollcall.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.simple.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 娱乐人物 on 2017/5/20.
 */

public abstract class BaseFragment extends Fragment {
    public abstract int getContentViewId();

    protected Context context;
    protected View mRootView;
    Unbinder unbinder;
    public String[] requiredPermissions={};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentViewId(), container, false);
        unbinder = ButterKnife.bind(this, mRootView);//绑定framgent
        this.context = getActivity();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    protected abstract void init();

    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : requiredPermissions) {
                int permissionGranted = ContextCompat.checkSelfPermission(getActivity(), permission);
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    startRequestPermission();
                }
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(getActivity(), requiredPermissions, 0);
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
                int permissionGranted = ContextCompat.checkSelfPermission(getActivity(), permission);
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    showDialogTipUserGoToAppSetting();
                }
            }
        }
    }

    private void showDialogTipUserGoToAppSetting() {
        new AlertDialog.Builder(getActivity())
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
                        getActivity().finish();
                    }
                }).setCancelable(false).show();
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
