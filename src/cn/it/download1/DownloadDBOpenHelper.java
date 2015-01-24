package cn.it.download1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DownloadDBOpenHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "download.db";
    private static final int VERSION = 4;

    public static final String TABLE_FILE_DOWN = "tabel_filedown";

    public static final String KEY_ID = "_id";// 自动增长的ID
    public static final String KEY_DEVICE_ID = "device_id";// 盒子的唯�?���?
    public static final String KEY_URL = "url";// 下载地址
    public static final String KEY_SAVE_PATH = "save_path";// 保存地址
    public static final String KEY_SIZE = "file_size";// 文件大小
    public static final String KEY_DOWNLOAD_SIZE = "file_download_size";// 文件大小
    public static final String KEY_STATE = "download_state";// 软件的下载情�?
    public static final String KEY_NAME = "file_name";// 名称
    public static final String KEY_DATE = "date";// 时间
    public static final String KEY_SERVER_PATH = "server_path";

    public DownloadDBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FILE_DOWN + " (" + KEY_ID + " integer primary key autoincrement,"
                + KEY_URL + " varchar(100), " + KEY_SAVE_PATH + " varchar(100), " + KEY_DEVICE_ID + " varchar(100), " + KEY_SIZE
                + " integer, " + KEY_DOWNLOAD_SIZE + " integer, " + KEY_STATE + " int, " + KEY_NAME + " varchar(20), " + KEY_DATE
                + " integer, " + KEY_SERVER_PATH + " varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE_DOWN);
        onCreate(db);
    }

}
