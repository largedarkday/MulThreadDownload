package cn.it.download1;

public interface DownloadObserver {

    public void onDownloadStateChanged(DownloadBean downloadBean);
    
    public void onDownloadProgressed(DownloadBean downloadBean);
}
