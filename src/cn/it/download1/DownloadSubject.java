package cn.it.download1;

public interface DownloadSubject {

    public void registerObserver(DownloadObserver o);
    
    public void unRegisterObserver(DownloadObserver o);
    
    public void notifyDownloadStateChanged(DownloadBean downloadBean);
    
    public void notifyDownloadProgressed(DownloadBean downloadBean);
}
