package com.klj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 娱乐人物 on 2017/5/18.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener {
    private static final String TAG = "BaseRecyclerAdapter--->";
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOT = 2;
    private boolean isScrolling = true;  //false表示滑动，true表示不滑动
    private View headerView,footerView;  //头和尾的view
    private List<T> lists;   //传入的集合。
    private int totalList;
    private int itemLayoutId; //设置Layout的id。
    public Context cxt;
    private OnItemViewClickListener onItemViewClickListener;

    @Override
    public void onClick(View v) {
        if(null!=onItemViewClickListener){
            onItemViewClickListener.onItemViewClick(v,(int)v.getTag());
        }
    }

    public interface OnItemViewClickListener{
        //设置item中某个view的点击事件
        void onItemViewClick(View view, int position);
    }

    /**
     * 设置点击监听
     * @param listener
     */
    public void setOnItemViewClickListener(OnItemViewClickListener listener){
        this.onItemViewClickListener = listener;
    }

    /**
     * 设置值
     * @param data
     */
    public void setData(List<T> data) {
        addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 获取值
     * @return
     */
    public List<T> getData() {
        return lists;
    }
    /*public BaseRecyclerAdapter(){
        this.lists=new ArrayList<>();
    }*/

    public BaseRecyclerAdapter(RecyclerView view, List<T> list, int itemLayoutId){
        this.lists = list;
        this.itemLayoutId = itemLayoutId;
        this.cxt = view.getContext();
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //设置如果Recycler停止滑动，去绑定更新数据。
                //将上面代码改写成这样。
                isScrolling = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if(isScrolling){ //为true时候表示停止滑动操作，去更新数据
                    notifyDataSetChanged();
                }
                super.onScrollStateChanged(recyclerView,newState);
            }
        });
    }
    public void setFooterView(View view){
        this.footerView = view;
    }
    public void setHeaderView(View view){
        this.headerView = view;
    }

    /**
     * 创建RecyclerView里面的itemView。
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEAD && headerView !=null){
            return new BaseViewHolder(headerView);
        }
        if(viewType == TYPE_FOOT && footerView!=null){
            return new BaseViewHolder(footerView);
        }
        View view = LayoutInflater.from(cxt).inflate(itemLayoutId,parent,false);
        BaseViewHolder holder = new BaseViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    /**
     *  绑定每一个itemView的具体数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(getItemViewType(position) != TYPE_ITEM){
            //如果不是正常的Item，就不去绑定数据
            return;
        }
        //抽象方法，让子类去具体的实现
        int p = getRealPosition(position);
        bindConvert(holder,p,lists.get(p),isScrolling);
    }
    private int getRealPosition(int position){
        return headerView ==null ? position : position-1;
    }
    public abstract void bindConvert(BaseViewHolder holder, int position, T item, boolean isScrolling);

    /**
     * 判断有多少个list.size()
     * @return
     */
    @Override
    public int getItemCount() {
        if(lists == null){
            return 0;
        }
        if(headerView!=null && footerView!=null){
            //头尾都不为空
            totalList =  lists.size()+2;
        }else if(headerView ==null && footerView ==null){
            //头尾都为空
            totalList = lists.size();
        }else{
            //头尾有一个不为空
            totalList = lists.size()+1;
        }
        return totalList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && headerView !=null){
            //表示第一个item，并且headerView不为空
            return TYPE_HEAD;
        }
        if(position+1 == getItemCount() && footerView !=null){
            //表示最好一个item，并且footerView不为空
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    /**
     * 添加一个数据
     * @param data
     */
    public void add(T data){
        lists.add(data);
        int index = lists.indexOf(data);
        notifyItemChanged(index);
    }
    /**
     * 在指定位置添加一个数据
     * @param data
     */
    public void add(int index,T data){
        lists.add(index,data);
        notifyItemChanged(index);
    }

    /**
     * 移除一个数据
     * @param data
     */
    public void remove(T data){
        int indexOfData = lists.indexOf(data);
        remove(indexOfData);
    }
    /**
     * 移除某一个数据
     * @param index
     */
    public void remove(int index){
        lists.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * 移除某一些数据
     * @param start
     * @param count
     */
    public void remove(int start,int count){
        if((start +count) > lists.size()){
            return;
        }
        int size = getItemCount();
        for(int i =start;i<size;i++){
            lists.remove(i);
        }
        notifyItemRangeRemoved(start,count);
    }

    /**
     * 添加一些数据
     * @param datas
     */
    public void addAll(List<T> datas){
        if(datas == null || datas.size() == 0){
            return;
        }
        lists.addAll(datas);
        notifyItemRangeChanged(lists.size()-datas.size(),lists.size());
    }

    /**
     * 插入一段数据
     * @param index
     * @param datas
     */
    public void addAll(int index,List<T> datas){
        if(datas == null || datas.size() == 0){
            return;
        }
        lists.addAll(index,datas);
        notifyItemRangeChanged(index,index+datas.size());
    }

    /**
     * 清空数据
     */
    public void clear(){
        lists.clear();
        notifyDataSetChanged();
    }

}
