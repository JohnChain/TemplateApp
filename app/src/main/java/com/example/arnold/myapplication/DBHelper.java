package com.example.arnold.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arnold on 2015/7/30.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME ="template.db";//数据库名
    private final static int VERSION = 1;//版本号
    private final static String TABLE_NAME = "TemplateTB"; //表名
    private final static String ITEM = "Item";
    private final static String PRICE = "Price";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //为了每次构造时不用传入dbName和版本号，自己得新定义一个构造方法
    public DBHelper(Context cxt){
        this(cxt, DB_NAME, null, VERSION);//调用上面的构造方法
    }
    //版本变更时
    public DBHelper(Context cxt,int version) {
        this(cxt,DB_NAME,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " ( " + ITEM + " text primary key , " + PRICE + " float);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //查询操作
    // public Cursor query (String table, String[] columns, String selection, String[] selectionArgs,
    //                      String groupBy, String having, String orderBy, String limit)
    public Cursor select(String item) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns ={ITEM, PRICE};
        String selection = "where ITEM = ?";
        String[] whereValue ={item};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, whereValue, null, null, null);
        return cursor;
    }
    public Cursor select_all() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
    //增加操作 
    public long insert(String item,float price)
    {
        SQLiteDatabase db = this.getWritableDatabase(); 
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(ITEM, item);
        cv.put(PRICE, price);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }
    //删除操作 
    public void delete(String item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ITEM + " = ?";
        String[] whereValue ={item};
        db.delete(TABLE_NAME, where, whereValue);
    }
    public void delete_all()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
    //修改操作 
    public void update(String item, float price)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ITEM + " = ?";
        String[] whereValue = { item };

        ContentValues cv = new ContentValues();
        cv.put(PRICE, price);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
    public void close(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.close();
    }
}
