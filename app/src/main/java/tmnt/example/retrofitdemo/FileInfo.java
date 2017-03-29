package tmnt.example.retrofitdemo;

/**
 * Created by tmnt on 2017/3/13.
 */

public class FileInfo {

    /**
     * name : apps_128px_1145144_easyicon.net.png
     * path : G:\image\apps_128px_1145144_easyicon.net.png
     */

    private String name;
    private String path;


    public FileInfo() {
    }

    public FileInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
