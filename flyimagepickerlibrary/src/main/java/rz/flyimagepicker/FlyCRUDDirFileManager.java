package rz.flyimagepicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FlyCRUDDirFileManager {
    public boolean isDebug = true;

    public void makeDirs(String argDirectoryPath) {
        File file = new File(argDirectoryPath);
        if (!isDirExists(file)) {
            file.mkdirs();
            log("Directory created: " + argDirectoryPath);
        } else {
            log("Directory already exists");
        }
    }

    private boolean isDirExists(String argFile) {
        File file = new File(argFile);
        return (file.exists() && file.isDirectory());
    }

    private boolean isDirExists(File argFile) {
        return (argFile.exists() && argFile.isDirectory());
    }

    private boolean isFileExists(String argFile) {
        File file = new File(argFile);
        return file.exists() && file.isFile();
    }

    private boolean isFileExists(File argFile) {
        return argFile.exists() && argFile.isFile();
    }

    public void fileCopy(File argSourcePath, File argDestinationPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(argSourcePath);
            outputStream = new FileOutputStream(argDestinationPath);
            byte[] byteArray = new byte[1024];
            int length;
            while ((length = inputStream.read(byteArray)) > 0) {
                outputStream.write(byteArray, 0, length);
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            log("Error: " + e.getMessage());
        } catch (IOException e) {
            //e.printStackTrace();
            log("Error: " + e.getMessage());
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                //e.printStackTrace();
                log("Error: " + e.getMessage());
            }
        }
    }

    public boolean deleteFile(String argFilePath) {
        File file = new File(argFilePath);
        if (file.exists()) {
            file.delete();
        }
        return file.exists();
    }

    private void log(String argMessage) {
        if (isDebug) {
            System.out.println();
            System.out.println("FlyDirFileManager_DEBUG_LOG_PRINT: " + argMessage);
        }
        boolean installedMaps = false;

        /*// CHECK IF AN APPLICATION IS INSTALLED
        PackageManager pkManager = getPackageManager();
        try {
            PackageInfo pkInfo = pkManager.getPackageInfo("com.google.android.apps.maps", 0); // REPLACE THIS "com.google.android.apps.maps" WITH THE ACTUAL PACAKAGE NAME
            // Log.e("pkInfo", pkInfo.toString());
            installedMaps = true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            installedMaps = false;
        }*/
    }
}