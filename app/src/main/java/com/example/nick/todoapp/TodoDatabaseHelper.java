package com.example.nick.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 9/17/16.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "todoItemDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ITEMS = "items";

    // Items Table Columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TEXT = "text";

    private static TodoDatabaseHelper sInstance;
    public static synchronized TodoDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }

    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the tables
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_ITEM_TEXT + " TEXT" +
                ")";

        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // This method is called when database is upgraded like
    // modifying the table structure,
    // adding constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // SQL for upgrading the tables
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    // Insert an item into the database
    public Item createItem(String text) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        Item newItem = new Item(text);

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            // The user might already exist in the database (i.e. the same user created multiple posts).
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, newItem.text);

            // update newItem with corresponding id in created db row
            newItem.id = db.insertOrThrow(TABLE_ITEMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("SQL", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
        return newItem;
    }

    public long updateItem(Item item) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long updated = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, item.text);

            String itemsUpdateQuery = String.format("UPDATE %s SET %s = ? WHERE id = ?",
                    TABLE_ITEMS, KEY_ITEM_TEXT, new String[]{item.text}, item.id);

            updated = db.update(TABLE_ITEMS, values, "id = ?", new String[]{String.valueOf(item.id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("SQL","Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return updated;
    }

    public long deleteItem(Item item) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        int deleted = -1;

        db.beginTransaction();
        try {
            deleted = db.delete(TABLE_ITEMS, "id = ?", new String[]{String.valueOf(item.id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("SQL","Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return deleted;
    }

    // Get all posts in the database
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        String ITEMS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_ITEMS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex("id"));
                    String text = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TEXT));
                    Item newItem = new Item(id,text);

                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("SQL", "Error while trying to get items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

}
