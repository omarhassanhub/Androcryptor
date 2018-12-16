package com.myproject.androcryptor.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int dAccDatabaseVersion = 1;
    private static final String dAccDatabaseName = "cloud_contacts";
    private static final String dAccTableLogin = "login";
    private static final String dAccKeyID = "id";
    private static final String dAccKeyFirstName  = "fname";
    private static final String dAccKeyLastName  = "lname";
    private static final String dAccKeyEmail  = "email";
    private static final String dAccKeyUsername  = "uname";
    private static final String dAccKeyUid = "uid";
    private static final String dAccKeyCreatedAt = "created_at";

    public DatabaseHandler(Context context) {
        super(context, dAccDatabaseName, null, dAccDatabaseVersion);
    }
    //Create table login.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + dAccTableLogin + "("
                + dAccKeyID + " INTEGER PRIMARY KEY,"
                + dAccKeyFirstName + " TEXT,"
                + dAccKeyLastName + " TEXT,"
                + dAccKeyEmail + " TEXT UNIQUE,"
                + dAccKeyUsername + " TEXT,"
                + dAccKeyUid + " TEXT,"
                + dAccKeyCreatedAt + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + dAccTableLogin);

        onCreate(db);
    }

    //Add user to the database. 
    public void dAccAddUser(String fname, String lname, String email, String uname, String uid, String created_at) {
        SQLiteDatabase dAccDatabase = this.getWritableDatabase();

        ContentValues dAccValues = new ContentValues();
        dAccValues.put(dAccKeyFirstName, fname); 
        dAccValues.put(dAccKeyLastName, lname); 
        dAccValues.put(dAccKeyEmail, email); 
        dAccValues.put(dAccKeyUsername, uname); 
        dAccValues.put(dAccKeyUid, uid); 
        dAccValues.put(dAccKeyCreatedAt, created_at); 

        dAccDatabase.insert(dAccTableLogin, null, dAccValues);
        dAccDatabase.close(); 
    }

    //Select from the database table.
    public HashMap<String, String> dAccGetUserDetails(){
        HashMap<String,String> dAccUser = new HashMap<String,String>();
        String dAccSelectQuery = "SELECT  * FROM " + dAccTableLogin;

        SQLiteDatabase dAccDatabase = this.getReadableDatabase();
        Cursor dAccCursor = dAccDatabase.rawQuery(dAccSelectQuery, null);

        dAccCursor.moveToFirst();
        if(dAccCursor.getCount() > 0){
            dAccUser.put("fname", dAccCursor.getString(1));
            dAccUser.put("lname", dAccCursor.getString(2));
            dAccUser.put("email", dAccCursor.getString(3));
            dAccUser.put("uname", dAccCursor.getString(4));
            dAccUser.put("uid", dAccCursor.getString(5));
            dAccUser.put("created_at", dAccCursor.getString(6));
        }
        dAccCursor.close();
        dAccDatabase.close();

        return dAccUser;
    }

    
    public int dAccGetRowCount() {
        String dAccCountQuery = "SELECT  * FROM " + dAccTableLogin;
        SQLiteDatabase dAccDatabase = this.getReadableDatabase();
        Cursor dAccCursor = dAccDatabase.rawQuery(dAccCountQuery, null);
        int dAccRowCount = dAccCursor.getCount();
        dAccDatabase.close();
        dAccCursor.close();

        return dAccRowCount;
    }


    public void dAccResetTables(){
        SQLiteDatabase dAccDatabase = this.getWritableDatabase();

        dAccDatabase.delete(dAccTableLogin, null, null);
        dAccDatabase.close();
    }

}
