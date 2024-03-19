package com.example.firebase.TemporaryPassword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firebase.RFID.RFID;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTemporary extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Temporary Password";
    private static final String TABLE_NAME = "Nhat300622";
    private static final String ID = "ID";
    private static final String MyUser = "MyUser";
    private static final String MyOpenPass = "MyOpenPass";
    private static final String MyClosePass = "MyClosePass";
    private static final String MyDate = "MyDate";
    private static final String MyTime = "MyTime";

    private Context context;
    DatabaseReference reference;

    public DatabaseTemporary(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " integer primary key  , " +
                MyUser + " TEXT, " +
                MyOpenPass + " interger, " +
                MyClosePass + " interger, " +
                MyDate + " TEXT, " +
                MyTime + " TEXT)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTempo(Tempo tempo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyUser, tempo.getUserTempo());
        values.put(MyOpenPass, tempo.getOpenPassTempo());
        values.put(MyClosePass, tempo.getClosePassTemPo());
        values.put(MyDate, tempo.getDateTempo());
        values.put(MyTime, tempo.getTimeTempo());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Tempo> getAll() {
        List<Tempo> tempos = new ArrayList<Tempo>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Tempo tempo = new Tempo();
                tempo.setTempo(cursor.getInt(0));
                tempo.setUserTempo(cursor.getString(1));
                tempo.setOpenPassTempo(cursor.getInt(2));
                tempo.setClosePassTemPo(cursor.getInt(3));
                tempo.setDateTempo(cursor.getString(4));
                tempo.setTimeTempo(cursor.getString(5));
                tempos.add(tempo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tempos;
    }

    public void deleteByIDTempo(String id) {
        // Lấy ID duy nhất của dữ liệu trong Firebase
        DatabaseReference temporaryPasswords = FirebaseDatabase.getInstance().getReference("TemporaryPasswords");
        temporaryPasswords.orderByChild("userTempo").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String firebaseId = childSnapshot.getKey();
                    temporaryPasswords.child(firebaseId).removeValue();
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
        String where = MyUser + " = '" + id + "'"; // Đặt giá trị trong dấu ngoặc đơn
        db.delete(TABLE_NAME, where, null);
        db.close();
    }

    public void deleteItemsNotInFirebase() {
        // Lấy tất cả các mục từ SQLite
        List<Tempo> tempList = getAll();
        // Tham chiếu đến nút Firebase chứa dữ liệu
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("TemporaryPasswords");

        for (Tempo temp : tempList) {
            String userTempo = temp.getUserTempo();
            // Kiểm tra xem mục có tồn tại trên Firebase không
            firebaseRef.orderByChild("userTempo").equalTo(userTempo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // Mục không tồn tại trên Firebase, xóa nó khỏi SQLite
                        deleteFromSQLite(userTempo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra
                    Toast.makeText(context, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteFromSQLite(String username) {
        // Xóa dữ liệu từ SQLite
        SQLiteDatabase db = this.getWritableDatabase();
        String where = MyUser + " = ?";
        String[] whereArgs = {username};
        db.delete(TABLE_NAME, where, whereArgs);
        db.close();
    }
}
