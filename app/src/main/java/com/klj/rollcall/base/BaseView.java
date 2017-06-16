package com.klj.rollcall.base;

import android.content.DialogInterface;

/**
 * Created by 娱乐人物 on 2017/5/19.
 */

public interface BaseView {

    /**
     * showLoading 方法主要用于页面请求数据时显示加载状态
     */
    void showLoading();

    /**
     * showEmpty 方法用于请求的数据为空的状态
     */
    void showEmpty();

    /**
     * showError 方法用于请求数据出错
     */
    void showError();

    void showError(String msg);

    /**
     * showError 方法用于请求数据完成
     */
    void loadingComplete();

}
