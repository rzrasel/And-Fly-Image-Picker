package rz.flyimagepicker;

import android.content.Context;
import android.os.Environment;

public class FlyDirManager {
    private Context context;
    private String rootDirectory;
    private String directory;

    public FlyDirManager(Context argContext) {
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


    public FlyDirManager setRootDirectory() {
        //setRootDirectory(getSysDirectory());
        rootDirectory = getSysDirectory();
        directory = rootDirectory;
        return this;
    }

    public FlyDirManager setRootDirectory(String argDirectoryName) {
        if (argDirectoryName == null) {
            setRootDirectory();
            return this;
        } else if (argDirectoryName.trim().isEmpty()) {
            setRootDirectory();
            return this;
        } else {
            rootDirectory = getRootDir() + "/" + argDirectoryName;
            directory = rootDirectory;
        }
        /*FlyDirFileManager flyDirFileManager = new FlyDirFileManager();
        flyDirFileManager.makeDirs(directory);*/
        return this;
    }

    public String setRootCacheDirectory() {
        //setRootDirectory(getSysDirectory());
        rootDirectory = getSysRootCacheDirectory();
        directory = rootDirectory;
        log("PRINT: " + directory);
        return directory;
    }

    public String setRootCacheDirectory(String argDirectoryName) {
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
        rootDirectory = getRootCacheDir() + "/" + argDirectoryName;
        directory = rootDirectory;
        /*FlyDirFileManager flyDirFileManager = new FlyDirFileManager();
        flyDirFileManager.makeDirs(directory);*/
        log("PRINT: " + directory);
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
