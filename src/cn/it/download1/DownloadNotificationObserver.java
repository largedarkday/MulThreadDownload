package cn.it.download1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



import cn.it.download.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class DownloadNotificationObserver implements DownloadObserver{

    private Map<String,Notification> notifications = new ConcurrentHashMap<String,Notification>();
    private NotificationManager notificationManager;
    private Context mContext;
    private static DownloadNotificationObserver instance;
    private static final int PROGRESSBAR_MAX_LEVEL = 1000;

    private DownloadNotificationObserver(Context context){
        mContext = context;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
    public static DownloadNotificationObserver getInstance(Context context){
        if(instance == null){
            instance = new DownloadNotificationObserver(context);
        }
        return instance;
    }
    
    private Notification createNotification(DownloadBean downloadBean){
        String tickerText = downloadBean.url;
        long when = System.currentTimeMillis(); 
        Notification notification = new Notification(android.R.drawable.stat_sys_download, tickerText, when);

        //Intent intent = new Intent(this,Bactivity.class);  
        //PendingIntent pendingIntent  = PendingIntent.getActivity(this, 0, intent, 0);  
        //notification.contentIntent = pendingIntent;

        //Ëá™ÂÆö‰πâÁïåÈù?  
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.download_notification);  
        rv.setTextViewText(R.id.tv_download_file_name, downloadBean.name);
        notification.contentView = rv;
        
        return notification;
    }

    @Override
    public void onDownloadStateChanged(DownloadBean downloadBean) {
        synchronized (notifications) {
            switch (downloadBean.downloadState) {
            case DownloadTaskManager.STATE_DOWNLOADING:
                Notification no = null;
                if(notifications.get(downloadBean.url) == null){
                    no = createNotification(downloadBean);
                    notifications.put(downloadBean.url, no);
                }else{
                    no = notifications.get(downloadBean.url);
                }
                int level = getProgress(downloadBean.downloadSize, downloadBean.fileSize);
                no.contentView.setProgressBar(R.id.pb_download_file_progress, PROGRESSBAR_MAX_LEVEL, level, false);
                no.flags= Notification.FLAG_NO_CLEAR;
                no.contentView.setViewVisibility(R.id.tv_download_file_state, View.GONE);
                no.contentView.setViewVisibility(R.id.pb_download_file_progress, View.VISIBLE);
                notificationManager.notify(downloadBean.url,0, no);
                break;
            case DownloadTaskManager.STATE_DOWNLOADED:
                if(notifications.containsKey(downloadBean.url)){
                    Notification n = notifications.get(downloadBean.url);
                    n.flags = Notification.FLAG_AUTO_CANCEL;
                    n.contentView.setViewVisibility(R.id.tv_download_file_state, View.VISIBLE);
                    n.contentView.setTextViewText(R.id.tv_download_file_state, "œ¬‘ÿÕÍ≥…");//mContext.getString(R.string.download_completed)
                    n.contentView.setViewVisibility(R.id.pb_download_file_progress, View.GONE);
                    notificationManager.notify(downloadBean.url,0, n);
                }
                break;
            case DownloadTaskManager.STATE_PAUSED:
                if(notifications.containsKey(downloadBean.url)){
                    Notification n = notifications.get(downloadBean.url);
                    n.flags = Notification.FLAG_AUTO_CANCEL;
                    n.contentView.setViewVisibility(R.id.tv_download_file_state, View.VISIBLE);
                    n.contentView.setTextViewText(R.id.tv_download_file_state, "‘›Õ£");//mContext.getString(R.string.download_paused)
                    n.contentView.setViewVisibility(R.id.pb_download_file_progress, View.GONE);
                    notificationManager.notify(downloadBean.url,0, n);
                }
                break;
            case DownloadTaskManager.STATE_ERROR:
                if(notifications.containsKey(downloadBean.url)){
                    Notification n = notifications.get(downloadBean.url);
                    n.flags = Notification.FLAG_AUTO_CANCEL;
                    n.contentView.setViewVisibility(R.id.tv_download_file_state, View.VISIBLE);
                    n.contentView.setTextViewText(R.id.tv_download_file_state, "‘›Õ£");//mContext.getString(R.string.download_paused)
                    n.contentView.setViewVisibility(R.id.pb_download_file_progress, View.GONE);
                    notificationManager.notify(downloadBean.url,0, n);
                }
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onDownloadProgressed(DownloadBean downloadBean) {
        synchronized (notifications) {
            if(notifications.containsKey(downloadBean.url)){
                Notification n = notifications.get(downloadBean.url);
                int level = getProgress(downloadBean.downloadSize, downloadBean.fileSize);
                n.contentView.setProgressBar(R.id.pb_download_file_progress, PROGRESSBAR_MAX_LEVEL, level, false);
                notificationManager.notify(downloadBean.url,0, n);
            }
        }
    }
    
    private int getProgress(long downloadSize,long fileSize){
        int result = PROGRESSBAR_MAX_LEVEL;
        if(downloadSize != fileSize){
            double d = ((double)downloadSize)/fileSize;
            result = (int)(d*PROGRESSBAR_MAX_LEVEL);
        }
        return result;
    }
    
}
