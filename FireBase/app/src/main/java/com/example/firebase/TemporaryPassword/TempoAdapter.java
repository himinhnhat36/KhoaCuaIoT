package com.example.firebase.TemporaryPassword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firebase.Log.LogSK;
import com.example.firebase.R;

import java.util.ArrayList;

public class TempoAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Tempo> arrayList;

    public TempoAdapter(Context context, int layout, ArrayList<Tempo> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
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

        TextView tvUser = convertView.findViewById(R.id.tvUserTempo);
        TextView tvOpenPass = convertView.findViewById(R.id.tvPassTempo);
        TextView tvClosePass = convertView.findViewById(R.id.tvPassTempo1);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        Tempo tempo = arrayList.get(position);
        tvUser.setText(tempo.getUserTempo());
        tvOpenPass.setText(tempo.getOpenPassTempo()+"");
        tvClosePass.setText(tempo.getClosePassTemPo()+"");
        tvDate.setText(tempo.getDateTempo());
        tvTime.setText(tempo.getTimeTempo());
        return convertView;
    }
}
