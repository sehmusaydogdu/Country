package com.country;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 23.12.2017.
 */

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;//Database Version
    private static final String DATABASE_NAME="ULKELER";//Database Name
    private static final String TABLE_NAME="ULKE";//Table Name

    private static String ID="_id";
    private static String NAME="name";
    private static String CITY="city";
    private static String COUNTRY="country";

    public Database(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +NAME+" TEXT, "
                +CITY+" TEXT, "
                +COUNTRY+" TEXT "+")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void bilgiEkle(Bilgiler bilgiler){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(NAME,bilgiler.getName());
        values.put(CITY,bilgiler.getCity());
        values.put(COUNTRY,bilgiler.getCountry());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public List<Bilgiler> getListAll(){
        List<Bilgiler> bilgilerList=new ArrayList<>();
        String selectQuery="SELECT name,city,country FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();


        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Bilgiler bilgi=new Bilgiler();
                bilgi.setName(cursor.getString(0));
                bilgi.setCity(cursor.getString(1));
                bilgi.setCountry(cursor.getString(2));
                bilgilerList.add(bilgi);
            }
            while (cursor.moveToNext());
        }
        return bilgilerList;
    }

    private void BilgiSil(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,ID+"= ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


    public void getCountID(String country){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{ID,COUNTRY},COUNTRY+"=?",new String[]{country},null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            int _id=cursor.getInt(0);
            BilgiSil(_id);
        }
    }

    public void getNameID(String name){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{ID,NAME},NAME+"=?",new String[]{name},null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            int _id=cursor.getInt(0);
            BilgiSil(_id);
        }
    }

    public void getCityID(String city){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{ID,CITY},CITY+"=?",new String[]{city},null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            int _id=cursor.getInt(0);
            BilgiSil(_id);
        }
    }

}
