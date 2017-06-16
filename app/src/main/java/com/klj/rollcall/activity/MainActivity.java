package com.klj.rollcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.klj.rollcall.R;
import com.klj.rollcall.adapter.MyViewPagerAdapter;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.FragmentUtil;
import com.klj.rollcall.utils.Utils;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.rb_main_syllabus)
    RadioButton rbMainSyllabus;
    @BindView(R.id.rb_main_sign_in)
    RadioButton rbMainSignIn;
    @BindView(R.id.rb_main_mine)
    RadioButton rbMainMine;
    @BindView(R.id.rg_main_menu)
    RadioGroup rgMainMenu;
    public static MainActivity mineActivity=null;
    private boolean isLogin;

    @Override
    protected void init() {
        if (TextUtils.isEmpty((String) Utils.getSPFile(MainActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, ""))) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setText();
            initData();
            setAdapter();
            setListener();
        }
        mineActivity=this;
        isLogin=getIntent().getBooleanExtra(ConstantUtil.IS_LOGIN,false);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    private void setText() {
        if (ConstantUtil.STUDENT.equals(Utils.getSPFile(MainActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, ""))) {
            rbMainSignIn.setText("签到");
        } else {
            rbMainSignIn.setText("点名");
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String userRole = getIntent().getStringExtra(ConstantUtil.LOGIN_USER_ROLE);
        if (ConstantUtil.STUDENT.equals(userRole)) {

        } else if (ConstantUtil.TEACHER.equals(userRole)) {

        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        rgMainMenu.setOnCheckedChangeListener(new MyRadioGroupChangeListener());
        vpMain.addOnPageChangeListener(new MyViewPagerChangeListener());
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        vpMain.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), FragmentUtil.getFragments()));
    }

    public boolean isLogin() {
        return isLogin;
    }

    /**
     * 为ViewPager设置页面改变监听事件
     */
    private class MyViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rgMainMenu.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * 为RadioGroup设置RadioButton选中的监听事件
     */
    private class MyRadioGroupChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_main_syllabus:
                    vpMain.setCurrentItem(0);
                    break;
                case R.id.rb_main_sign_in:
                    vpMain.setCurrentItem(1);
                    break;
                case R.id.rb_main_mine:
                    vpMain.setCurrentItem(2);
                    break;
            }
        }
    }

    long exitTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - exitTime) > 2000) {//比较两次按键时间差
            Utils.showMessage(MainActivity.this, "再点一次退出！");
            exitTime = mNowTime;
        } else {//退出程序
            this.finish();
        }
    }

    @Override
    protected void destroy() {
    }
}
