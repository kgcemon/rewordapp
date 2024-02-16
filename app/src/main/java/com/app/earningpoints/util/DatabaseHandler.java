package com.app.earningpoints.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.app.earningpoints.R;
import com.app.earningpoints.Responsemodel.ConfigResp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_EXT = ".db";
    private static final String TABLE_HOME = "home";
    private static final String KEY_AUTO_ID = "id";
    private static final String ID = "id";
    private static final String ICON = "icon";
    private static final String OFFER_TITLE = "offer_title";
    private static final String OFFER_TYPE = "type";
    private static final String OFFER_ICON = "offer_icon";

    public DatabaseHandler(Context context) {
        super(context, context.getString(R.string.app_name).replace(" ", "") + DATABASE_EXT, null, DATABASE_VERSION);
        getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_HOME = "CREATE TABLE " + TABLE_HOME + "("
                + KEY_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OFFER_TYPE + " TEXT ,"
                + OFFER_TITLE + " TEXT ,"
                + OFFER_ICON + "  TEXT " + ")";


        db.execSQL(CREATE_TABLE_HOME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME);
        onCreate(db);
    }

    public void insert(List<ConfigResp.OfferItem> home) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0; i<home.size(); i++){
            ContentValues values = new ContentValues();
            values.put(OFFER_TYPE,home.get(i).getType());
            values.put(OFFER_TITLE,home.get(i).getOfferTitle());
            values.put(OFFER_ICON,home.get(i).getOffer_icon());
            db.insert(TABLE_HOME, null, values);
        }

        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<ConfigResp.OfferItem> getHomeList() {
        ArrayList<ConfigResp.OfferItem> modelDBArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_HOME ;
        @SuppressLint("Recycle") Cursor cur = db.rawQuery(query, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    @SuppressLint("Range") int MainID = cur.getInt(cur.getColumnIndex(KEY_AUTO_ID));
                    @SuppressLint("Range") ConfigResp.OfferItem home = new ConfigResp.OfferItem();
                    home.setOffer_icon(cur.getString(cur.getColumnIndex(OFFER_ICON)));
                    home.setOfferTitle(cur.getString(cur.getColumnIndex(OFFER_TITLE)));
                    home.setType(cur.getString(cur.getColumnIndex(OFFER_TYPE)));
                    modelDBArrayList.add(home);
                } while (cur.moveToNext());
            }
        }
        return modelDBArrayList;
    }


    public void removeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM home"); //delete all rows in a table
        db.close();
    }

}
