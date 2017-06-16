package com.klj.rollcall.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.klj.rollcall.R;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.bean.TeacherBean;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.ToastUtils;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.klj.rollcall.view.CircleImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MineActivity extends BaseActivity {
    @BindView(R.id.tv_signin_title)
    TextView tvSigninTitle;
    @BindView(R.id.iv_mineinfo_portrait)
    CircleImageView ivMineinfoPortrait;
    @BindView(R.id.et_mine_userId)
    EditText etMineUserId;
    @BindView(R.id.et_mine_name)
    EditText etMineName;
    @BindView(R.id.rb_mine_man)
    RadioButton rbMineMan;
    @BindView(R.id.rb_mine_woman)
    RadioButton rbMineWoman;
    @BindView(R.id.rg_mine_sex)
    RadioGroup rgMineSex;
    @BindView(R.id.et_mine_age)
    EditText etMineAge;
    @BindView(R.id.et_mine_dept)
    EditText etMineDept;
    @BindView(R.id.et_mine_phone)
    EditText etMinePhone;
    @BindView(R.id.btn_mine_update)
    Button btnMineUpdate;
    @BindView(R.id.et_mine_grade)
    EditText etMineGrade;
    @BindView(R.id.et_mine_major)
    EditText etMineMajor;
    @BindView(R.id.ll_mine_grade)
    LinearLayout llMineGrade;
    @BindView(R.id.ll_mine_major)
    LinearLayout llMineMajor;

    private String userRole = "";
    private String userId = "";
    private TeacherBean teacherBean = null;
    private StudentBean studentBean = null;
    private Map<String, String> map = null;
    private Uri uri = null;
    private String info = "";
    JSONObject jsonObject = null;
    private String portraitPath = "";

    @Override
    protected void init() {
        userRole = (String) Utils.getSPFile(MineActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        userId = (String) Utils.getSPFile(MineActivity.this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        initData();
    }

    private void initData() {
        if (!TextUtils.isEmpty(userRole) && ConstantUtil.STUDENT.equals(userRole)) {
            studentBean = Utils.getStudentInfo(MineActivity.this);
            setStudent(studentBean);
        }
        if (!TextUtils.isEmpty(userRole) && ConstantUtil.TEACHER.equals(userRole)) {
            teacherBean = Utils.getTeacherInfo(MineActivity.this);
            llMineGrade.setVisibility(View.GONE);
            llMineMajor.setVisibility(View.GONE);
            setTeacher(teacherBean);
        }

    }

    private void setStudent(StudentBean studentBean) {
        if (!TextUtils.isEmpty(studentBean.getsPortrait())) {
            Picasso.with(this).load(UrlUtil.ROOT_PATH + studentBean.getsPortrait()).placeholder(R.mipmap.icon_portrait).into(ivMineinfoPortrait);
        }
        etMineUserId.setText(studentBean.getsNo());
        etMineName.setText(studentBean.getsName());
        if ("男".equals(studentBean.getsSex())) {
            rbMineMan.setChecked(true);
        }
        if ("女".equals(studentBean.getsSex())) {
            rbMineWoman.setChecked(true);
        }
        etMineAge.setText(studentBean.getsAge() + "");
        etMineGrade.setText(studentBean.getsGrade() + "");
        etMineMajor.setText(studentBean.getmName());
        etMineDept.setText(studentBean.getdName());
        etMinePhone.setText(studentBean.getsPhone());
    }

    private void setTeacher(TeacherBean teacherBean) {
        if (!TextUtils.isEmpty(teacherBean.gettPortrait())) {
            Picasso.with(this).load(UrlUtil.ROOT_PATH + teacherBean.gettPortrait()).placeholder(R.mipmap.icon_portrait).into(ivMineinfoPortrait);
        }
        etMineUserId.setText(teacherBean.gettNo());
        etMineName.setText(teacherBean.gettName());
        if ("男".equals(teacherBean.gettSex())) {
            rbMineMan.setChecked(true);
        }
        if ("女".equals(teacherBean.gettSex())) {
            rbMineWoman.setChecked(true);
        }
        etMineAge.setText(teacherBean.gettAge() + "");
        etMineDept.setText(teacherBean.getdName());
        etMinePhone.setText(teacherBean.gettPhone());
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_mine;
    }

    private void updateMineInfo() {
        getInfo();
        portraitPath = getAbsoluteImagePath(uri);
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.UPDATE_MINE_INFO)
                .isMultipart(true)
                .params("userRole", userRole)
                .params("info", info)
                .params("portrait", new File(portraitPath))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (ConstantUtil.STUDENT.equals(userRole)) {
                                getStudentData(jsonObject.optJSONObject("data"));
                            } else {
                                getTeacherData(jsonObject.optJSONObject("data"));
                            }
                            ToastUtils.showToastAtCenterOfScreen(MineActivity.this, jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取学生的信息
     */
    private void getStudentData(JSONObject student) throws JSONException {
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
        Utils.saveStudntInfo(MineActivity.this, studentBean);
    }

    /**
     * 获取教师的信息
     */
    private void getTeacherData(JSONObject teacher) throws JSONException {
        String tNo = teacher.optString("t_no");
        String tName = teacher.optString("t_name");
        String tSex = teacher.optString("t_sex");
        int tAge = teacher.optInt("t_age");
        String dName = teacher.optString("t_dname");
        String tPhone = teacher.optString("t_phone");
        String tPortrait = teacher.optString("t_portrait");
        teacherBean = new TeacherBean(tNo, tName, tSex, tAge, dName, tPhone, tPortrait);
        Utils.saveTeacherInfo(MineActivity.this, teacherBean);
    }

    /**
     * 获取学生的信息
     */
    private void saveInfo() {
        String userId = jsonObject.optString("userId");
        String name = jsonObject.optString("name");
        String sex = jsonObject.optString("sex");
        int age = jsonObject.optInt("age");
        String dName = jsonObject.optString("dept");
        String phone = jsonObject.optString("phone");
        String portrait = portraitPath;
        if (ConstantUtil.STUDENT.equals(userRole)) {
            String mName = jsonObject.optString("major");
            int sGrade = jsonObject.optInt("grade");
            studentBean = new StudentBean(userId, name, sex, age, mName, sGrade, dName, phone, portrait);
            Utils.saveStudntInfo(MineActivity.this, studentBean);
        } else {
            teacherBean = new TeacherBean(userId, name, sex, age, dName, phone, portrait);
            Utils.saveTeacherInfo(MineActivity.this, teacherBean);
        }
    }

    private void getInfo() {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("userId", getText(etMineUserId));
            jsonObject.put("name", getText(etMineName));
            jsonObject.put("age", getText(etMineAge));
            if (rbMineWoman.isChecked()) {
                jsonObject.put("sex", rbMineWoman.getText().toString());
            }
            if (rbMineMan.isChecked()) {
                jsonObject.put("sex", rbMineMan.getText().toString());
            }
            if (!TextUtils.isEmpty(userRole) && ConstantUtil.STUDENT.equals(userRole)) {
                jsonObject.put("grade", getText(etMineGrade));
                jsonObject.put("major", getText(etMineMajor));
            }
            jsonObject.put("dept", getText(etMineDept));
            jsonObject.put("phone", getText(etMinePhone));

            info = String.valueOf(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void destroy() {

    }

    @OnClick({R.id.btn_mine_update, R.id.iv_mineinfo_portrait, R.id.ib_mine_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_mine_update:
                updateMineInfo();
                break;
            case R.id.iv_mineinfo_portrait:
                choosePic();

                break;
            case R.id.ib_mine_back:
                finish();
                break;
        }
    }

    private void choosePic() {
        Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
        /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ivMineinfoPortrait.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 通过uri获取文件的绝对路径
     *
     * @param uri
     * @return
     */
    private String getAbsoluteImagePath(Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    imagePath = cursor.getString(column_index);
                }
            }
        }
        return imagePath;
    }

    /**
     * 获取控件的值
     */
    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

}
