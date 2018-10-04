package rz.flyimagepicker;

import android.content.Context;
import android.os.Environment;

public class FlyPathManager {
    //FlyDirManager
    private Context context;
    private String rootDirectory;
    private String directory;

    public FlyPathManager(Context argContext) {
        context = argContext;
    }

    private String getSysRootDirectory() {
        return getRootDir();
    }

    private String getSysDirectory() {
        //return rootDirectory + "/" + context.getPackageName();
        return getSysRootDirectory() + "/" + context.getPackageName();
    }


    private String getSysRootCacheDirectory() {
        return getRootCacheDir();
    }

    /*private String getSysCacheDirectory() {
        //return rootDirectory + "/" + context.getPackageName();
        return getSysRootCacheDirectory() + "/" + context.getPackageName();
    }*/


    public String getRootDirectory() {
        //setRootDirectory(getSysDirectory());
        rootDirectory = getSysDirectory();
        directory = rootDirectory;
        return directory;
    }

    public String getRequestRootPath(FlyPathManager argFlyPathManager, String argStrPath) {
        return argFlyPathManager.getRootCacheDirectory(argStrPath).toString();
    }

    public String getRequestCachePath(FlyPathManager argFlyPathManager, String argStrPath) {
        return argFlyPathManager.getRootCacheDirectory(argStrPath).toString();
    }

    public String getRootDirectory(String argDirectoryName) {
        if (argDirectoryName == null) {
            return getRootDirectory();
        } else if (argDirectoryName.trim().isEmpty()) {
            return getRootDirectory();
        } else {
            rootDirectory = getRootDir() + "/" + argDirectoryName;
            directory = rootDirectory;
        }
        /*FlyDirFileManager flyDirFileManager = new FlyDirFileManager();
        flyDirFileManager.makeDirs(directory);*/
        return directory;
    }

    protected String getRootCacheDirectory() {
        //setRootDirectory(getSysDirectory());
        rootDirectory = getSysRootCacheDirectory();
        directory = rootDirectory;
        //log("PRINT: " + directory);
        return directory;
    }

    protected String getRootCacheDirectory(String argDirectoryName) {
        /*if (argDirectoryName == null) {
            setRootCacheDirectory();
            return this;
        } else if (argDirectoryName.trim().isEmpty()) {
            setRootCacheDirectory();
            return this;
        } else {
            rootDirectory = getRootCacheDir() + "/" + argDirectoryName;
            directory = rootDirectory;
        }*/
        if (argDirectoryName == null) {
            return getRootCacheDirectory();
        } else if (argDirectoryName.trim().isEmpty()) {
            return getRootCacheDirectory();
        }
        rootDirectory = getRootCacheDir() + "/" + argDirectoryName;
        directory = rootDirectory;
        /*FlyDirFileManager flyDirFileManager = new FlyDirFileManager();
        flyDirFileManager.makeDirs(directory);*/
        //log("PRINT: " + directory);
        return directory;
    }

    private String getRootDir() {
        return Environment.getExternalStorageDirectory() + "";
    }

    private String getRootCacheDir() {
        return context.getCacheDir() + "";
    }

    private void log(String argMessage) {
        System.out.println("FlyDirManager_DEBUG_LOG_PRINT: " + argMessage);
    }
}
