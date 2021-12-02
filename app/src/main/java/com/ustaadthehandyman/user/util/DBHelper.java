package com.inhank.yankems.staff.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jogi Prasad Pakki on 16-May-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2019 All reserved by Jogi Prasad Pakki.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "UstaadServices";
    private static final String  TABLE_USER_ADDRESS = "UserAddress";
    private static final String KEY_ID = "Id";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";
    private static final String KEY_PERSON_NAME = "PersonName";
    private static final String KEY_PERSON_MOBILE = "PersonMobile";
    private static final String KEY_PERSON_HOUSE = "PersonHouse";
    private static final String KEY_PERSON_STREET = "PersonStreet";
    private static final String KEY_PERSON_ADDRESS = "PersonAddress";
    public DBHelper(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_ADDRESS_TABLE = "CREATE TABLE "+TABLE_USER_ADDRESS+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"+KEY_PERSON_NAME+" TEXT,"+KEY_PERSON_MOBILE+" TEXT,"+KEY_PERSON_HOUSE+" TEXT,"+KEY_PERSON_STREET+" TEXT,"+ KEY_PERSON_ADDRESS +" TEXT,"+KEY_LATITUDE+" REAL, "+KEY_LONGITUDE+" REAL )";
        sqLiteDatabase.execSQL(CREATE_ADDRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_ADDRESS);
        onCreate(sqLiteDatabase);
    }

    public long putAddress(DBAddress userLocations){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_PERSON_NAME,userLocations.getName());
        contentValues.put(KEY_PERSON_MOBILE,userLocations.getMobile());
        contentValues.put(KEY_PERSON_HOUSE,userLocations.getHouse());
        contentValues.put(KEY_PERSON_STREET,userLocations.getStrret());
        contentValues.put(KEY_LATITUDE,userLocations.getLatitude());
        contentValues.put(KEY_LONGITUDE,userLocations.getLongitude());
        contentValues.put(KEY_PERSON_ADDRESS,userLocations.getAddress());

        long id = database.insert(TABLE_USER_ADDRESS,null,contentValues);
        database.close();
        return id;
    }

    public DBAddress getAddress(int id){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_USER_ADDRESS+" WHERE "+KEY_ID+ " = " + id;
        Cursor cursor = database.rawQuery(query,null);
                //database.query(TABLE_USER_ADDRESS,new String[]{KEY_ID,KEY_PERSON_NAME,KEY_PERSON_MOBILE,KEY_PERSON_HOUSE,KEY_PERSON_STREET,KEY_PERSON_ADDRESS,KEY_LATITUDE,KEY_LONGITUDE},KEY_ID+" =? ",new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();
        database.close();
        return new DBAddress(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_PERSON_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_PERSON_MOBILE)),
                cursor.getString(cursor.getColumnIndex(KEY_PERSON_HOUSE)),
                cursor.getString(cursor.getColumnIndex(KEY_PERSON_STREET)),
                cursor.getString(cursor.getColumnIndex(KEY_PERSON_ADDRESS)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
    }


    public List<DBAddress> getAllAddress(){
        List<DBAddress> addressList = new ArrayList<DBAddress>();
        String query = "SELECT * FROM "+ TABLE_USER_ADDRESS +" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                DBAddress address = new DBAddress();
                address.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                address.setName(cursor.getString(cursor.getColumnIndex(KEY_PERSON_NAME)));
                address.setMobile(cursor.getString(cursor.getColumnIndex(KEY_PERSON_MOBILE)));
                address.setHouse(cursor.getString(cursor.getColumnIndex(KEY_PERSON_HOUSE)));
                address.setStrret(cursor.getString(cursor.getColumnIndex(KEY_PERSON_STREET)));
                address.setAddress(cursor.getString(cursor.getColumnIndex(KEY_PERSON_ADDRESS)));
                address.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
                address.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
                addressList.add(address);
                Log.d("PK",cursor.getString(cursor.getColumnIndex(KEY_ID)));
            }while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return addressList;
    }

    public void deleteAddress(DBAddress serviceAddress){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_USER_ADDRESS,KEY_ID+" =?",new String[]{String.valueOf(serviceAddress.getId())});
        database.close();
    }
}
