package com.klj.rollcall.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.rollcall.R;
import com.klj.rollcall.adapter.StudentRecyclerViewAdapter;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.sql.DataBaseHelper;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.IsAttendaceTime;
import com.klj.rollcall.utils.ToastUtils;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 教师常规点名
 */
public class RuleActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemViewClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.btn_rule_commit)
    Button btnRuleCommit;
    @BindView(R.id.rv_rule_studentList)
    RecyclerView rvRuleStudentList;
    @BindView(R.id.srl_rule_refresh)
    SwipeRefreshLayout srlRuleRefresh;
    private String userId;
    private String className;
    private List<StudentBean> studentList;
    private StudentBean studentBean;
    private DataBaseHelper db;
    private LinearLayoutManager mLayoutManager;
    private StudentRecyclerViewAdapter myRecyclerViewAdapter;
    private int lastItem;
    private final int totalPage = 36;  //设置总共的数据
    private int pageNum = 1;
    private View footView;

    @Override
    protected void init() {
        userId = (String) Utils.getSPFile(this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        className = getIntent().getStringExtra(ConstantUtil.CLASSNAME);
        loadData();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_rule;
    }

    @Override
    protected void destroy() {

    }

    private void initRecyclerView() {
        initSwipeRefresh();
        mLayoutManager = new LinearLayoutManager(this);
        rvRuleStudentList.setLayoutManager(mLayoutManager);
        rvRuleStudentList.setHasFixedSize(true);
        myRecyclerViewAdapter = new StudentRecyclerViewAdapter(rvRuleStudentList, studentList, R.layout.item_rule_student);
        //在设置adapter之前要先获取list集合。
        rvRuleStudentList.setAdapter(myRecyclerViewAdapter);
        srlRuleRefresh.setRefreshing(false);
        footView = LayoutInflater.from(this).inflate(R.layout.recycler_foot, rvRuleStudentList, false);
        View headView = LayoutInflater.from(this).inflate(R.layout.recycler_header, rvRuleStudentList, false);
        setRecyclerScrollListener();
//        myRecyclerViewAdapter.setOnItemViewClickListener(this);
    }

    private void setRecyclerScrollListener() {
        rvRuleStudentList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (lastItem + 1 == myRecyclerViewAdapter.getItemCount() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //最后一个item并且还有下滑的趋势
                    if (lastItem + 1 <= totalPage) {
                        //getList(pageNum);
                    } else {
                        TextView loadMoreTv = (TextView) footView.findViewById(R.id.loadMore_tv);
                        ProgressBar loadMorePb = (ProgressBar) footView.findViewById(R.id.loadMore_pb);
                        loadMorePb.setVisibility(View.GONE);
                        loadMoreTv.setText("没有更多数据");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void initSwipeRefresh() {

        srlRuleRefresh.setOnRefreshListener(this);

        //第一次进入页面时，显示加载进度条
        srlRuleRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        srlRuleRefresh.setRefreshing(true);  //开始进入界面，显示刷新条

    }

    /**
     * 初始化数据
     */
    private void loadData() {
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
                                initRecyclerView();
                            }
                            Utils.showMessage(RuleActivity.this, jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Utils.showMessage(RuleActivity.this, "获取数据失败！");
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


    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemViewClick(View view, int position) {
    }

    @OnClick({R.id.btn_rule_commit,R.id.ib_rule_back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_rule_commit:
                commit();
                break;
            case R.id.ib_rule_back:
                finish();
                break;
        }

    }

    private void commit() {
        SparseArray<String> attendanceType = myRecyclerViewAdapter.getAttendanceType();
        JSONArray array = new JSONArray();
        JSONObject jsonObject = null;
        JSONObject data = new JSONObject();
        try {
            for (int i = 0; i < attendanceType.size(); i++) {
                jsonObject = new JSONObject();
                jsonObject.put("sno", studentList.get(i).getsNo());
                jsonObject.put("time", IsAttendaceTime.getCurrntTime());
                jsonObject.put("type", attendanceType.get(i));
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
                            ToastUtils.showToastAtCenterOfScreen(RuleActivity.this, js.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
