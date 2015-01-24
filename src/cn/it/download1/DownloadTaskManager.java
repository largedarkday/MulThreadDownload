package cn.it.download1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadTaskManager implements DownloadSubject {

    public static final int STATE_NONE = 0;
    /** Á≠âÂæÖ‰∏?*/
    public static final int STATE_WAITING = 1;
    /** ‰∏ãËΩΩ‰∏?*/
    public static final int STATE_DOWNLOADING = 2;
    /** ÊöÇÂÅú */
    public static final int STATE_PAUSED = 3;
    /** ‰∏ãËΩΩÂÆåÊØï */
    public static final int STATE_DOWNLOADED = 4;
    /** ‰∏ãËΩΩÂ§±Ë¥• */
    public static final int STATE_ERROR = 5;
    
    private static final int STATE_CHANGED = 6;
    private static final int STATE_PROGRESSED = 7;


    private Context mContext;
    public static int downloadTaskMax = 3;
    private Boolean isDownloading;
    private static DownloadTaskManager instance;
    
    private List<DownloadObserver> mObservers = new ArrayList<DownloadObserver>();
    private Map<String, DownloadBean> mDownloadMap = new ConcurrentHashMap<String, DownloadBean>();
    private Map<String, DownloadTask> mTaskMap = new ConcurrentHashMap<String, DownloadTask>();
    
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case STATE_CHANGED:
                synchronized (mObservers) {
                    Bundle bundle = msg.getData();
                    DownloadBean bean = (DownloadBean) bundle.getSerializable("bean");
                    for (DownloadObserver observer : mObservers) {
                        observer.onDownloadStateChanged(bean);
                    }
                }
                break;
            case STATE_PROGRESSED:
                synchronized (mObservers) {
                    Bundle bundle = msg.getData();
                    DownloadBean bean = (DownloadBean) bundle.getSerializable("bean");
                    for (DownloadObserver observer : mObservers) {
                        observer.onDownloadProgressed(bean);
                    }
                }
                break;

            default:
                break;
            }
        }
    };

    public static synchronized DownloadTaskManager getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadTaskManager(context.getApplicationContext());
        }
        return instance;
    }

    private DownloadTaskManager(Context context) {
        this.mContext = context;
        setShowRunningNotification(true);
    }

    public void setShowRunningNotification(boolean show) {
        if (show) {
            registerObserver(DownloadNotificationObserver.getInstance(mContext));
        } else {
            unRegisterObserver(DownloadNotificationObserver.getInstance(mContext));
        }

    }

    public synchronized void download(DownloadBean bean1) {
        DownloadBean bean = bean1;

        /*if (bean == null) {

            bean = DownloadBean.clone(fileContent, mContext);
            mDownloadMap.put(fileContent.getId(), bean);
        }*/
        if (bean.downloadState == STATE_NONE || bean.downloadState == STATE_PAUSED
                || bean.downloadState == STATE_ERROR) {

            bean.setDownloadState(STATE_WAITING);
            notifyDownloadStateChanged(bean);
            DownloadTask task = new DownloadTask(bean);
            mTaskMap.put(bean.url, task);
            ThreadManager.getDownloadPool().execute(task);
        }
    }

    public synchronized void pause(FileContent fileContent) {
        DownloadTask task = mTaskMap.remove(fileContent.getId());
        if (task != null) {
            ThreadManager.getDownloadPool().cancel(task);
        }
        DownloadBean bean = mDownloadMap.get(fileContent.getId());
        if (bean != null) {
            bean.setDownloadState(STATE_PAUSED);
            notifyDownloadStateChanged(bean);
        }
    }

    public synchronized void cancel(DownloadBean fileContent) {
        DownloadTask task = mTaskMap.remove(fileContent.url);
        if (task != null) {
            ThreadManager.getDownloadPool().cancel(task);
        }
        /*DownloadBean bean = mDownloadMap.get(fileContent.getId());
        if (bean != null) {
            bean.setDownloadState(STATE_NONE);
            notifyDownloadStateChanged(bean);
            bean.setCurrentSize(0);
            File file = new File(bean.sdCardPath);
            file.delete();
        }*/
    }
    
    public synchronized DownloadBean getDownloadInfo(long id) {
        return mDownloadMap.get(id);
    }

    @Override
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }

    @Override
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }

    @Override
    public void notifyDownloadStateChanged(DownloadBean downloadBean) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", downloadBean);
        //bundle.putParcelable(key, value);
        msg.what = STATE_CHANGED;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void notifyDownloadProgressed(DownloadBean downloadBean) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", downloadBean);
        msg.what = STATE_PROGRESSED;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public class DownloadTask implements Runnable {

        private DownloadBean bean;
        FileService fileService = new FileService(mContext);

        public DownloadTask(DownloadBean bean) {
            this.bean = bean;
        }
        
        @Override
        public void run() {
            
                InputStream inStream = null;
                RandomAccessFile threadfile = null;
                URL downUrl = null;
                try {
                    downUrl = new URL(bean.url);
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
                    http.setConnectTimeout(5 * 1000);
                    http.setRequestMethod("GET");
                    http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                    http.setRequestProperty("Accept-Language", "zh-CN");
                    http.setRequestProperty("Referer", downUrl.toString()); 
                    http.setRequestProperty("Charset", "UTF-8");
                    long startPos = bean.downloadSize;//ø™ ºŒª÷√
                    long endPos = bean.fileSize * 2;//Ω· ¯Œª÷√
                    http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//…Ë÷√ªÒ»° µÃÂ ˝æ›µƒ∑∂Œß
                    http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                    http.setRequestProperty("Connection", "Keep-Alive");
                    
                    inStream = http.getInputStream();
                    byte[] buffer = new byte[1024];
                    int offset = 0;
                    //print("Thread " + this.threadId + " start download from position "+ startPos);
                    File file=new File(bean.sdCardPath);
                    if (!file.exists()) {
                        File parentFile = file.getParentFile();
                        if (parentFile != null && parentFile.exists() == false) {
                            parentFile.mkdirs();
                        }
                    }
                    threadfile = new RandomAccessFile(file, "rwd");
                    threadfile.seek(startPos);
                    //downloader.update(downLength,ThreadState_START);
                    while (!false && (offset = inStream.read(buffer, 0, 1024)) != -1) {
                        threadfile.write(buffer, 0, offset);
                        //downLength += offset;
                        //downloader.append(offset);
                        //downloader.update(downLength,ThreadState_RUN);
                        System.out.println(bean.sdCardPath);
                    }
                    
                    
                    //print("Thread " + this.threadId + " download finish");
                    //this.finish = true;
                    
                } catch (Exception e) {
                    Log.i("cccccccccccccccccccccc", e.toString());
                    //this.exception = true;
                    //downloader.update(downLength,ThreadState_EXCEPTION);
                } finally {
                    System.out.println("over");
                    try {
                        if(threadfile!=null && inStream!=null){
                            threadfile.close();
                            inStream.close();
                        }
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                }
            //}
        }

        /*@Override
        public void run() {
            DownloadBean b = fileService.getFileInfo(bean.url);
            //ÂàùÂßãÂåñ‰∏ãËΩΩËøõÂ∫?
            if(b != null){
                bean.downloadSize = b.downloadSize;
            }else{
                fileService.initNewFile(bean);
            }
            //ÈÄöËøΩ‰∏ãËΩΩÂº?ßã
            bean.setDownloadState(STATE_DOWNLOADING);
            notifyDownloadStateChanged(bean);
            //File file = new File(bean.getPath());
            HttpResponse httpResponse = null;
            HttpEntity httpentity = null;
            InputStream inStream = null;
            RandomAccessFile threadfile = null;
            
            //ËÆ°Êï∞Âô®ÔºåÈò≤Ê≠¢Êõ¥Êñ∞uiÈ¢ëÁπÅ
            int counter = 0;
            
            try {
                threadfile = new RandomAccessFile(bean.sdCardPath, "rwd");
                
                threadfile.seek(bean.downloadSize);
                httpResponse = ElastosServerAPI.getDownloadResponce(mContext, bean.url, bean.downloadSize);
                httpentity = httpResponse.getEntity();
                inStream = httpentity.getContent();
                //fos = new FileOutputStream(file, true);
                int count = -1;
                byte[] buffer = new byte[1024];
                while (((count = inStream.read(buffer)) != -1) && bean.downloadState == STATE_DOWNLOADING) {
                    threadfile.write(buffer, 0, count);
                    
                    bean.setCurrentSize(bean.downloadSize + count);
                    fileService.updateDownloadPositionState(bean.url, bean.downloadSize, DownloadBean.STATE_UNFINISH);
                    counter++;
                    if(counter >= 512){
                        counter = 0;
                        notifyDownloadProgressed(bean);
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                bean.setDownloadState(STATE_ERROR);
                notifyDownloadStateChanged(bean);
                bean.setCurrentSize(0);
                //file.delete();
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (threadfile != null) {
                        threadfile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (bean.downloadSize == bean.fileSize) {
                fileService.updateDownloadPositionState(bean.url, bean.downloadSize, DownloadBean.STATE_FINISH);
                bean.setDownloadState(STATE_DOWNLOADED);
                notifyDownloadStateChanged(bean);
            } else if (bean.downloadState == STATE_PAUSED) {
                notifyDownloadStateChanged(bean);
            } else {
                bean.setDownloadState(STATE_ERROR);
                notifyDownloadStateChanged(bean);
                bean.setCurrentSize(0);
                //file.delete();
            }
            
            mTaskMap.remove(bean.url);
        }*/

    }
}
