package com.klj.rollcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.klj.rollcall.R;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.CourseBean;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.bean.TeacherBean;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.klj.rollcall.view.CircleImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.iv_login_portrait)
    CircleImageView ivLoginPortrait;
    @BindView(R.id.et_login_userId)
    EditText etLoginUserId;
    @BindView(R.id.et_login_pwd)
    EditText etLoginPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String userId;
    private String userPwd;
    private StudentBean studentBean = null;
    private TeacherBean teacherBean = null;
    private CourseBean courseBean = null;
    private List<CourseBean> courseList = new ArrayList<>();

    @Override
    protected void init() {
        loadData();
    }

    /**
     * 获取登录信息
     */
    private void loadData() {
        userId = (String) Utils.getSPFile(LoginActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        userPwd = (String) Utils.getSPFile(LoginActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_PWD, "");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userPwd)) {
            etLoginUserId.setText(userId);
            etLoginPwd.setText(userPwd);
        }
    }

    @OnClick({R.id.iv_login_portrait, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_login_portrait:
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        userId = etLoginUserId.getText().toString();
        userPwd = etLoginPwd.getText().toString();
        if (TextUtils.isEmpty(userId)) {
            etLoginUserId.requestFocus();
            Utils.showMessage(LoginActivity.this, "账号不能为空！");
        } else if (TextUtils.isEmpty(userPwd)) {
            etLoginPwd.requestFocus();
            Utils.showMessage(LoginActivity.this, "密码不能为空！");
        } else {
            showLoading();
            OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.Login_PATH)
                    .connTimeOut(5000)
                    .readTimeOut(5000)
                    .params("userId", userId)
                    .params("userPwd", userPwd)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                hideLoading();
                                JSONObject jsonObject = new JSONObject(s);
                                int result = jsonObject.optInt("result");
                                String userRole = jsonObject.optString("userRole");
                                if (result == 1) {
                                    Utils.saveSPFile(LoginActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, userId);
                                    Utils.saveSPFile(LoginActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_PWD, userPwd);
                                    Utils.saveSPFile(LoginActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, userRole);
                                    if (ConstantUtil.STUDENT.equals(userRole)) {
                                        getStudentData(jsonObject);
                                    } else if (ConstantUtil.TEACHER.equals(userRole)) {
                                        getTeacherData(jsonObject);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "信息错误", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra(ConstantUtil.IS_LOGIN,true);
                                    startActivity(intent);
                                    finish();
                                }
                                Utils.showMessage(LoginActivity.this, jsonObject.optString("msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            hideLoading();
                            Utils.showMessage(LoginActivity.this, "连接服务器失败！");
                        }
                    });
        }
    }

    /**
     * 获取学生的信息
     *
     * @param jsonObject
     */
    private void getStudentData(JSONObject jsonObject) throws JSONException {
        JSONObject student = jsonObject.getJSONObject("student");
        String sNo = student.optString("s_no");
        String sName = student.optString("s_name");
        String sSex = student.optString("s_sex");
        int sAge = student.optInt("s_age");
        String mName = student.optString("s_mname");
        int sGrade = student.optInt("s_grade");
        String dName = student.optString("s_dname");
        String sPhone = student.optString("s_phone");
        String sPortrait = student.optString("s_portrait");
        studentBean = new StudentBean(sNo, sName, sSex, sAge, mName, sGrade, dName, sPhone, sPortrait);
        Utils.saveStudntInfo(LoginActivity.this, studentBean);
    }

    /**
     * 获取教师的信息
     *
     * @param jsonObject
     */
    private void getTeacherData(JSONObject jsonObject) throws JSONException {
        JSONObject teacher = jsonObject.getJSONObject("teacher");
        String tNo = teacher.optString("t_no");
        String tName = teacher.optString("t_name");
        String tSex = teacher.optString("t_sex");
        int tAge = teacher.optInt("t_age");
        String dName = teacher.optString("t_dname");
        String tPhone = teacher.optString("t_phone");
        String tPortrait = teacher.optString("t_portrait");
        teacherBean = new TeacherBean(tNo, tName, tSex, tAge, dName, tPhone, tPortrait);
        Utils.saveTeacherInfo(LoginActivity.this, teacherBean);
    }

    long exitTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - exitTime) > 2000) {//比较两次按键时间差
            Utils.showMessage(LoginActivity.this, "再点一次退出！");
            exitTime = mNowTime;
        } else {//退出程序
            this.finish();
        }
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void destroy() {

    }
}
