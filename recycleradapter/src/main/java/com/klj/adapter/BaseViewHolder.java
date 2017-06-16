package com.klj.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

import butterknife.ButterKnife;

/**
 * 自定义ViewHolder
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        AutoUtils.autoSize(itemView);
        ButterKnife.bind(this, itemView);
    }
    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置TextView的内容
     *
     * @param itemId
     * @param text
     */
    public void setText(int itemId, String text) {
        TextView tv = getView(itemId);
        tv.setText(text);
    }

    /**
     * 设置图片
     *
     * @param itemId
     * @param imageId
     */
    public void setBitmapImage(int itemId, int imageId) {
        ImageView iv = getView(itemId);
        iv.setImageResource(imageId);
    }

}
