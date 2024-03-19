package com.example.firebase.RFID;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firebase.Log.LogSK;
import com.example.firebase.R;

import java.util.ArrayList;

public class RFIDAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<RFID> arrayList;
    private ArrayList<RFID> getBackUp;

    public RFIDAdapter(Context context, int layout, ArrayList<RFID> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
        this.getBackUp=new ArrayList<RFID>();
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

        TextView tvIDRFID = (TextView) convertView.findViewById(R.id.tvIDRFID);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tvUserRFID);
        TextView tvEmailRFID = (TextView) convertView.findViewById(R.id.tvEmailRFID);


        RFID rfid = arrayList.get(position);
        tvIDRFID.setText(rfid.getMyIDRFID());
        tvUser.setText(rfid.getMyUserRFID());
        tvEmailRFID.setText(rfid.getMyEmailRFID());
        return convertView;
    }

    public void filterList(ArrayList<RFID> filterList){
        arrayList=filterList;
        notifyDataSetChanged();
    }
    public ArrayList<RFID>getGetBackUp(){
        return getBackUp;
    }
}
