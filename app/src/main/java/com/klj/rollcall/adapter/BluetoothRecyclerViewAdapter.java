package com.klj.rollcall.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.adapter.BaseViewHolder;
import com.klj.rollcall.R;
import com.klj.rollcall.bean.AttendanceBean;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.utils.UrlUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 娱乐人物 on 2017/5/13.
 */

public class BluetoothRecyclerViewAdapter extends BaseRecyclerAdapter<AttendanceBean> {

    private SparseArray<String> attendanceType;

    public BluetoothRecyclerViewAdapter(RecyclerView view, List<AttendanceBean> list, int itemLayoutId) {
        super(view, list, itemLayoutId);
        attendanceType = new SparseArray<>();
        for (int i = 0; i < list.size(); i++) {
            attendanceType.put(i, list.get(i).getaType());
        }
    }

    @Override
    public void bindConvert(BaseViewHolder holder, int position, AttendanceBean attendanceBean, final boolean isScrolling) {
        if (isScrolling) {
            holder.setText(R.id.tv_bluetooth_student_name,attendanceBean.getsName());
            holder.setText(R.id.tv_bluetooth_student_sno, attendanceBean.getsNo());
            setAttendType(holder, attendanceBean.getaType());
            RadioGroup rgBluetooth = holder.getView(R.id.rg_bluetooth);
            rgBluetooth.setOnCheckedChangeListener(new MyOnCheckedChangeListener(position));
        }
    }

    private void setAttendType(BaseViewHolder holder, String s) {
        RadioButton view = null;
        switch (s) {
            case "正常":
                view = holder.getView(R.id.rb_bluetooth_normal);
                view.setChecked(true);
                break;
            case "迟到":
                view = holder.getView(R.id.rb_bluetooth_late);
                view.setChecked(true);
                break;
            case "早退":
                view = holder.getView(R.id.rb_bluetooth_leave_early);
                view.setChecked(true);
                break;
            case "请假":
                view = holder.getView(R.id.rb_bluetooth_leave);
                view.setChecked(true);
                break;
            case "旷课":
            default:
                view = holder.getView(R.id.rb_bluetooth_absenteeism);
                view.setChecked(true);
                break;
        }
    }

    public SparseArray<String> getAttendanceType() {
        return attendanceType;
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        private int position = 0;

        public MyOnCheckedChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_bluetooth_normal:
                    attendanceType.put(position, "正常");
                    break;
                case R.id.rb_bluetooth_late:
                    attendanceType.put(position, "迟到");
                    break;
                case R.id.rb_bluetooth_leave_early:
                    attendanceType.put(position, "早退");
                    break;
                case R.id.rb_bluetooth_leave:
                    attendanceType.put(position, "请假");
                    break;
                case R.id.rb_bluetooth_absenteeism:
                    attendanceType.put(position, "旷课");
                    break;
            }
        }
    }
}
