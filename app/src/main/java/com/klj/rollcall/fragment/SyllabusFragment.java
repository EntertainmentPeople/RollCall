package com.klj.rollcall.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.rollcall.R;
import com.klj.rollcall.activity.MainActivity;
import com.klj.rollcall.bean.CourseBean;
import com.klj.rollcall.listener.SingleChooseListener;
import com.klj.rollcall.sql.DataBaseHelper;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.SPUtils;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 课程表
 */
public class SyllabusFragment extends Fragment implements View.OnClickListener {
    private LinearLayout[] weekPanels;
    private List<List<CourseBean>> courseData;
    private int itemHeight;
    private int marTop, marLeft;
    private List<CourseBean> courseList;
    private int[] colors = null;
    private String[] years;
    private String[] terms;
    private DataBaseHelper db;
    private String userId;
    private String userRole;
    private TextView tvYear;
    private TextView tvTerm;
    private String year;
    private String term;
    private String table;
    private int yearSelection;
    private int termSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_syllabus, container, false);
        findView(view);
        return view;
    }

    /**
     * 找到控件
     *
     * @param view
     */
    private void findView(View view) {
        tvYear = (TextView) view.findViewById(R.id.tv_syllabus_year);
        tvTerm = (TextView) view.findViewById(R.id.tv_syllabus_term);
        weekPanels = new LinearLayout[7];
        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i] = (LinearLayout) view.findViewById(R.id.weekPanel_1 + i);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        itemHeight = getResources().getDimensionPixelSize(R.dimen.weekItemHeight);
        marTop = getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
        marLeft = getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);
        setYearAndTerm();
        setCorlor();
        initData();
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        tvYear.setOnClickListener(this);
        tvTerm.setOnClickListener(this);
    }

    /**
     * 设置学年和学期
     */
    private void setYearAndTerm() {
        terms = new String[]{"第一学期", "第二学期"};
        years = new String[4];
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 4; i++) {
            if (calendar.get(Calendar.MONTH) < 8 || calendar.get(Calendar.MONTH) >= 2) {
                year = (calendar.get(Calendar.YEAR) - i - 1) + "-" + (calendar.get(Calendar.YEAR) - i) + "学年";
                termSelection = 1;
            } else {
                year = (calendar.get(Calendar.YEAR) - i) + "-" + ((calendar.get(Calendar.YEAR) - i) + 1) + "学年";
                termSelection = 0;
            }
            years[i] = year;
        }
        year = (String) Utils.getSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.YEAR, years[0]);
        term = (String) Utils.getSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.TERM, terms[0]);
        Utils.saveSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.YEAR, year);
        Utils.saveSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.TERM, term);
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(year)) {
                yearSelection = i;
            }
        }
        for (int i = 0; i < terms.length; i++) {
            if (terms[i].equals(term)) {
                termSelection = i;
            }
        }
        tvYear.setText(year);
        tvTerm.setText(term);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        courseData = new ArrayList<>();
        courseList = new ArrayList<>();
        userRole = (String) Utils.getSPFile(getActivity(), ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        userId = (String) Utils.getSPFile(getActivity(), ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        if (ConstantUtil.STUDENT.equals(userRole)) {
            table = ConstantUtil.TABLE_SC;
        }
        if (ConstantUtil.TEACHER.equals(userRole)) {
            table = ConstantUtil.TABLE_TC;
        }
        db = new DataBaseHelper(getActivity(), userRole);
        Cursor cursor = db.query(table, "c_year=? and c_term=?", new String[]{year, term}, null);
        if ((null != cursor) && (cursor.getCount() > 0)) {
            getDataFromSqlite(cursor);
        } else {
            getDataFromService();
        }
        cursor.close();
    }

    /**
     * 从本地获取数据
     * @param cursor
     */
    private void getDataFromSqlite(Cursor cursor) {
        CourseBean courseBean = null;
        while (cursor.moveToNext()) {
            courseBean = new CourseBean();
            courseBean.setcNo(cursor.getString(cursor.getColumnIndex("c_cno")));
            courseBean.setcName(cursor.getString(cursor.getColumnIndex("c_cname")));
            courseBean.setTcPlace(cursor.getString(cursor.getColumnIndex("c_place")));
            courseBean.setTcYear(cursor.getString(cursor.getColumnIndex("c_year")));
            courseBean.setTcTerm(cursor.getString(cursor.getColumnIndex("c_term")));
            courseBean.setTcWeek(cursor.getString(cursor.getColumnIndex("c_week")));
            courseBean.setTcWeekday(cursor.getInt(cursor.getColumnIndex("c_weekday")));
            courseBean.setTcStart(cursor.getInt(cursor.getColumnIndex("c_start")));
            courseBean.setTcStep(cursor.getInt(cursor.getColumnIndex("c_step")));
            courseBean.setcScore(cursor.getFloat(cursor.getColumnIndex("c_score")));
            courseBean.setcPeriod(cursor.getInt(cursor.getColumnIndex("c_period")));
            courseBean.setcDname(cursor.getString(cursor.getColumnIndex("c_dname")));
            courseBean.setTcClass(cursor.getString(cursor.getColumnIndex("c_class")));
            if (ConstantUtil.STUDENT.equals(userRole)) {
                courseBean.settName(cursor.getString(cursor.getColumnIndex("c_tname")));
            }
            if (ConstantUtil.TEACHER.equals(userRole)) {
                courseBean.setTcStudentNum(cursor.getInt(cursor.getColumnIndex("c_studentnum")));
            }
            courseList.add(courseBean);
        }
        setCourse();
    }

    /**
     * 设置颜色
     */
    private void setCorlor() {
        colors = new int[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.DKGRAY, Color.MAGENTA};
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    setCourse();
                    break;
            }
        }
    };

    private void setCourse() {
        for (int i = 0; i < 7; i++) {
            List<CourseBean> list = new ArrayList<CourseBean>();
            for (int j = 0; j < courseList.size(); j++) {
                if (courseList.get(j).getTcWeekday() == i + 1) {
                    list.add(courseList.get(j));
                }
            }
            courseData.add(i, list);
        }
        for (int i = 0; i < weekPanels.length; i++) {
            initWeekPanel(weekPanels[i], courseData.get(i));
        }
    }

    /**
     * 从服务器获取数据
     */
    public void getDataFromService() {
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.GET_COURSE_PATH)
                .params("userRole", userRole)
                .params("userId", userId)
                .params("year", year)
                .params("term", term)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                jsonParse(jsonObject);
                            }
                            handler.sendEmptyMessage(100);
                            Toast.makeText(getActivity(), jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 解析数据
     *
     * @param jsonObject
     */
    private void jsonParse(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            courseList.add(getCourseData(data.getJSONObject(i)));
        }
    }

    /**
     * 获取课程相关信息
     *
     * @param jsonObject
     */
    private CourseBean getCourseData(JSONObject jsonObject) {
        String cNo = jsonObject.optString("c_no");
        String cName = jsonObject.optString("c_name");
        String tcPlace = jsonObject.optString("tc_place");
        String tName = jsonObject.optString("t_name");
        String tcYear = jsonObject.optString("tc_year");
        String tcTerm = jsonObject.optString("tc_term");
        String tcWeek = jsonObject.optString("tc_week");
        int tcWeekday = jsonObject.optInt("tc_weekday");
        int tcStart = jsonObject.optInt("tc_start");
        int tcStep = jsonObject.optInt("tc_step");
        float cScore = (float) jsonObject.optDouble("c_score");
        int cPeriod = jsonObject.optInt("c_period");
        String cDname = jsonObject.optString("c_dname");
        String tcClass = jsonObject.optString("tc_class");
        int tcStudentNum = jsonObject.optInt("tc_studentnum");
        insertIntoTable(cNo, cName, tcPlace, tName, tcYear, tcTerm, tcWeek, tcWeekday, tcStart, tcStep, cScore, cPeriod, cDname, tcClass, tcStudentNum);
        return new CourseBean(cNo, cName, tcPlace, tName, tcYear, tcTerm, tcWeek, tcWeekday, tcStart, tcStep, cScore, cPeriod, cDname, tcClass, tcStudentNum);
    }

    private void insertIntoTable(String cNo, String cName, String tcPlace, String tName, String tcYear, String tcTerm, String tcWeek, int tcWeekday, int tcStart, int tcStep, float cScore, int cPeriod, String cDname, String tcClass, int tcStudentNum) {
        ContentValues values = new ContentValues();
        values.put("c_cno", cNo);
        values.put("c_cname", cName);
        values.put("c_place", tcPlace);
        values.put("c_year", tcYear);
        values.put("c_term", tcTerm);
        values.put("c_week", tcWeek);
        values.put("c_weekday", tcWeekday);
        values.put("c_start", tcStart);
        values.put("c_step", tcStep);
        values.put("c_score", cScore);
        values.put("c_period", cPeriod);
        values.put("c_dname", cDname);
        values.put("c_class", tcClass);
        if (ConstantUtil.STUDENT.equals(userRole)) {
            values.put("c_tname", tName);
        }
        if (ConstantUtil.TEACHER.equals(userRole)) {
            values.put("c_studentnum", tcStudentNum);
        }
        db.insert(table, values);
    }

    public void initWeekPanel(LinearLayout ll, List<CourseBean> data) {
        if (ll == null || data == null || data.size() < 1) return;
        Log.i("Msg", "初始化面板");
        CourseBean pre = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            CourseBean c = data.get(i);
            final TextView tv = new TextView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight * c.getTcStep() + marTop * (c.getTcStep() - 1));
            if (i > 0) {
                lp.setMargins(marLeft, (c.getTcStart() - (pre.getTcStart() + pre.getTcStep())) * (itemHeight + marTop) + marTop, 0, 0);
            } else {
                lp.setMargins(marLeft, (c.getTcStart() - 1) * (itemHeight + marTop) + marTop, 0, 0);
            }
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.TOP);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(12);
            tv.setTextColor(getResources().getColor(R.color.courseTextColor));
            tv.setTag(c.getTcClass());
            if (ConstantUtil.STUDENT.equals(userRole)) {
                tv.setText(c.getcName() + "\n" + c.getTcClass() + "\n" + c.gettName() + "\n" + c.getTcPlace() + "\n" + c.getTcWeek());
            }
            if (ConstantUtil.TEACHER.equals(userRole)) {
                tv.setText(c.getcName() + "\n" + c.getTcClass() + "\n" + c.getTcPlace() + "\n" + c.getTcWeek() + "\n" + c.getTcStudentNum() + "人");
            }
            //tv.setBackgroundColor(getResources().getColor(R.color.classIndex));  
            tv.setBackground(getResources().getDrawable(R.drawable.tv_shape));
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = v.getTag();
                }
            });
            ll.addView(tv);
            pre = c;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_syllabus_year:
                Utils.singleChooseAlertDialog(getActivity(), "学年", years, yearSelection, new SingleChooseListener() {
                    @Override
                    public void onChoose(int position) {
                        Utils.saveSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.YEAR, years[position]);
                        yearSelection = position;
                        tvYear.setText(years[position]);
                        initData();
                    }
                });
                break;
            case R.id.tv_syllabus_term:
                Utils.singleChooseAlertDialog(getActivity(), "学期", terms, termSelection, new SingleChooseListener() {
                    @Override
                    public void onChoose(int position) {
                        Utils.saveSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.TERM, terms[position]);
                        termSelection = position;
                        tvTerm.setText(terms[termSelection]);
                        initData();
                    }
                });
                break;
        }

    }
}

