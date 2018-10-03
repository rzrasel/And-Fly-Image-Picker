package rz.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import static rz.flyimagepicker.DirFileManager.READ_WRITE_STORAGE_REQUEST;

public class FlyImageManager {
    private Context context;
    private String rootDirectory;
    private String directory;
    //private String directoryPath;
    private int width = 0;
    private int height = 0;
    private Bitmap bitmap;
    private String name = "";
    private int quality = 100;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
    private String fileTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    public boolean isDebug = true;

    public FlyImageManager(Context argContext) {
        context = argContext;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= 23) {
            if (!FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
                FlyPermissionsManager.requestPermissions(context, READ_WRITE_STORAGE_REQUEST, PERMISSIONS);
                log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                return;
            }
        } else if (!FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
            log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
            return;
        }
    }

    /*private FlyDirManager flyDirManager = new FlyDirManager(context) {
        @Override
        public FlyDirManager setRootCacheDirectory() {
            return super.setRootCacheDirectory();
        }
    };*/

    /*public FlyImageManager set() {
        FlyDirManager flyDirManager = new FlyDirManager(context) {
            @Override
            public FlyDirManager setRootCacheDirectory() {
                return super.setRootCacheDirectory();
            }
        }.setRootCacheDirectory();
        return this;
    }*/
    public FlyImageManager set(String argParam) {
        FlyDirManager flyDirManager = new FlyDirManager(context) {
            @Override
            public String setRootCacheDirectory() {
                return "from- " + super.setRootCacheDirectory();
            }

            @Override
            public String setRootCacheDirectory(String argDirectoryName) {
                return super.setRootCacheDirectory(argDirectoryName);
            }
        };
        checkMe(flyDirManager, argParam);
        return this;
    }

    private void checkMe(FlyDirManager argFlyDirManager, String argParam) {
        log(argFlyDirManager.setRootCacheDirectory().toString());
    }

    public FlyImageManager withDirectoryPath(String argDirectoryPath) {
        directory = directory + "/" + argDirectoryPath;
        //System.out.println("FILE_DIRECTORY: " + directory);
        return this;
    }

    public FlyImageManager withWidth(int argWidth) {
        width = argWidth;
        return this;
    }

    public FlyImageManager withHeight(int argHeight) {
        height = argHeight;
        return this;
    }

    public FlyImageManager withImageView(ImageView argImageView) {
        bitmap = null;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
            /*argImageView.setDrawingCacheEnabled(true);
            argImageView.buildDrawingCache(true);
            Bitmap bitmap = argImageView.getDrawingCache();*/
        return this;
    }

    public FlyImageManager withBitmap(Bitmap argBitmap) {
        bitmap = null;
        bitmap = argBitmap;
        return this;
    }

    public FlyImageManager withName(String argName) {
        name = argName;
        return this;
    }

    public FlyImageManager withQuality(int argQuality) {
        quality = argQuality;
        return this;
    }

    public FlyImageManager withCompressFormat(Bitmap.CompressFormat argCompressFormat) {
        compressFormat = argCompressFormat;
        return this;
    }

    public FlyImageManager withResize() {
        bitmap = getResizedBitmap(bitmap, width, height);
        return this;
    }

    public String getFilePath(String argFilePath) {
        return directory + "/" + argFilePath;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void copy() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(compressFormat, quality, byteArrayOutputStream);
            File file = new File(directory, name);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    public void copy(ImageView argImageView, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap = getResizedBitmap(bitmap, width, height);
                /*argImageView.setDrawingCacheEnabled(true);
                argImageView.buildDrawingCache(true);
                Bitmap bitmap = argImageView.getDrawingCache();*/
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(argCompressFormat, argQuality, byteArrayOutputStream);
            File file = new File(directory);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    public void copy(String argSourcePath, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(directory, bmOptions);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //argScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            bitmap.compress(argCompressFormat, argQuality, byteArrayOutputStream);
            File file = new File(argSourcePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    public void copy(Bitmap argBitmap, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            argBitmap.compress(argCompressFormat, argQuality, byteArrayOutputStream);
            File file = new File(directory);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    public Bitmap getResizedBitmap(Bitmap argBitmap, int argTargetWidth, int argTargetHeight) {
        if (argTargetWidth > 0 && argTargetHeight > 0) {
            int width = argBitmap.getWidth();
            int height = argBitmap.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) argTargetWidth / (float) argTargetHeight;

            int finalWidth = argTargetWidth;
            int finalHeight = argTargetHeight;
                /*if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float) argTargetHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) argTargetWidth / ratioBitmap);
                }*/
            if (width > height) {
                // landscape
                float ratio = (float) width / argTargetWidth;
                finalWidth = argTargetWidth;
                finalHeight = (int) (height / ratio);
            } else if (height > width) {
                // portrait
                float ratio = (float) height / argTargetHeight;
                finalWidth = (int) (width / ratio);
                finalHeight = argTargetHeight;
            } else {
                // square
                finalWidth = argTargetWidth;
                finalHeight = argTargetHeight;
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(argBitmap, finalWidth, finalHeight, true);
            //Bitmap background = Bitmap.createBitmap((int)finalWidth, (int)finalHeight, Bitmap.Config.ARGB_8888);
            return bitmap;
        } else {
            log("ERROR_IMAGE_SIZE");
            return argBitmap;
        }
    }

    public String getNewName(String argName, ImageFormat argImageFormat) {
        return argName + "-" + fileTimeStamp + "." + argImageFormat.getValue();
    }

    public enum ImageFormat {
        JPEG("jpg"),
        PNG("png"),
        WEBP("webp");
        private String value;

        ImageFormat(String argValue) {
            value = argValue;
        }

        public String getValue() {
            return this.value;
        }
    }

    private void log(String argMessage) {
        if (isDebug) {
            System.out.println();
            System.out.println("DEBUG_LOG_PRINT: " + argMessage);
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

    public static class StringUtils {
        public static String join(String[] argString, String argDelimiter) {
            String result = "";
            int count = 0;
            for (String item : argString) {
                result += item;
                if (count < argString.length - 1) {
                    result += argDelimiter;
                }
                count++;
            }
            return result;
        }
    }
}