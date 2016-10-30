package hung.armylife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hung.armylife.fragment.CountDownFragment;

/**
 * Created by Hung on 2016/10/30.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static SQLiteDatabase database;
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "ArmyLife.db";
    public static final String TABLE_NAME = "armylife";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    //public static final String COUNTDOWN_COLUMN = "countdown";
    public static final String MOOD_COLUMN = "mood";
    public static final String ENTRYDATE_COLUMN = "entrydate";
    public static final String EXITDATE_COLUMN = "exitdate";
    public static final String FACEBOOK_ID_COLUMN = "facebook_id";
    //public static final String FRIENDS_COLUMN = "friends";
    public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ID + " INTEGER PRIMARY KEY,"
            + NAME_COLUMN + " TEXT,"
            + MOOD_COLUMN + " TEXT,"
            + ENTRYDATE_COLUMN + " TEXT,"
            + EXITDATE_COLUMN + " TEXT,"
            + FACEBOOK_ID_COLUMN + " TEXT)";
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        db = getDatabase(context);
    }
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new SQLiteHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }
    public void update_insert(String name, String mood, String entrydate, String exitdate, String facebook_id) {
        if(!update(name,mood,entrydate,exitdate,facebook_id)) insert(name,mood,entrydate,exitdate,facebook_id);
    }
    // 新增參數指定的物件
    public long insert(String name, String mood, String entrydate, String exitdate, String facebook_id) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(NAME_COLUMN, name);
        cv.put(MOOD_COLUMN, mood);
        cv.put(ENTRYDATE_COLUMN, entrydate);
        cv.put(EXITDATE_COLUMN, exitdate);
        cv.put(FACEBOOK_ID_COLUMN, facebook_id);
        //cv.put(FRIENDS_COLUMN, friends);

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        return id;
    }

    // 修改參數指定的物件
    public boolean update(String name, String mood, String entrydate, String exitdate, String facebook_id) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(NAME_COLUMN, name);
        cv.put(MOOD_COLUMN, mood);
        cv.put(ENTRYDATE_COLUMN, entrydate);
        cv.put(EXITDATE_COLUMN, exitdate);
        cv.put(FACEBOOK_ID_COLUMN, facebook_id);
        //cv.put(FRIENDS_COLUMN, friends);

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = FACEBOOK_ID_COLUMN + "=" + facebook_id;

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(String facebook_id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = FACEBOOK_ID_COLUMN + "=" + facebook_id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }
    public void saveToSQLite(List<Map<String,Object>> item) {
        for(int i=0;i<item.size();i++) {
            String name = item.get(i).get("name").toString();
            String mood = item.get(i).get("mood").toString();
            String entrydate = item.get(i).get("entrydate").toString();
            String exitdate = item.get(i).get("exitdate").toString();
            String facebook_id = item.get(i).get("facebook_id").toString();
            //String friends = item.get(i).get("friends").toString();
            Log.d("saveToSQLite",name);
            update_insert(name,mood,entrydate,exitdate,facebook_id);//,friends);
        }
    }
    public List<Map<String, Object>> getFriendsData() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }
    // 把Cursor目前的資料包裝為物件
    public Map<String, Object> getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Map<String, Object> result = new HashMap<String, Object>();
        String cd = new CountDownFragment().getCountDown(cursor.getString(4));
        result.put("name", cursor.getString(1));
        result.put("mood", cursor.getString(2));
        result.put("cd",cd);
        //result.put("entrydate", cursor.getString(3));
        //result.put("exitdate", cursor.getString(4));
        result.put("facebook_id", cursor.getString(5));

        // 回傳結果
        return result;
    }
}
