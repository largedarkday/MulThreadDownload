package cn.it.download1;

public class FileContent {
    public long createTime = 0;
    public String dir = "";
    public String name = "";
    public String path = "";
    public String type = "";
    public long visitTime = 0;
    public long modifyTime = 0;
    public String ext = "";
    public long size = 0;
    public int mode = 0;
    public String lastModified = "";
    public int favoriteState = 0;
    public String downloadUrl = null;
    public boolean isSelect;
    public int isAdd = 0;
    public boolean isOperate = false;
    public boolean isFolder;
    public String hash;
    public String UID;
    public String getId() {
        return downloadUrl;
    }
}