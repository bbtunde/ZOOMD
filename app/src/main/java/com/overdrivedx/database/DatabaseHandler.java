package com.overdrivedx.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.overdrivedx.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babatundedennis on 6/13/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 11;

    // Database Name
    private static final String DATABASE_NAME = "cwd_zoom_d";
    // Contacts table name
    private static final String TABLE_FEED = "feed";
    private static final String TABLE_USER = "user";

    private static final String USER_NAME = "user_name";

    // Contacts User Columns names
    private static final String ID = "id";
    private static final String ITEM_DETAILS = "item";
    private static final String WEIGHT = "weight";
    private static final String SIZE = "size";

    private static final String RECIPIENT_FIRST_NAME ="recipient_first_name";
    private static final String RECIPIENT_LAST_NAME ="recipient_last_name";
    private static final String RECIPIENT_PHONE ="recipient_phone";
    private static final String RECIPIENT_TOWN ="recipient_town";
    private static final String RECIPIENT_ADDRESS ="recipient_address";

    private static final String SENDER_NAME ="sender_name";
    private static final String SENDER_PHONE ="sender_phone";
    private static final String SENDER_TOWN ="sender_town";
    private static final String SENDER_ADDRESS ="sender_address";

    private static final String PRICE ="price";
    private static final String SELLER_MONEY ="seller_money";
    private static final String SELLER_ID ="seller_id";

    private static final String DATE_POSTED ="date_posted";
    private static final String STATUS ="status";
    private static final String TYPE ="type";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FEED_TABLE = "CREATE TABLE " + TABLE_FEED + "("
                + ID + " VARCHAR PRIMARY KEY," + ITEM_DETAILS + " TEXT,"  + WEIGHT + " TEXT," +  SIZE + " TEXT," +
                RECIPIENT_FIRST_NAME + " TEXT," + RECIPIENT_LAST_NAME + " TEXT," + RECIPIENT_PHONE + " TEXT," + RECIPIENT_TOWN + " TEXT," +
                RECIPIENT_ADDRESS + " TEXT," + SENDER_NAME + " TEXT," + SENDER_PHONE + " TEXT," + SENDER_TOWN + " TEXT," +
                SENDER_ADDRESS + " TEXT," + PRICE + " TEXT," + SELLER_MONEY + " TEXT," + SELLER_ID + " VARCHAR," + DATE_POSTED + " TEXT," + STATUS + " VARCHAR," + TYPE + " VARCHAR"
                +")";
        db.execSQL(CREATE_FEED_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_FEED);

        onCreate(db);

    }

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, item.getID());
        values.put(ITEM_DETAILS, item.getItemDetails());
        values.put(SIZE, item.getSize()); // Contact Name
        values.put(WEIGHT, item.getWeight()); // Contact Name

        values.put(SENDER_NAME, item.getSenderName()); // Contact Name
        values.put(SENDER_PHONE, item.getSenderPhone()); // Contact Phone Number
        values.put(SENDER_TOWN, item.getSenderTown()); // Contact Name
        values.put(SENDER_ADDRESS, item.getSenderAddress()); // Contact Phone Number


        values.put(RECIPIENT_FIRST_NAME, item.getRecipientFirstName()); // Contact Name
        values.put(RECIPIENT_LAST_NAME, item.getRecipientLastName()); // Contact Phone Number
        values.put(RECIPIENT_PHONE, item.getRecipientPhone()); // Contact Phone Number
        values.put(RECIPIENT_TOWN, item.getRecipientTown()); // Contact Name
        values.put(RECIPIENT_ADDRESS, item.getRecipientAddress()); // Contact Phone Number


        values.put(PRICE, item.getPrice()); // Contact Phone Number
        values.put(SELLER_ID, item.getSellerID()); // Contact Phone Number
        values.put(SELLER_MONEY, item.getSellerMoney()); // Contact Phone Number
        values.put(DATE_POSTED, item.getDatePosted()); // Contact Phone Number

        values.put(STATUS, item.getStatus()); // Contact Phone Number
        values.put(TYPE, item.getType());

        // Inserting Row
        db.insert(TABLE_FEED, null, values);
        db.close(); // Closing database connection
    }

    public List<Item> getFeed(String status, String status2) {
        List<Item> clientList = new ArrayList<Item>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEED +
                " WHERE " + STATUS + "=? or " + STATUS + "=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{status, status2});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setID(cursor.getString(0));
                item.setItemDetails(cursor.getString(1));
                item.setWeight(cursor.getString(2));
                item.setSize(cursor.getString(3));
                item.setRecipientFirstName(cursor.getString(4));
                item.setRecipientLastName(cursor.getString(5));
                item.setRecipientPhone(cursor.getString(6));
                item.setRecipientTown(cursor.getString(7));
                item.setRecipientAddress(cursor.getString(8));
                item.setSenderName(cursor.getString(9));
                item.setSenderPhone(cursor.getString(10));
                item.setSenderTown(cursor.getString(11));
                item.setSenderAddress(cursor.getString(12));
                item.setPrice(cursor.getString(13));
                item.setSellerMoney(cursor.getString(14));
                item.setSellerID(cursor.getString(15));
                item.setDatePosted(cursor.getString(16));
                item.setStatus(cursor.getString(17));
                item.setType(cursor.getString(18));
                // Adding contact to list
                clientList.add(item);
            } while (cursor.moveToNext());
        }

        // return contact list
        return clientList;
    }

    public int updateFeed(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, item.getStatus());
        // updating row
        return db.update(TABLE_FEED, values, ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
    }

    public Boolean updateStatus(String status, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE "+ TABLE_FEED +" SET " + STATUS + "=? WHERE " + ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { status, id });
        int r = c.getCount();
        c.close();

        if(r == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public void deleteFeed(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEED, ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
        db.close();
    }


    public void truncateFeedTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_FEED + " WHERE " + STATUS  + "=0");
        db.execSQL("VACUUM");
    }
}
