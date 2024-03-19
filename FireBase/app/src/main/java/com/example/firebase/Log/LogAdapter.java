package com.example.firebase.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firebase.R;

import java.util.ArrayList;

public class LogAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<LogSK> arrayList;
    private ArrayList<LogSK> getBackUp;

    public LogAdapter(Context context, int layout, ArrayList<LogSK> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
        this.getBackUp=new ArrayList<LogSK>();
        this.getBackUp.addAll(arrayList);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvTrangThai = (TextView) convertView.findViewById(R.id.tvTrangThai);


        LogSK log = arrayList.get(position);
        tvDate.setText(log.getMyDate());
        tvTime.setText(log.getMyTime());
        tvTrangThai.setText(log.getMyTrangThai());
        return convertView;
    }
    public void filterList(ArrayList<LogSK> filterList){
        arrayList=filterList;
        notifyDataSetChanged();
    }

    public ArrayList<LogSK>getGetBackUp(){
        return getBackUp;
    }

}
