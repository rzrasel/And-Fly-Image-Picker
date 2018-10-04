package rz.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

import static rz.flyimagepicker.DirFileManager.READ_WRITE_STORAGE_REQUEST;

public class FlyImageManager {
    private Context context;
    private String rootDirectory;
    private String workingDirectory;
    private String referDirectory;
    private String referFullFilePath;
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

    public void setCachePath(String argStrDirPath) {
        FlyPathManager flyPathManager = new FlyPathManager(context) {
            @Override
            public String getRootCacheDirectory() {
                return super.getRootCacheDirectory();
            }

            @Override
            public String getRootCacheDirectory(String argDirectoryName) {
                return super.getRootCacheDirectory(argDirectoryName);
            }
        };
        workingDirectory = flyPathManager.getRequestCachePath(flyPathManager, argStrDirPath);
        referDirectory = workingDirectory;
        if (argStrDirPath != null) {
            if (!argStrDirPath.trim().isEmpty()) {
                FlyCRUDPathManager.onCreateDirs(workingDirectory);
            }
        }
    }

    public FlyImageManager withDirectoryPath(String argDirectoryPath) {
        referDirectory = workingDirectory + "/" + argDirectoryPath;
        if (referDirectory != null) {
            if (!referDirectory.trim().isEmpty()) {
                FlyCRUDPathManager.onCreateDirs(referDirectory);
            }
        }
        //System.out.println("FILE_DIRECTORY: " + directory);
        return this;
    }

