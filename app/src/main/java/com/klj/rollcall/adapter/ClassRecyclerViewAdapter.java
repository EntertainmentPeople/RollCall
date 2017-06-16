package com.klj.rollcall.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.klj.adapter.BaseRecyclerAdapter;
import com.klj.adapter.BaseViewHolder;
import com.klj.rollcall.R;
import com.klj.rollcall.bean.ClassBean;

import java.util.List;

/**
 * Created by 娱乐人物 on 2017/5/13.
 */

public class ClassRecyclerViewAdapter extends BaseRecyclerAdapter<ClassBean> {

    public ClassRecyclerViewAdapter(RecyclerView view, List<ClassBean> list, int itemLayoutId) {
        super(view, list, itemLayoutId);
    }

    @Override
    public void bindConvert(BaseViewHolder holder, int position, ClassBean classBean, boolean isScrolling) {
        if(isScrolling){
            holder.itemView.setTag(position);
            holder.setText(R.id.tv_class_className,classBean.getClassName());
            if (!TextUtils.isEmpty(classBean.gettName())){
                holder.setText(R.id.tv_class_studentNum,classBean.gettName());
            }else {
                holder.setText(R.id.tv_class_studentNum, classBean.getStudentNum() + "");
            }
        }
    }

}
