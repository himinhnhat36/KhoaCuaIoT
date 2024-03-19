package com.example.firebase.RFID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebase.Log.LogSK;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRFID extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="SuKienRFID";
    private static final String TABLE_NAME ="SUKIENRFID";
    private static final String ID ="ID";
    private static final String MyIDRFID ="MyIDRFID";
    private static final String MyUserRFID ="MyUserRFID";
    private static final String MyEmailRFID ="myEmailRFID";

    Context context;

    public DatabaseRFID(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID +" integer primary key  , "+
                MyIDRFID + " TEXT, "+
                MyUserRFID + " TEXT, "+
                MyEmailRFID +" TEXT)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void addRFID(RFID rfid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyIDRFID, rfid.getMyIDRFID());
        values.put(MyUserRFID, rfid.getMyUserRFID());
        values.put(MyEmailRFID, rfid.getMyEmailRFID());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public void QueryData(String sql){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL(sql);
    }


    public List<RFID> getAll() {
        List<RFID> listLog = new ArrayList<RFID>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME  ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RFID rfid = new RFID();
                rfid.setMaRFID(cursor.getInt(0));
                rfid.setMyIDRFID(cursor.getString(1));
                rfid.setMyUserRFID(cursor.getString(2));
                rfid.setMyEmailRFID(cursor.getString(3));
                listLog.add(rfid);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listLog;
    }
    public void deleteById(String id) {
        // Lấy ID duy nhất của dữ liệu trong Firebase
        DatabaseReference rfidRef = FirebaseDatabase.getInstance().getReference("Data_RFID");
        rfidRef.orderByChild("MyIDRFID").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
        String where = MyIDRFID + " = '" + id + "'"; // Đặt giá trị trong dấu ngoặc đơn
        db.delete(TABLE_NAME, where, null);
        db.close();
    }

    public boolean isRFIDIdExists(String rfidId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{MyIDRFID}, MyIDRFID + "=?",
                new String[]{rfidId}, null, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void update(RFID updatedRFID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyUserRFID, updatedRFID.getMyUserRFID());
        values.put(MyEmailRFID, updatedRFID.getMyEmailRFID());

        String where = MyIDRFID + " = '" + updatedRFID.getMyIDRFID() + "'";
        db.update(TABLE_NAME, values, where, null);
        db.close();
    }


}
