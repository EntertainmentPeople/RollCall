package com.klj.rollcall.adapter;

import android.support.v7.widget.RecyclerView;
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

public class AttendanceDetailRecyclerViewAdapter extends BaseRecyclerAdapter<AttendanceBean> {
    public AttendanceDetailRecyclerViewAdapter(RecyclerView view, List<AttendanceBean> list, int itemLayoutId) {
        super(view, list, itemLayoutId);
    }

    @Override
    public void bindConvert(BaseViewHolder holder, int position, AttendanceBean attendanceBean, final boolean isScrolling) {
        if(isScrolling){
            ImageView ivPortrait = holder.getView(R.id.iv_attendanceItem_portrait);
            Picasso.with(cxt).load(UrlUtil.ROOT_PATH + attendanceBean.getsPortrait()).placeholder(R.mipmap.icon_portrait).into(ivPortrait);
            holder.setText(R.id.tv_attendanceItem_student_sno,attendanceBean.getsName());
            holder.setText(R.id.tv_attendanceItem_student_dept,attendanceBean.getsDname());
            holder.setText(R.id.tv_attendanceItem_student_major,attendanceBean.getsMname());
            holder.setText(R.id.tv_attendanceItem_student_sex,attendanceBean.getsSex());
            holder.setText(R.id.tv_attendanceItem_student_sno,attendanceBean.getsNo());
            holder.setText(R.id.item_attendanceItem_attendace_time, Utils.toTransferTime(attendanceBean.getaTime()+""));
            holder.setText(R.id.item_attendanceItem_attendace_type,attendanceBean.getaType());
        }
    }

    @Override
    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        super.setOnItemViewClickListener(listener);
    }
}
