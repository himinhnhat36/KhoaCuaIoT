package com.example.firebase.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBaseLog extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="SuKienLogg";
    private static final String TABLE_NAME ="Nhat";
    private static final String ID ="ID";
    private static final String MyDate ="MyDate";
    private static final String MyTime ="MyTime";
    private static final String TrangThai ="TrangThai";

    private Context context;
    DatabaseReference mReference;

    public DataBaseLog(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID +" integer primary key  , "+
                MyDate + " TEXT, "+
                MyTime + " TEXT, "+
                TrangThai +" TEXT)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void addLog(LogSK log){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDate, log.getMyDate());
        values.put(MyTime, log.getMyTime());
        values.put(TrangThai, log.getMyTrangThai());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }


    public void QueryData(String sql){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL(sql);
    }


    public List<LogSK> getAll() {
        List<LogSK> listLog = new ArrayList<LogSK>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME  ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LogSK log = new LogSK();
                log.setMalog(cursor.getInt(0));
                log.setMyDate(cursor.getString(1));
                log.setMyTime(cursor.getString(2));
                log.setMyTrangThai(cursor.getString(3));
                listLog.add(log);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listLog;
    }

    public boolean logExists(String date, String time, String description) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Truy vấn để kiểm tra xem mục nhật ký đã tồn tại chưa
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + MyDate + " = ?" +
                    " AND " + MyTime + " = ?" +
                    " AND " + TrangThai + " = ?";
            cursor = db.rawQuery(query, new String[]{date, time, description});

            // Kiểm tra xem con trỏ có bất kỳ hàng nào hay không, cho thấy mục nhật ký đã tồn tại
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void deleteByIdLogSK(String id) {
        // Lấy ID duy nhất của dữ liệu trong Firebase
        DatabaseReference rfidRef = FirebaseDatabase.getInstance().getReference("LogSK");
        rfidRef.orderByChild("myTime").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Lấy ID duy nhất của mục Firebase
                    String firebaseId = childSnapshot.getKey();

                    // Xóa dữ liệu từ Firebase bằng ID duy nhất
                    rfidRef.child(firebaseId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(context, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xóa dữ liệu từ SQLite
        SQLiteDatabase db = this.getWritableDatabase();
        String where = MyTime + " = '" + id + "'"; // Đặt giá trị trong dấu ngoặc đơn
        db.delete(TABLE_NAME, where, null);
        db.close();
    }
    public void deleteAllData() {
        // Xóa dữ liệu từ Firebase trong nút LogSK
        DatabaseReference rfidRef = FirebaseDatabase.getInstance().getReference("LogSK");
        rfidRef.removeValue();

        // Xóa dữ liệu từ SQLite
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
