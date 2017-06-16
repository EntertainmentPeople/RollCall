package com.klj.rollcall.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.rollcall.R;
import com.klj.rollcall.activity.AttendanceActivity;
import com.klj.rollcall.activity.AttendanceDetailActivity;
import com.klj.rollcall.activity.BluetoothActivity;
import com.klj.rollcall.activity.RuleActivity;
import com.klj.rollcall.activity.ScanActivity;
import com.klj.rollcall.adapter.ClassRecyclerViewAdapter;
import com.klj.rollcall.base.BaseFragment;
import com.klj.rollcall.bean.ClassBean;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.sql.DataBaseHelper;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.IsAttendaceTime;
import com.klj.rollcall.utils.ToastUtils;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemViewClickListener {
    @BindView(R.id.tv_signin_title)
    TextView tvSigninTitle;
    @BindView(R.id.rv_signin_classList)
    RecyclerView rvSigninClassList;
    @BindView(R.id.srl_signin_refresh)
    SwipeRefreshLayout srlSigninRefresh;
    private DataBaseHelper db;
    private LinearLayoutManager mLayoutManager;
    private ClassRecyclerViewAdapter myRecyclerViewAdapter;
    private int lastItem;
    private final int totalPage = 36;  //设置总共的数据
    private int pageNum = 1;
    private View footView;
    private List<ClassBean> classList;
    private String userRole;
    private String userId;
    private String table;
    private String year;
    private String term;
    private String studentClass = "";

    @Override
    public int getContentViewId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    protected void init() {
        setTitle();
        initData();
    }


    private void initRecyclerView() {
        initSwipeRefresh();
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSigninClassList.setLayoutManager(mLayoutManager);
        rvSigninClassList.setHasFixedSize(true);
        //初始化，一直显示第一条数据。
        //getList(1);
        myRecyclerViewAdapter = new ClassRecyclerViewAdapter(rvSigninClassList, classList, R.layout.item_signin_class);
        //在设置adapter之前要先获取list集合。
        rvSigninClassList.setAdapter(myRecyclerViewAdapter);
        srlSigninRefresh.setRefreshing(false);
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_foot, rvSigninClassList, false);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, rvSigninClassList, false);
        setRecyclerScrollListener();
        myRecyclerViewAdapter.setOnItemViewClickListener(this);
    }

    private void setRecyclerScrollListener() {
        rvSigninClassList.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

        srlSigninRefresh.setOnRefreshListener(this);

        //第一次进入页面时，显示加载进度条
        srlSigninRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        srlSigninRefresh.setRefreshing(true);  //开始进入界面，显示刷新条
    }

    /**
     * 初始化数据
     */
    private void initData() {
        classList = new ArrayList<>();
        year = (String) Utils.getSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.YEAR, "");
        term = (String) Utils.getSPFile(getActivity(), ConstantUtil.YEAR_TERM, ConstantUtil.TERM, "");
        db = new DataBaseHelper(getActivity(), ConstantUtil.LOGIN_USER_ROLE);
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
        if (null != cursor && cursor.getCount() > 0) {
            getDataFromSqlite(cursor);
        } else {
            getDataFromInternet(userId, year, term);
        }
        cursor.close();
        initRecyclerView();
    }

    private void getDataFromInternet(String userId, String year, String term) {
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.GET_CLASS_PATH)
                .params("userId", userId)
                .params("year", year)
                .params("term", term)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            ClassBean classBean = null;
                            if (jsonObject.optInt("result") == 1) {
                                JSONArray data = jsonObject.optJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    classBean = new ClassBean();
                                    JSONObject object = data.getJSONObject(i);
                                    classBean.setClassName(object.optString("tc_class"));
                                    if (ConstantUtil.STUDENT.equals(userRole)) {
                                        classBean.settName(object.optString("t_name"));
                                    }
                                    if (ConstantUtil.TEACHER.equals(userRole)) {
                                        classBean.setStudentNum(object.optInt("tc_studentnum"));
                                    }
                                    classList.add(classBean);
                                }
                            }
                            ToastUtils.showToastAtBottomOfScreen(getActivity(), jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 从本地获取数据
     *
     * @param cursor
     */
    private void getDataFromSqlite(Cursor cursor) {
        ClassBean classBean = null;
        while (cursor.moveToNext()) {
            classBean = new ClassBean();
            classBean.setClassName(cursor.getString(cursor.getColumnIndex("c_class")));
            classBean.setStart(cursor.getInt(cursor.getColumnIndex("c_start")));
            classBean.setWeekday(cursor.getInt(cursor.getColumnIndex("c_weekday")));
            if (ConstantUtil.STUDENT.equals(userRole)) {
                classBean.settName(cursor.getString(cursor.getColumnIndex("c_tname")));
            }
            if (ConstantUtil.TEACHER.equals(userRole)) {
                classBean.setStudentNum(cursor.getInt(cursor.getColumnIndex("c_studentnum")));
            }
            classList.add(classBean);
        }
        srlSigninRefresh.setRefreshing(false);
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (ConstantUtil.STUDENT.equals(Utils.getSPFile(getActivity(), ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, ""))) {
            tvSigninTitle.setText("签到");
        } else {
            tvSigninTitle.setText("点名");
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onItemViewClick(final View view, final int position) {
        String[] signins = new String[]{"常规点名", "扫码点名", "蓝牙点名", "考勤查看"};
        if (ConstantUtil.STUDENT.equals(userRole)) {
            signins = new String[]{"扫码签到", "蓝牙签到", "考勤查看"};
        }
        final String[] finalSignins = signins;

        studentClass = classList.get(position).getClassName();
        Utils.signinAlertDialog(getActivity(), "选择点名方式", finalSignins, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0:
                        if (ConstantUtil.STUDENT.equals(userRole)) {
                            requiredPermissions = new String[]{Manifest.permission.CAMERA};
                            checkPermission();
                            intent = new Intent(getActivity(), CaptureActivity.class);
                            startActivityForResult(intent, ConstantUtil.REQUEST_STUDENT_SCAN);
                        } else {
                            intent = new Intent(getActivity(), RuleActivity.class);
                            intent.putExtra(ConstantUtil.CLASSNAME, classList.get(position).getClassName());
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        if (ConstantUtil.STUDENT.equals(userRole)) {
                            requiredPermissions = new String[]{Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION};
                            checkPermission();
                            initBluetooth();
                        } else {
//                            if (IsAttendaceTime.isAttendTime(classList.get(position).getWeekday(), classList.get(position).getStart(), IsAttendaceTime.getCurrntTime())) {
                                intent = new Intent(getActivity(), ScanActivity.class);
                                intent.putExtra(ConstantUtil.CLASSNAME, classList.get(position).getClassName());
                                intent.putExtra(ConstantUtil.START_COURSE, classList.get(position).getStart());
                                intent.putExtra(ConstantUtil.WEEKDAY, classList.get(position).getWeekday());
                                startActivity(intent);
//                            } else {
//                                ToastUtils.showToastAtCenterOfScreen(getActivity(), ConstantUtil.IS_NOT_ATTENDAN_TIME);
//                            }
                        }
                        break;
                    case 2:
                        if (ConstantUtil.STUDENT.equals(userRole)) {
                            intent = new Intent(getActivity(), AttendanceDetailActivity.class);
                            intent.putExtra(ConstantUtil.CLASSNAME, classList.get(position).getClassName());
                            startActivity(intent);
                        } else {
                            intent = new Intent(getActivity(), BluetoothActivity.class);
                            intent.putExtra(ConstantUtil.CLASSNAME, classList.get(position).getClassName());
                            intent.putExtra(ConstantUtil.START_COURSE, classList.get(position).getStart());
                            intent.putExtra(ConstantUtil.WEEKDAY, classList.get(position).getWeekday());
                            startActivity(intent);
                        }
                        break;
                    case 3:
                        intent = new Intent(getActivity(), AttendanceActivity.class);
                        intent.putExtra(ConstantUtil.CLASSNAME, classList.get(position).getClassName());
                        startActivity(intent);
                        break;
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            checkPermission();
        }
        if (requestCode == ConstantUtil.REQUEST_STUDENT_SCAN && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result_string", "utf-8");
            try {
                scanResult = URLDecoder.decode(scanResult, "utf-8");
                JSONObject jsonObject = new JSONObject(scanResult);
                String tno = jsonObject.optString("userId");
                String className = jsonObject.optString("className");
                int start = jsonObject.optInt("start");
                int weekday = jsonObject.optInt("weekday");
                long oldTime = jsonObject.optLong("attendTime");
                if (!TextUtils.isEmpty(studentClass) && studentClass.equals(className)) {
                    getScid(tno,className, userId, weekday, start, oldTime);
                } else {
                    ToastUtils.showToastAtCenterOfScreen(getActivity(), "班级错误");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void getScid(String tno,String className, String sno, final int weekday, final int start, final long oldTime) {
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.GET_SCID)
                .params("tno", tno)
                .params("className", className)
                .params("sno", sno)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            long aTime = IsAttendaceTime.getCurrntTime();
                            if (result == 1) {
                                int scId = jsonObject.optInt("scId");
                                String attendType = IsAttendaceTime.getAttendType(weekday, start, aTime);
//                                if (!TextUtils.isEmpty(attendType) && !attendType.equals(ConstantUtil.IS_NOT_ATTENDAN_TIME)) {
                                    uploadAttendance(scId, aTime, attendType, oldTime);
//                                }
                            }
                            ToastUtils.showToastAtCenterOfScreen(getActivity(), jsonObject.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtils.showToastAtCenterOfScreen(getActivity(), "连接服务器失败");
                    }
                });
    }

    private void uploadAttendance(int scId, long aTime, String attendType, long oldTime) {
        OkGo.post(UrlUtil.ROOT_PATH + UrlUtil.UPDATE_ATTENDANCE)
                .params("scId", scId)
                .params("time", aTime)
                .params("type", attendType)
                .params("oldTime", oldTime)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject js = new JSONObject(s);
                            ToastUtils.showToastAtBottomOfScreen(getActivity(), js.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initBluetooth() {
        boolean isSet = (boolean) Utils.getSPFile(getActivity(), ConstantUtil.SET_BLUETOOTH_NAME, ConstantUtil.IS_SET_BLUETOOTH_NAME, false);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        if (!isSet) {
            if (adapter != null) {
                Log.e("------", "本机有蓝牙设备！");
                if (!adapter.isEnabled()) {
                    adapter.enable();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
                JSONObject jsonObject=new JSONObject();
                StudentBean studentBean = Utils.getStudentInfo(getActivity());
                try {
                    jsonObject.put("sno",studentBean.getsNo());
                    jsonObject.put("sname",studentBean.getsName());
                    adapter.setName(jsonObject.toString());
                    Utils.saveSPFile(getActivity(), ConstantUtil.SET_BLUETOOTH_NAME, ConstantUtil.IS_SET_BLUETOOTH_NAME, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                ToastUtils.showToastAtCenterOfScreen(getActivity(), "该手机没有蓝牙设备");
            }
//        }
        Log.e("=====",adapter.getName());
    }
}
