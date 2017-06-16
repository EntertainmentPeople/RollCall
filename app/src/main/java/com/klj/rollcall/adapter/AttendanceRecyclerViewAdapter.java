package com.klj.rollcall.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.adapter.BaseViewHolder;
import com.klj.rollcall.R;
import com.klj.rollcall.bean.AttendanceBean;
import com.klj.rollcall.bean.AttendanceType;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 娱乐人物 on 2017/5/13.
 */

public class AttendanceRecyclerViewAdapter extends BaseRecyclerAdapter<AttendanceType> {
    public AttendanceRecyclerViewAdapter(RecyclerView view, List<AttendanceType> list, int itemLayoutId) {
        super(view, list, itemLayoutId);
    }

    @Override
    public void bindConvert(BaseViewHolder holder, int position, AttendanceType attendanceType, final boolean isScrolling) {
        if(isScrolling){
            holder.itemView.setTag(position);
            AttendanceBean attendanceBean = attendanceType.getAttendanceBean();
            ImageView ivPortrait = holder.getView(R.id.iv_attend_student_portrait);
            if (!TextUtils.isEmpty(attendanceBean.getsPortrait())) {
                Picasso.with(cxt).load(UrlUtil.ROOT_PATH + attendanceBean.getsPortrait()).placeholder(R.mipmap.icon_portrait).into(ivPortrait);
            }
            holder.setText(R.id.tv_attend_student_sno,attendanceBean.getsName());
            holder.setText(R.id.tv_attend_student_dept,attendanceBean.getsDname());
            holder.setText(R.id.tv_attend_student_major,attendanceBean.getsMname());
            holder.setText(R.id.tv_attend_student_sex,attendanceBean.getsSex());
            holder.setText(R.id.tv_attend_student_sno,attendanceBean.getsNo());
            holder.setText(R.id.tv_attend_total,"总数："+attendanceType.getaTotal());
            holder.setText(R.id.tv_attend_normal,"正常："+attendanceType.getaNormal());
            holder.setText(R.id.tv_attend_late,"迟到："+attendanceType.getaLate());
            holder.setText(R.id.tv_attend_leave_early,"早退："+attendanceType.getaLeaveEarly());
            holder.setText(R.id.tv_attend_leave,"请假："+attendanceType.getaLeave());
            holder.setText(R.id.tv_attend_absenteeism,"旷课："+attendanceType.getaAssentisson());
        }
    }

    @Override
    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        super.setOnItemViewClickListener(listener);
    }
}
