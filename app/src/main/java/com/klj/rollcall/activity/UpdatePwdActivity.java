package com.klj.rollcall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.klj.rollcall.R;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class UpdatePwdActivity extends BaseActivity {

    @BindView(R.id.et_updatepwd_oldpwd)
    EditText etUpdatepwdOldpwd;
    @BindView(R.id.et_updatepwd_newpwd)
    EditText etUpdatepwdNewpwd;
    @BindView(R.id.et_updatepwd_repwd)
    EditText etUpdatepwdRepwd;
    @BindView(R.id.btn_updatepwd_updatepwd)
    Button btnUpdatepwdUpdatepwd;
    private String userId;
    private String oldPwd;
    private String newPwd;
    private String rePwd;
    private String userRole;

    @Override
    protected void init() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void destroy() {
    }

    @OnClick({R.id.btn_updatepwd_updatepwd, R.id.ib_updatePwd_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_updatepwd_updatepwd:
                judgePwd();
                break;
            case R.id.ib_updatePwd_back:
                finish();
                break;
        }

    }

    private void judgePwd() {
        userId = (String) Utils.getSPFile(UpdatePwdActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        userRole = (String) Utils.getSPFile(UpdatePwdActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        oldPwd = etUpdatepwdOldpwd.getText().toString();
        newPwd = etUpdatepwdNewpwd.getText().toString();
        rePwd = etUpdatepwdRepwd.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            Utils.showMessage(UpdatePwdActivity.this, "原密码不能为空，请输入原密码！");
            etUpdatepwdOldpwd.requestFocus();
        } else if (TextUtils.isEmpty(newPwd)) {
            Utils.showMessage(UpdatePwdActivity.this, "新密码不能为空，请输入新密码！");
            etUpdatepwdNewpwd.requestFocus();
        } else if (TextUtils.isEmpty(rePwd)) {
            Utils.showMessage(UpdatePwdActivity.this, "重复密码不能为空，请输入重复密码！");
            etUpdatepwdRepwd.requestFocus();
        } else if (!newPwd.equals(rePwd)) {
            Utils.showMessage(UpdatePwdActivity.this, "两次输入密码不一致");
            etUpdatepwdNewpwd.setText("");
            etUpdatepwdRepwd.setText("");
            etUpdatepwdNewpwd.requestFocus();
        } else {
            updatePwd();
        }
    }

    private void updatePwd() {
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.UPDATE_PWD)
                .params("userRole", userRole)
                .params("userId", userId)
                .params("oldPwd", oldPwd)
                .params("newPwd", newPwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                Intent intent = new Intent(UpdatePwdActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                MainActivity.mineActivity.finish();
                            }
                            Utils.showMessage(UpdatePwdActivity.this, jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Utils.showMessage(UpdatePwdActivity.this, "连接服务器失败！");
                    }
                });
    }

}
