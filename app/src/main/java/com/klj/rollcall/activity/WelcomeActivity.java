package com.klj.rollcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.klj.rollcall.R;
import com.klj.rollcall.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void init() {
        initData();
    }

    /**
     * 初始化
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    gotoMain();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void destroy() {

    }

    /**
     * 跳转到主界面
     */
    private void gotoMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
