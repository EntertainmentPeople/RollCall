package com.klj.rollcall.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.klj.rollcall.R;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.IsAttendaceTime;
import com.klj.rollcall.utils.ToastUtils;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.uuzuche.lib_zxing.encoding.EncodingHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static android.R.attr.data;

/**
 * 教师扫码点名
 */
public class ScanActivity extends BaseActivity {
    @BindView(R.id.iv_teacher_scan_qrcode)
    ImageView ivTeacherScanQrcode;

    private String className = "";
    private String userId;
    private String userRole;
    private int start = 0;
    private int weekday = -1;
    private List<StudentBean> studentList;
    private StudentBean studentBean;
    private long attendTime = 0;

    @Override
    protected void init() {
        userRole = (String) Utils.getSPFile(ScanActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        className = getIntent().getStringExtra(ConstantUtil.CLASSNAME);
        start = getIntent().getIntExtra(ConstantUtil.START_COURSE, 0);
        weekday = getIntent().getIntExtra(ConstantUtil.WEEKDAY, -1);
        userId = (String) Utils.getSPFile(ScanActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        attendTime = IsAttendaceTime.getCurrntTime();
        String msg = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("className", className);
            jsonObject.put("start", start);
            jsonObject.put("weekday", weekday);
            jsonObject.put("attendTime", attendTime);
            msg = URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        createScan(msg);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_scan;
    }

    @Override
    protected void destroy() {
        finish();
    }

    /**
     * 创建二维码
     *
     * @param msg
     */
    private void createScan(String msg) {
        try {
            getSudents();
            Bitmap bitMap = EncodingHandler.createQRCode(msg, 500);
            ivTeacherScanQrcode.setImageBitmap(bitMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void uploadAttendance() {
        JSONArray array = new JSONArray();
        JSONObject jsonObject = null;
        JSONObject data = new JSONObject();
        try {
            for (int i = 0; i < studentList.size(); i++) {
                jsonObject = new JSONObject();
                jsonObject.put("sno", studentList.get(i).getsNo());
                jsonObject.put("time", attendTime);
                jsonObject.put("type", "旷课");
                array.put(jsonObject);
            }
            data.put("tno", userId);
            data.put("className", className);
            data.put("attend", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.UPLOAD_ATTENDANCE)
                .params("uploadAttendance", String.valueOf(data))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            ToastUtils.showToastAtBottomOfScreen(ScanActivity.this, js.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getSudents() {
        studentList = new ArrayList<>();
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.GET_STUDENTLIST_PATH)
                .params("userId", userId)
                .params("className", className)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                getStudentData(jsonObject);
                                uploadAttendance();
                            }
                            Utils.showMessage(ScanActivity.this, jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Utils.showMessage(ScanActivity.this, "获取数据失败！");
                    }
                });
    }

    private void getStudentData(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject student = data.getJSONObject(i);
            String sNo = student.optString("s_no");
            String sName = student.optString("s_name");
            String sSex = student.optString("s_sex");
            int sAge = student.optInt("s_age");
            String mName = student.optString("s_mname");
            int sGrade = student.optInt("s_grade");
            String dName = student.optString("s_dname");
            String sPhone = student.optString("s_phone");
            String sPortrait = student.optString("s_portrait");
            int scId = student.optInt("sc_id");
            studentBean = new StudentBean(sNo, sName, sSex, sAge, mName, sGrade, dName, sPhone, sPortrait, scId);
            studentList.add(studentBean);
        }
    }

    @OnClick(R.id.ib_rule_back)
    public void onViewClicked() {
        finish();
    }
}
