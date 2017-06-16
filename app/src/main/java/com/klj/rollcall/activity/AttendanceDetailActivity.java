package com.klj.rollcall.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.rollcall.R;
import com.klj.rollcall.adapter.AttendanceDetailRecyclerViewAdapter;
import com.klj.rollcall.adapter.AttendanceRecyclerViewAdapter;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.AttendanceBean;
import com.klj.rollcall.sql.DataBaseHelper;
import com.klj.rollcall.utils.ConstantUtil;
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
 * 教师蓝牙点名
 */
public class AttendanceDetailActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemViewClickListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_attend_detail_studentList)
    RecyclerView rvAttendanceStudentList;
    @BindView(R.id.srl_attend_detail_refresh)
    SwipeRefreshLayout srlAttendanceRefresh;
    @BindView(R.id.ib_attend_detail_back)
    ImageButton tbAttendBack;
    private String userId;
    private String className;
    private List<AttendanceBean> attendanceList;
    private AttendanceBean attendanceBean;
    private DataBaseHelper db;
    private LinearLayoutManager mLayoutManager;
    private AttendanceDetailRecyclerViewAdapter myRecyclerViewAdapter;
    private int lastItem;
    private final int totalPage = 36;  //设置总共的数据
    private int pageNum = 1;
    private View footView;
    private String userRole;
    private String sno;

    @Override
    protected void init() {
        userId = (String) Utils.getSPFile(this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        userRole = (String) Utils.getSPFile(this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        className = getIntent().getStringExtra(ConstantUtil.CLASSNAME);
        sno = getIntent().getStringExtra(ConstantUtil.SNO);
        loadData();
    }

    private void loadData() {
        attendanceList = new ArrayList<>();
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.GET_ATTENDANCE_DETAIL_RECODE)
                .params("userRole", userRole)
                .params("userId", userId)
                .params("className", className)
                .params("sno", sno)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                getAttendanceData(jsonObject);
                                initRecyclerView();
                            }
                            Utils.showMessage(AttendanceDetailActivity.this, jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Utils.showMessage(AttendanceDetailActivity.this, "获取数据失败！");
                    }
                });
    }

    private void getAttendanceData(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject attendance = data.getJSONObject(i);
            String sNo = attendance.optString("s_no");
            String sName = attendance.optString("s_name");
            String sSex = attendance.optString("s_sex");
            int sAge = attendance.optInt("s_age");
            String mName = attendance.optString("s_mname");
            int sGrade = attendance.optInt("s_grade");
            String dName = attendance.optString("s_dname");
            String sPortrait = attendance.optString("s_portrait");
            long aTime = attendance.optLong("a_time");
            String aType = attendance.optString("a_type");
            attendanceBean = new AttendanceBean(sNo, sName, sSex, sPortrait, dName, mName, sGrade, aTime, aType);
            attendanceList.add(attendanceBean);
        }
    }

    private void initRecyclerView() {
        initSwipeRefresh();
        mLayoutManager = new LinearLayoutManager(this);
        rvAttendanceStudentList.setLayoutManager(mLayoutManager);
        rvAttendanceStudentList.setHasFixedSize(true);
        //初始化，一直显示第一条数据。
        //getList(1);
        myRecyclerViewAdapter = new AttendanceDetailRecyclerViewAdapter(rvAttendanceStudentList, attendanceList, R.layout.item_attendance_detail_recodes);
        //在设置adapter之前要先获取list集合。
        rvAttendanceStudentList.setAdapter(myRecyclerViewAdapter);
        srlAttendanceRefresh.setRefreshing(false);
        footView = LayoutInflater.from(this).inflate(R.layout.recycler_foot, rvAttendanceStudentList, false);
        View headView = LayoutInflater.from(this).inflate(R.layout.recycler_header, rvAttendanceStudentList, false);
        setRecyclerScrollListener();
        //myRecyclerViewAdapter.setOnItemViewClickListener(this);
    }

    private void setRecyclerScrollListener() {
        rvAttendanceStudentList.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        srlAttendanceRefresh.setOnRefreshListener(this);
        //第一次进入页面时，显示加载进度条
        srlAttendanceRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        srlAttendanceRefresh.setRefreshing(true);  //开始进入界面，显示刷新条

    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemViewClick(View view, int position) {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_attendance_detail;
    }

    @Override
    protected void destroy() {

    }


    @OnClick(R.id.ib_attend_detail_back)
    public void onViewClicked() {
        finish();
    }
}
