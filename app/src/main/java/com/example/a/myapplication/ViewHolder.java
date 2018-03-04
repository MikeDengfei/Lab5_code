package com.example.a.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by a on 2017/10/19.
 */

public class ViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;//存储子view
    private View mConvertView;//存储list_view
    public ViewHolder(Context context, View itemView, ViewGroup parent)
    {
        super(itemView);
        mConvertView=itemView;
        mViews=new SparseArray<View>();
    }
    public static ViewHolder get(Context context,ViewGroup parent, int layoutID)
    {
        //通过layoutID号得到我们定义的列表项 list_item.xml
        View itemView= LayoutInflater.from(context).inflate(layoutID,parent,false);
        //创建ViewHolder实例
        ViewHolder holder=new ViewHolder(context,itemView,parent);
        return holder;
    }
    public <T extends  View>T getView(int viewId)
    {
        View view=mViews.get(viewId);
        if(view==null)
        {
            view=mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }
}