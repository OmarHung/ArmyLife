package hung.armylife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by Hung on 2016/6/25.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase db;
    public static final String DATABASE_NAME = "ArmyLifeSQLite.db";
    public static final String TABLE_NAME = "Contact";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String MESSEGE_COLUMN = "messege";
    public static final int VERSION = 4;
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ID + " INTEGER PRIMARY KEY,"
            + NAME_COLUMN + " TEXT,"
            + PHONE_COLUMN + " TEXT,"
            + MESSEGE_COLUMN + " TEXT)";
    private Context mContext = null;
    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
    }
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (db == null || !db.isOpen()) {
            db = new DBHelper(context).getWritableDatabase();
        }
        return db;
    }
    public void close() {
        Log.d("close","close");
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG", "No DB");
        try {
            Log.e("TAG", "No DB");
            db.execSQL(CREATE_TABLE);
            /*
            for(int i=0; i<1; i++) {
                ContentValues args = new ContentValues();
                args.put(NAME_COLUMN, "組長"+i);
                args.put(PHONE_COLUMN, "0912345678");
                args.put(MESSEGE_COLUMN, i+"報告，在家");
                db.insert(TABLE_NAME, null, args);
            }
                */
        }catch (Exception e) {
            Log.e("TAG", "Has DB"+e);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("TAG", "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

    public long append(SQLiteDatabase db, String name, String phone, String messege) { // 新增資料
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(MESSEGE_COLUMN, messege);
        return db.insert(TABLE_NAME, null, args);
    }
    public boolean update(SQLiteDatabase db, long rowId, String name, String phone, String messege) {
        Log.d("TAG","update");
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(MESSEGE_COLUMN, messege);
        return db.update(TABLE_NAME, args, KEY_ID + "=" + rowId, null) > 0;
    }
    public boolean updateMessege(SQLiteDatabase db, long rowId, String messege) {
        ContentValues args = new ContentValues();
        args.put(MESSEGE_COLUMN, messege);
        return db.update(TABLE_NAME, args, KEY_ID + "=" + rowId, null) > 0;
    }
    public boolean delete(SQLiteDatabase db, long rowId) {
        return db.delete(TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
    }
    public String getDate(long rowId, String columnName) {
        Cursor c = db.rawQuery("SELECT" + columnName + "FROM " + TABLE_NAME + "WHERE" + KEY_ID + "=" + rowId, null);
        c.moveToFirst();
        return c.getString(0);
    }
}
