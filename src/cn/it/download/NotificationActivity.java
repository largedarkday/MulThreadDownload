package cn.it.download;

import cn.it.download1.DownloadTaskManager;
import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.it.download1.DownloadBean;

public class NotificationActivity extends Activity {
    
    private Button create;
    private DownloadBean bean1;
    private DownloadBean bean2;
    private DownloadBean bean4[];
    private String[] urls = {"http://www.8kmm.com/UploadFiles/2012/6/201206051024060127.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024077286.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024084195.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024119040.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024111589.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024137579.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024130640.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024153888.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024024339.jpg",
            "http://www.8kmm.com/UploadFiles/2012/6/201206051024049139.jpg"};
    private TextView text;
    String url = "fdsss";
    private static String a ="Œ“ «static:1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_acttivity);
        
        create = (Button) findViewById(R.id.create1);
        text = (TextView) findViewById(R.id.text);
        //text.setText(a);
        a =a + 1;
        if(savedInstanceState != null){
            bean1 = (DownloadBean) savedInstanceState.getSerializable("bean1");
            bean2 = (DownloadBean) savedInstanceState.getSerializable("bean2");
        }
    }
    
    @Override
    protected void onResume() {
        text.setText(a);
        super.onResume();
    }
    
    public void createNotification1(View v){
        bean1 = new DownloadBean();
        bean1.url = "vfgdsss";
        bean1.fileSize = 1000;
        bean1.downloadSize = 254;
        bean1.downloadState = 2;
        bean1.name = "Ã⁄—∂ø·≈‹";
        //DownloadNotificationObserver.getInstance(NotificationActivity.this).onDownloadStateChanged(bean1);
    }
    public void createNotification2(View v){
        bean2 = new DownloadBean();
        bean2.url = "fdsaaaaaaaa";
        bean2.fileSize = 1000;
        bean2.downloadSize = 554;
        bean2.downloadState = 2;
        bean2.name = "Ã⁄—∂ø·≈‹";
        //DownloadNotificationObserver.getInstance(NotificationActivity.this).onDownloadStateChanged(bean2);
    }
    
    public void updateNotification(View v){
        //bean1 = new DownloadBean();
        bean1.url = "vfgdsss";
        bean1.fileSize = 1000;
        bean1.downloadSize = 554;
        bean1.downloadState = 4;
        bean1.name = "Ã⁄—∂ø·≈‹";
        //DownloadNotificationObserver.getInstance(NotificationActivity.this).onDownloadStateChanged(bean1);
    }
    
    public void download(View v){
        bean4 = new DownloadBean[10];
        for(int i =0;i<10;i++){
            bean4[i] = new DownloadBean();
            bean4[i].downloadSize = 0;
            bean4[i].downloadState = 0;
            bean4[i].fileSize = 170000 + i*200;
            bean4[i].sdCardPath = "/storage/sdcard0/1/1/sign"+ i + ".jpg";
            bean4[i].url = urls[i];
            
            DownloadTaskManager.getInstance(getApplicationContext()).download(bean4[i]);
                   }
        //DownloadTaskManager.getInstance(getApplicationContext()).cancel(bean4[5]);  
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("bean1", bean1);
        outState.putSerializable("bean2", bean2);
        super.onSaveInstanceState(outState);
    }
}
