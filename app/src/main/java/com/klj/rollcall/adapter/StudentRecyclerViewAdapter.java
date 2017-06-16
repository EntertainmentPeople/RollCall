package com.klj.rollcall.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.adapter.BaseViewHolder;
import com.klj.rollcall.R;
import com.klj.rollcall.bean.ClassBean;
import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.utils.UrlUtil;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 娱乐人物 on 2017/5/13.
 */

public class StudentRecyclerViewAdapter extends BaseRecyclerAdapter<StudentBean> {

    private SparseArray<String> attendanceType;

    public StudentRecyclerViewAdapter(RecyclerView view, List<StudentBean> list, int itemLayoutId) {
        super(view, list, itemLayoutId);
        attendanceType=new SparseArray<>();
        for (int i=0;i<list.size();i++){
            attendanceType.put(i,"");
        }
    }

    @Override
    public void bindConvert(BaseViewHolder holder, int position, StudentBean studentBean, final boolean isScrolling) {
        if(isScrolling){
            ImageView ivPortrait = holder.getView(R.id.iv_rule_student_portrait);
            Picasso.with(cxt).load(UrlUtil.ROOT_PATH + studentBean.getsPortrait()).placeholder(R.mipmap.icon_portrait).into(ivPortrait);
            holder.setText(R.id.tv_rule_student_name,studentBean.getsName());
            holder.setText(R.id.tv_rule_student_dept,studentBean.getdName());
            holder.setText(R.id.tv_rule_student_major,studentBean.getmName());
            holder.setText(R.id.tv_rule_student_sex,studentBean.getsSex());
            holder.setText(R.id.tv_rule_student_sno,studentBean.getsNo());
            RadioGroup rgRule = holder.getView(R.id.rg_rule);
            rgRule.setOnCheckedChangeListener(new MyOnCheckedChangeListener(position));
        }
    }
    public SparseArray<String> getAttendanceType() {
        return attendanceType;
    }
    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        private int position=0;
        public MyOnCheckedChangeListener(int position) {
            this.position=position;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_rule_normal:
                    attendanceType.put(position,"正常");
                    break;
                case R.id.rb_rule_late:
                    attendanceType.put(position,"迟到");
                    break;
                case R.id.rb_rule_leave_early:
                    attendanceType.put(position,"早退");
                    break;
                case R.id.rb_rule_leave:
                    attendanceType.put(position,"请假");
                    break;
                case R.id.rb_rule_absenteeism:
                    attendanceType.put(position,"旷课");
                    break;
            }
        }
    }
}
