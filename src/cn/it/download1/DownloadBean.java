package cn.it.download1;

import java.io.File;
import java.io.Serializable;

import android.content.Context;


public class DownloadBean implements Serializable{

    public static final int STATE_UNFINISH = 0;
    public static final int STATE_FINISH = 1;

    public String url = "";
    public String sdCardPath = "";
    public String deviceId = "";
    public long fileSize = 0;
    public long downloadSize = 0;
    public int downloadState = 0;
    public String name = "";
    public long date = 0;
    public String serverPath = "";

    public DownloadBean() {

    }

    /*public DownloadBean(String url, String sdCardPath, long fileSize, long downloadSize, int downloadState) {
        this.url = url;
        this.sdCardPath = sdCardPath;
        this.fileSize = fileSize;
        this.downloadSize = downloadSize;
        this.downloadState = downloadState;
    }*/

    /*public static DownloadBean clone(DownloadBean fileContent, Context mContext) {
        DownloadBean bean = new DownloadBean();
        bean.url = fileContent.downloadUrl;
        bean.sdCardPath = fileContent.dir;
        bean.deviceId = fileContent.ext;
        bean.fileSize = fileContent.size;
        bean.downloadSize = 0;
        bean.downloadState = 0;
        bean.name = fileContent.name;
        bean.date = fileContent.modifyTime;
        bean.serverPath = fileContent.downloadUrl;
        return bean;
    }*/

    public void setDownloadState(int downloadState) {
        downloadState = downloadState;
    }

    public void setCurrentSize(long l) {
        downloadSize = l;
    }
}