    public FlyImageManager withName(String argName) {
        name = argName;
        referFullFilePath = referDirectory + "/" + argName;
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

    public FlyImageManager withQuality(int argQuality) {
        quality = argQuality;
        return this;
    }

    public FlyImageManager withCompressFormat(Bitmap.CompressFormat argCompressFormat) {
        compressFormat = argCompressFormat;
        return this;
    }

    public FlyImageManager withResize() {
        if (width > 0 && height > 0) {
            bitmap = getResizedBitmap(bitmap, width, height);
        } else if (width > 0 && height <= 0) {
            bitmap = getResizedBitmapByWidth(bitmap, width);
        } else if (width <= 0 && height > 0) {
            bitmap = getResizedBitmapByHeight(bitmap, height);
        }
        return this;
    }

    public String getWorkingFilePath(String argFilePath) {
        return workingDirectory + "/" + argFilePath;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public String getReferFilePath(String argFilePath) {
        return referDirectory + "/" + argFilePath;
    }

    public String getReferDirectory() {
        return referDirectory;
    }

    public String getReferFullFilePath() {
        return referFullFilePath;
    }

    public String getNewImageName(String argName, ImageFormat argImageFormat) {
        return argName + "-" + fileTimeStamp + getRandom(1111, 9999) + "." + argImageFormat.getValue();
    }

    public int getRandom(int argMinValue, int argMaxValue) {
        int retVal = 0;
        int rangeValue = argMaxValue - argMinValue + 1;
        Random random = new Random();
        retVal = random.nextInt(rangeValue) + argMinValue;
        return retVal;
    }

    public void write() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(compressFormat, quality, byteArrayOutputStream);
            //File file = new File(referDirectory, name);
            File file = new File(referFullFilePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
            log("Successfully write image with image file name: " + referFullFilePath);
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    private void copy(ImageView argImageView, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap = getResizedBitmap(bitmap, width, height);
            /*argImageView.setDrawingCacheEnabled(true);
            argImageView.buildDrawingCache(true);
            Bitmap bitmap = argImageView.getDrawingCache();*/
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(argCompressFormat, argQuality, byteArrayOutputStream);
            File file = new File(referFullFilePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
            log("Successfully write image");
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    private void copy(String argSourcePath, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(referFullFilePath, bmOptions);
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

    private void copy(Bitmap argBitmap, int argQuality, Bitmap.CompressFormat argCompressFormat) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            argBitmap.compress(argCompressFormat, argQuality, byteArrayOutputStream);
            File file = new File(referFullFilePath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            //Log.e(TAG, "copyImageToInternalStorage: " + e.getMessage());
            log("Error: " + e.getMessage());
        }
    }

    public Bitmap getBitmapFromPath(String argFileName) {
        File file = new File(argFileName);
        Bitmap bitmap = null;
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            //imageView.setImageBitmap(myBitmap);
        }
        return bitmap;
    }

    public Drawable getBitmapToDrawable(Bitmap argBitmap) {
        return new BitmapDrawable(context.getResources(), argBitmap);
    }

    public Bitmap getBitmapFromView(ImageView argImageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        /*argImageView.setDrawingCacheEnabled(true);
        argImageView.buildDrawingCache(true);
        Bitmap bitmap = argImageView.getDrawingCache();*/
        return bitmap;
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

    public Bitmap getResizedBitmapByWidth(Bitmap argBitmap, int argTargetWidth) {
        if (argTargetWidth > 0) {
            int width = argBitmap.getWidth();
            int height = argBitmap.getHeight();
            float aspectRatio = width / height;
            int targetWidth = argTargetWidth;
            int targetHeight = Math.round(width / aspectRatio);

            int finalWidth = 0;
            int finalHeight = 0;


            if (width > height) {
                // landscape
                float ratio = (float) width / targetWidth;
                finalWidth = targetWidth;
                finalHeight = (int) (height / ratio);
            } else if (height > width) {
                // portrait
                float ratio = (float) height / targetHeight;
                finalWidth = (int) (width / ratio);
                finalHeight = targetHeight;
            } else {
                // square
                float ratio = (float) width / targetWidth;
                finalWidth = targetWidth;
                finalHeight = (int) (height / ratio);
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(argBitmap, finalWidth, finalHeight, true);
            //Bitmap background = Bitmap.createBitmap((int)finalWidth, (int)finalHeight, Bitmap.Config.ARGB_8888);
            log("Image size: " + width + " - " + height);
            log("Final size: " + finalWidth + " - " + finalHeight);
            return bitmap;

        } else {
            log("ERROR_IMAGE_SIZE");
            return argBitmap;
        }
    }

    public Bitmap getResizedBitmapByHeight(Bitmap argBitmap, int argTargetHeight) {
        if (argTargetHeight > 0) {
            int width = argBitmap.getWidth();
            int height = argBitmap.getHeight();
            float aspectRatio = width / height;
            int targetWidth = Math.round(height / aspectRatio);
            int targetHeight = argTargetHeight;

            int finalWidth = 0;
            int finalHeight = 0;


            if (width > height) {
                // landscape
                float ratio = (float) width / targetHeight;
                finalWidth = targetWidth;
                finalHeight = (int) (height / ratio);
            } else if (height > width) {
                // portrait
                float ratio = (float) height / targetHeight;
                finalWidth = (int) (width / ratio);
                finalHeight = targetHeight;
            } else {
                // square
                float ratio = (float) height / targetHeight;
                finalWidth = (int) (width / ratio);
                finalHeight = targetHeight;
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(argBitmap, finalWidth, finalHeight, true);
            //Bitmap background = Bitmap.createBitmap((int)finalWidth, (int)finalHeight, Bitmap.Config.ARGB_8888);
            log("Image size: " + width + " - " + height);
            log("Final size: " + finalWidth + " - " + finalHeight);
            return bitmap;

        } else {
            log("ERROR_IMAGE_SIZE");
            return argBitmap;
        }
    }


    public String getEncodedBitmap(Bitmap argBitmap) {
        //ImageView imageView = null; // new ImageView();
        /*argImageView.buildDrawingCache();
        Bitmap bitmap = argImageView.getDrawingCache();*/
        ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
        argBitmap.compress(Bitmap.CompressFormat.PNG, 100, baOutStream); //bm is the bitmap object
        byte[] byteArray = baOutStream.toByteArray();

        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
        //http://stackoverflow.com/questions/16446841/android-send-image-file-to-the-server-db
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

    private void log(String argMessage) {
        if (isDebug) {
            System.out.println();
            System.out.println("FlyImageManager_DEBUG_LOG_PRINT: " + argMessage);
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
//https://softwareengineering.stackexchange.com/questions/267534/string-args-vs-string-args
//https://stackoverflow.com/questions/9048744/string-parameter-in-java
//https://medium.freecodecamp.org/why-you-should-ignore-exceptions-in-java-and-how-to-do-it-correctly-8e95e5775e58
//