package com.klj.rollcall.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.klj.rollcall.R;
import com.klj.rollcall.adapter.BluetoothRecyclerViewAdapter;
import com.klj.rollcall.base.BaseActivity;
import com.klj.rollcall.bean.AttendanceBean;
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
 * 教师蓝牙点名
 */
public class BluetoothActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.btn_blutooth_commit)
    Button btnBlutoothCommit;
    @BindView(R.id.rv_bluetooth_student)
    RecyclerView rvBluetoothStudent;
    @BindView(R.id.srl_bluetooth_refresh)
    SwipeRefreshLayout srlBluetoothRefresh;
    @BindView(R.id.btn_bluetooth_scan)
    Button btnBluetoothScan;
    private String userId;
    private String className;
    private List<AttendanceBean> attendList;
    private StudentBean studentBean;
    private DataBaseHelper db;
    private LinearLayoutManager mLayoutManager;
    private BluetoothRecyclerViewAdapter myRecyclerViewAdapter;
    private int lastItem;
    private final int totalPage = 36;  //设置总共的数据
    private int pageNum = 1;
    private View footView;
    private String userRole;
    private BluetoothAdapter adapter = null;
    private BluetoothSocket mSocket = null;
    private List<BluetoothDevice> mDevices;
    private int weekday;
    private int start;
    private String type;
    private boolean isSetMac = false;

    @Override
    protected void init() {
        requiredPermissions = new String[]{Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        checkPermission();
        userId = (String) Utils.getSPFile(this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ID, "");
        userRole = (String) Utils.getSPFile(this, ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        className = getIntent().getStringExtra(ConstantUtil.CLASSNAME);
        weekday = getIntent().getIntExtra(ConstantUtil.WEEKDAY, 0);
        start = getIntent().getIntExtra(ConstantUtil.START_COURSE, 0);
        mDevices = new ArrayList<>();
        attendList = new ArrayList<>();
        db = new DataBaseHelper(BluetoothActivity.this, userRole);
        isSetMac = (boolean) Utils.getSPFile(BluetoothActivity.this, ConstantUtil.IET_MAC, ConstantUtil.IS_SET_MAC, false);
        initBluetooth();
        registerReceiver();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_bluetooth;
    }

    private void initBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            Log.e("------", "本机有蓝牙设备！");
            if (!adapter.isEnabled()) {
                adapter.enable();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        } else {
            ToastUtils.showToastAtCenterOfScreen(BluetoothActivity.this, "该手机没有蓝牙设备");
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @OnClick({R.id.btn_blutooth_commit, R.id.btn_bluetooth_scan, R.id.ib_bluebooth_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_blutooth_commit:
                commit();
                break;
            case R.id.btn_bluetooth_scan:
                scanBluetooth();
                break;
            case R.id.ib_bluebooth_back:
                finish();
                break;
        }
    }

    private void scanBluetooth() {
        if (TextUtils.isEmpty(IsAttendaceTime.getAttendType(weekday, start, IsAttendaceTime.getCurrntTime())) || ConstantUtil.IS_NOT_ATTENDAN_TIME.equals(type)) {
            ToastUtils.showToastAtCenterOfScreen(BluetoothActivity.this, ConstantUtil.IS_NOT_ATTENDAN_TIME);
        } else {
            showLoading();
            adapter.startDiscovery();
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
                jsonObject.put("sno", attendList.get(i).getsNo());
                jsonObject.put("time", attendList.get(i).getaTime());
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
                            if (!isSetMac) {
                                ContentValues values = null;
                                for (int i = 0; i < mDevices.size(); i++) {
                                    values = new ContentValues();
                                    JSONObject jsonObject=new JSONObject(mDevices.get(i).getName());
                                    values.put("sno", jsonObject.optString("sno"));
                                    values.put("className", className);
                                    values.put("mac", mDevices.get(i).getAddress());
                                    db.insert(ConstantUtil.TABLE_MAC, values);
                                }
                                Utils.saveSPFile(BluetoothActivity.this, ConstantUtil.IET_MAC, ConstantUtil.IS_SET_MAC, true);
                            }
                            ToastUtils.showToastAtCenterOfScreen(BluetoothActivity.this, js.optString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                long attendTime = IsAttendaceTime.getCurrntTime();
                if ((null == mDevices && mDevices.size() <= 0) || !isExist(mDevices, device.getAddress())) {
                    mDevices.add(device);
                    type = IsAttendaceTime.getAttendType(weekday, start, attendTime);
                    showDevice(device, attendTime, type);
                }
                Log.e("-----", "find device:" + device.getName());
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                hideLoading();
                srlBluetoothRefresh.setRefreshing(false);  //开始进入界面，显示刷新条
                ToastUtils.showToastAtCenterOfScreen(mContext, "搜索完成");
            }
        }
    };

    private boolean isExist(List<BluetoothDevice> mDevices, String address) {
        for (int i = 0; i < mDevices.size(); i++) {
            BluetoothDevice bluetoothDevice = mDevices.get(i);
            if (bluetoothDevice.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    private void showDevice(BluetoothDevice mDevices, long attendTime, String type) {
        parseJsonObject(mDevices, attendTime, type);
        initSwipeRefresh();
        mLayoutManager = new LinearLayoutManager(this);
        rvBluetoothStudent.setLayoutManager(mLayoutManager);
        rvBluetoothStudent.setHasFixedSize(true);
        myRecyclerViewAdapter = new BluetoothRecyclerViewAdapter(rvBluetoothStudent, attendList, R.layout.item_bluetooth_student);
        rvBluetoothStudent.setAdapter(myRecyclerViewAdapter);
        srlBluetoothRefresh.setRefreshing(false);
        footView = LayoutInflater.from(this).inflate(R.layout.recycler_foot, rvBluetoothStudent, false);
        View headView = LayoutInflater.from(this).inflate(R.layout.recycler_header, rvBluetoothStudent, false);
        setRecyclerScrollListener();
//        myRecyclerViewAdapter.setOnItemViewClickListener(this);
    }

    private void parseJsonObject(BluetoothDevice mDevices, long attendTime, String type) {
        AttendanceBean attend = new AttendanceBean();
        String json = mDevices.getName();
        JSONObject jsonObject = null;
        JsonElement parse = new JsonParser().parse(json);
        try {
            if (parse.isJsonObject()) {
                jsonObject = new JSONObject(json);
                String sNo = jsonObject.optString("sno");
                attend.setsNo(sNo);
                attend.setsName(jsonObject.optString("sname"));
                Cursor cursor = null;
                if (isSetMac) {
                    cursor = db.query(ConstantUtil.TABLE_MAC, "sno=? and className=? and mac=?", new String[]{sNo, className, mDevices.getAddress()}, null);
                }
                if (null != cursor && cursor.getCount() <= 0) {
                    attend.setaType("旷课，不是本人");
                } else {
                    attend.setaType(type);
                }
                attend.setaTime(attendTime);
                attendList.add(attend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerScrollListener() {
        rvBluetoothStudent.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (lastItem + 1 == myRecyclerViewAdapter.getItemCount() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //最后一个item并且还有下滑的趋势
                    if (lastItem + 1 <= totalPage) {
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
        srlBluetoothRefresh.setOnRefreshListener(this);
        //第一次进入页面时，显示加载进度条
        srlBluetoothRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        srlBluetoothRefresh.setRefreshing(true);  //开始进入界面，显示刷新条
    }


    @Override
    protected void destroy() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onRefresh() {
        attendList.clear();
        scanBluetooth();
    }
}
