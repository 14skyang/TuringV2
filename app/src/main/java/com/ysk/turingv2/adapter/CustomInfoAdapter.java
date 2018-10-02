package com.ysk.turingv2.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ysk.turingv2.R;
import com.ysk.turingv2.bean.Custom;

import java.util.List;

/*
**展示用户自定义话术的页面的适配器
 */
public class CustomInfoAdapter extends BaseAdapter {
    private List<Custom>list;
    //构造函数
    public CustomInfoAdapter(List<Custom>list){
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Custom custom=list.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allcustom_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.question = (TextView) view.findViewById(R.id.custom_question);
            viewHolder.answer = (TextView) view.findViewById(R.id.custom_answer);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.question.setText(custom.getSendtext());//注意要先实例话Custom类的对象后才能用get/set方法
        viewHolder.answer.setText(custom.getReceivetext());
        return view;
    }


    //自定义ViewHolder
    private class ViewHolder{
        TextView question;
        TextView answer;
    }
}
