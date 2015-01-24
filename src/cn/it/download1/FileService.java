package cn.it.download1;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FileService {

    private DownloadDBOpenHelper openHelper;
    
    public FileService(Context context){
        openHelper = new DownloadDBOpenHelper(context);
    }
    
    /**
     * 取得�?��下载记录
     * @return
     */
    public ArrayList<DownloadBean> getDownloadFiles(){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DownloadDBOpenHelper.TABLE_FILE_DOWN,null);
        ArrayList<DownloadBean> data = new ArrayList<DownloadBean>();
        while(cursor.moveToNext()){
            DownloadBean bean = new DownloadBean();
            bean.url = cursor.getString(1);
            bean.sdCardPath = cursor.getString(2);
            bean.deviceId = cursor.getString(3);
            bean.fileSize = cursor.getInt(4);
            bean.downloadSize = cursor.getInt(5);
            bean.downloadState = cursor.getInt(6);
            bean.name = cursor.getString(7);
            bean.date = cursor.getInt(8);
            bean.serverPath = cursor.getString(9);
            data.add(bean);
        }
        cursor.close();
        db.close();
        return data;
    }
    
    /**
     * 判断是否有下载记�?
     * @param url
     * @return
     */
    public boolean isNewDownload(String url){
        boolean result = true;
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + DownloadDBOpenHelper.KEY_ID + " from "
                + DownloadDBOpenHelper.TABLE_FILE_DOWN + " where "
                + DownloadDBOpenHelper.KEY_URL + " = '" + url + "'", null);
        if (cursor != null && cursor.moveToNext()) {
           result = false;
        }
        cursor.close();
        db.close();
        return result;
    }
    
    /**
     * 
     * @param url
     * @return
     */
    public DownloadBean getFileInfo(String url){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " 
        + DownloadDBOpenHelper.TABLE_FILE_DOWN + " where " 
                + DownloadDBOpenHelper.KEY_URL + " =?", new String[]{url});
        DownloadBean bean = null;
        if(cursor != null && cursor.moveToNext()){
            bean = new DownloadBean();
            bean.url = cursor.getString(1);
            bean.sdCardPath = cursor.getString(2);
            bean.deviceId = cursor.getString(3);
            bean.fileSize = cursor.getInt(4);
            bean.downloadSize = cursor.getInt(5);
            bean.downloadState = cursor.getInt(6);
            bean.name = cursor.getString(7);
            bean.date = cursor.getInt(8);
            bean.serverPath = cursor.getString(9);
        }
        cursor.close();
        db.close();
        return bean;
    }
    
    /**
     * 创建新的下载文件
     * @param bean
     */
    public void initNewFile(DownloadBean bean){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        db.execSQL("insert into " + DownloadDBOpenHelper.TABLE_FILE_DOWN
                + "("
                + DownloadDBOpenHelper.KEY_URL + " , "
                + DownloadDBOpenHelper.KEY_SAVE_PATH + " , "
                + DownloadDBOpenHelper.KEY_DEVICE_ID
                + DownloadDBOpenHelper.KEY_SIZE + " , "
                + DownloadDBOpenHelper.KEY_DOWNLOAD_SIZE + " , "
                + DownloadDBOpenHelper.KEY_STATE + " , "
                + DownloadDBOpenHelper.KEY_NAME + " , "
                + DownloadDBOpenHelper.KEY_DATE + " , "
                + DownloadDBOpenHelper.KEY_SERVER_PATH + " , "
                + " ) values(?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        bean.url,
                        bean.sdCardPath,
                        bean.deviceId,
                        bean.fileSize,
                        bean.downloadSize,
                        bean.downloadState,
                        bean.name,
                        bean.date,
                        bean.serverPath
                }
        );
        db.close();
    }
    
    /**
     * 更新某一条记录下载位�?
     * @param url
     * @param downloadSize
     */
    public void updateDownloadPositionState(String url, long downloadSize,int state){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("update " + DownloadDBOpenHelper.TABLE_FILE_DOWN 
                + " set " + DownloadDBOpenHelper.KEY_DOWNLOAD_SIZE + "=?,"
                + DownloadDBOpenHelper.KEY_STATE + "=?"
                +" where " + DownloadDBOpenHelper.KEY_URL + "=?",
                new Object[]{downloadSize, url,state});
        db.close();
    }
    
    /**
     * 更新某一条下载记录下载状�?
     * @param url
     * @param state
     */
    public void updateDownloadState(String url, int state){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("update " + DownloadDBOpenHelper.TABLE_FILE_DOWN 
                + " set " + DownloadDBOpenHelper.KEY_DATE + "=?"
                +" where " + DownloadDBOpenHelper.KEY_URL + "=?",
                new Object[]{state, url});
        db.close();
    }
    
    /**
     * 删除某一条下载记�?
     * @param url
     */
    public void deleteFile(String url){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from " + DownloadDBOpenHelper.TABLE_FILE_DOWN
                + " where " + DownloadDBOpenHelper.KEY_URL + "=?", new Object[]{url});
        db.close();
    }
    
    /**
     * 删除�?��下载记录
     */
    public void deleteAllFile(){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.execSQL("delete from " + DownloadDBOpenHelper.TABLE_FILE_DOWN, new Object[]{});
        db.close();
    }
}
