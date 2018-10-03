package rz.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DirFileManager {
    //cache
    private Context context;
    private String fileTimeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    public static final int READ_WRITE_STORAGE_REQUEST = 1111;
    private String rootDirectory;
    private String rootCacheDirectory;
    private boolean isDebug = false;
    public ImageManager imageManager;

    public DirFileManager(Context argContext) {
        context = argContext;
        if (BuildConfig.DEBUG) {
            isDebug = true;
        }
    }

    public ImageManager getImageManager() {
        return new ImageManager();
    }

    public CameraManager getCameraManager() {
        return new CameraManager();
    }

    public GalleryManager getGalleryManager() {
        return new GalleryManager();
    }

    public void init(ImageManager argImageManager) {
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, READ_WRITE_STORAGE_REQUEST);
                //sysLog("ERROR_PERMISSION: " + PERMISSIONS.toString());
                sysLog("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                return;
            }
        }
        rootDirectory = getRootDir();
        makeDir(argImageManager.getDirectory());
        sysLog("DIRECTORY: " + argImageManager.getDirectory());
    }

    public void onRequestPermissionsResult(int argRequestCode, int[] argGrantResults) {
        if (argRequestCode == READ_WRITE_STORAGE_REQUEST) {
            if (argGrantResults.length > 0 && argGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //do here
            } else {
            }
        }
    }

    public class CameraManager {
        public static final int CAMERA_REQUEST = 4444;
        private String cameraCacheDirectory = "";
        private String cameraCashFilePath = "";

        public CameraManager() {
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= 23) {
                if (!hasPermissions(PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, CAMERA_REQUEST);
                    sysLog("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                    return;
                }
            }
        }

        public void open() {
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= 23) {
                if (!hasPermissions(PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, CAMERA_REQUEST);
                    sysLog("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                    return;
                }
            }
            /*cameraCacheDirectory = getFileDir("cache", true);
            makeDir(cameraCacheDirectory);
            cameraCashFilePath = "camera-" + fileTimeStamp + ".png";
            File file = new File(cameraCacheDirectory, cameraCashFilePath);
            cameraCashFilePath = file.toString();
            Uri uri = Uri.fromFile(file);*/
            //Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",fileImagePath);
            /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //File file = new File(camDir, String.valueOf(System.currentTimeMillis()) + ".png");*/
            //((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            //cameraIntent.putExtra("return-data", true);
            ((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

        public void onRequestPermissionsResult(int argRequestCode, int[] argGrantResults) {
            if (argRequestCode == CAMERA_REQUEST) {
                if (argGrantResults.length > 0 && argGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                }
            }
        }

        public Bitmap onActivityResult(int argRequestCode, int argResultCode, Intent argData) {
            Bitmap bitmap = null;
            if (argRequestCode == CAMERA_REQUEST && argResultCode == Activity.RESULT_OK) {
                Bundle bundle = argData.getExtras();
                if (bundle != null) {
                    //Bitmap bitmap = (Bitmap) argData.getExtras().get("data");
                    /*Bundle bundle = argData.getExtras();
                    bitmap = bundle.getParcelable("data");*/
                    //bitmap = (Bitmap) argData.getExtras().get("data");
                    bitmap = (Bitmap) bundle.get("data");
                } else {
                    bitmap = getBitmapFromPath(cameraCashFilePath);
                }
            }
            deleteFile(cameraCashFilePath);
            return bitmap;
        }
    }

    public class GalleryManager {
        public static final int GALLERY_REQUEST = 8888;
        private String galleryCacheDirectory = "";
        private String galleryCashFilePath = "";

        public GalleryManager() {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (Build.VERSION.SDK_INT >= 23) {
                if (!hasPermissions(PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, GALLERY_REQUEST);
                    sysLog("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                    return;
                }
            }
        }

        public void onRequestPermissionsResult(int argRequestCode, int[] argGrantResults) {
            if (argRequestCode == GALLERY_REQUEST) {
                if (argGrantResults.length > 0 && argGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                }
            }
        }

        public void open() {
            //System.out.println("OPEN_REQUEST: ");
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= 23) {
                if (!hasPermissions(PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, GALLERY_REQUEST);
                    sysLog("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                    return;
                }
            }
            //System.out.println("OPEN_REQUEST_PASS: ");
            /*galleryCacheDirectory = getFileDir("cache", true);
            makeDir(galleryCacheDirectory);
            galleryCashFilePath = "gallery-" + fileTimeStamp + ".png";
            File file = new File(galleryCacheDirectory, galleryCashFilePath);
            galleryCashFilePath = file.toString();*/
            //sysLog("LLLLL: " + galleryFilePath);
            //Uri uri = Uri.fromFile(file);
            /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //File file = new File(camDir, String.valueOf(System.currentTimeMillis()) + ".png");*/
            //((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            //galleryIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            ((Activity) context).startActivityForResult(Intent.createChooser(galleryIntent, "Select Image From Gallery"), GALLERY_REQUEST);
        }

        public Bitmap onActivityResult(int argRequestCode, int argResultCode, Intent argData) {
            Bitmap bitmap = null;
            if (argRequestCode == GALLERY_REQUEST && argResultCode == Activity.RESULT_OK) {
                //Bundle bundle = argData.getExtras();
                if (argData != null) {
                    //Bitmap bitmap = (Bitmap) argData.getExtras().get("data");
                    /*Bundle bundle = argData.getExtras();
                    bitmap = bundle.getParcelable("data");*/
                    //bitmap = (Bitmap) argData.getExtras().get("data");
                    //bitmap = (Bitmap) bundle.get("data");
                    Uri selectedImage = argData.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver()
                        .query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    sysLog("PICTURE_PATH_BROWSED: " + picturePath);
                    cursor.close();
                    bitmap = getBitmapFromPath(picturePath);
                } else {
                    bitmap = getBitmapFromPath(galleryCashFilePath);
                    sysLog("PICTURE_PATH_ELSE: " + galleryCashFilePath);
                }
            }
            deleteFile(galleryCashFilePath);
            return bitmap;
        }
    }

    private boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void makeDir(String argDirectoryPath) {
        File file = new File(argDirectoryPath);
        if (!isDirExists(file)) {
            file.mkdirs();
            //sysLog("File not exists");
        } else {
            sysLog("File exists");
        }
    }

    private String getRootDir() {
        return Environment.getExternalStorageDirectory() + "";
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
            sysLog("Error: " + e.getMessage());
        } catch (IOException e) {
            //e.printStackTrace();
            sysLog("Error: " + e.getMessage());
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                //e.printStackTrace();
                sysLog("Error: " + e.getMessage());
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

    public Bitmap getBitmapFromView(ImageView argImageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        /*argImageView.setDrawingCacheEnabled(true);
        argImageView.buildDrawingCache(true);
        Bitmap bitmap = argImageView.getDrawingCache();*/
        return bitmap;
    }

    public Bitmap getResizedBitmap(String argImagePath, int argTargetWidth, int argTargetHeight) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(argImagePath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((argTargetWidth > 0) || (argTargetHeight > 0)) {
            scaleFactor = Math.min(photoWidth / argTargetWidth, photoHeight / argTargetHeight);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(argImagePath, bmOptions);
    }

    /*public Bitmap getResizedBitmap(Bitmap argBitmap, int argNewWidth, int argNewHeight) {
        int width = argBitmap.getWidth();
        int height = argBitmap.getHeight();
        float scaleWidth = ((float) argNewWidth) / width;
        float scaleHeight = ((float) argNewHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(argBitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }*/

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

    public String getByteSizeOf(Bitmap argBitmap) {
        long bytes = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bytes = argBitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bytes = argBitmap.getByteCount();
        } else {
            bytes = argBitmap.getRowBytes() * argBitmap.getHeight();
        }
        return calculateProperFileSize(bytes);
    }

    private String calculateProperFileSize(long argBytes) {
        //System.out.println("Original file size: " + argBytes);
        String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String sizeToReturn = "";// = FileUtils.byteCountToDisplaySize(bytes), unit = "";
        Double bytes = argBytes * 1.0 / 8;
        int index = 0;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        //long sizeOf = 1024 * (index - 1);
        //System.out.println("Index file size: " + sizeOf);
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        String formByte = formatter.format(bytes);
        /*System.out.println("Systematic file size: " + formByte + " " + fileSizeUnits[index]);
        sizeToReturn = String.valueOf(formByte) + " " + fileSizeUnits[index];*/
        return formByte;
    }

    public String getEncodeImage(ImageView argImageView) {
        //ImageView imageView = null; // new ImageView();
        argImageView.buildDrawingCache();
        Bitmap bitmap = argImageView.getDrawingCache();
        ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baOutStream); //bm is the bitmap object
        byte[] byteArray = baOutStream.toByteArray();

        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
        //http://stackoverflow.com/questions/16446841/android-send-image-file-to-the-server-db
    }

    public String getEncodeImage(Bitmap argBitmap) {
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

    public String getFileDir() {
        return new File(getRootDir(), context.getPackageName()).toString();
    }

    public String getFileDir(String argFilePath, boolean argIsPackage) {
        File file = null;
        file = new File(getRootDir(), argFilePath);
        if (argIsPackage) {
            file = new File(getRootDir(), context.getPackageName() + "/" + argFilePath);
        }
        return file.toString();
    }

    public void sysLog(String argMsg) {
        if (isDebug) {
            System.out.println();
            System.out.println("|----|--------DEBUG_LOG: " + argMsg);
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

    public class ImageManager {
        private String directory;
        private int width = 0;
        private int height = 0;
        private Bitmap bitmap;
        private String name = "";
        private int quality = 100;
        private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;

        public ImageManager() {
            //directory = getRootDir() + "/" + context.getPackageName() + "/" + DIR_PROFILE_IMAGES;
        }

        public ImageManager withDirectoryPath(String argDirectoryPath) {
            directory = getRootDir() + "/" + context.getPackageName() + "/" + argDirectoryPath;
            //System.out.println("FILE_DIRECTORY: " + directory);
            return this;
        }
        public String getFullPath(String argFilePath) {
            return directory + "/" + argFilePath;
        }

        public ImageManager withWidth(int argWidth) {
            width = argWidth;
            return this;
        }

        public ImageManager withHeight(int argHeight) {
            height = argHeight;
            return this;
        }

        public String getDirectory() {
            return this.directory;
        }

        public ImageManager withImageView(ImageView argImageView) {
            bitmap = null;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) argImageView.getDrawable();
            bitmap = bitmapDrawable.getBitmap();
            /*argImageView.setDrawingCacheEnabled(true);
            argImageView.buildDrawingCache(true);
            Bitmap bitmap = argImageView.getDrawingCache();*/
            return this;
        }

        public ImageManager withBitmap(Bitmap argBitmap) {
            bitmap = null;
            bitmap = argBitmap;
            return this;
        }

        public ImageManager withName(String argName) {
            name = argName;
            return this;
        }

        public ImageManager withQuality(int argQuality) {
            quality = argQuality;
            return this;
        }

        public ImageManager withCompressFormat(Bitmap.CompressFormat argCompressFormat) {
            compressFormat = argCompressFormat;
            return this;
        }

        public ImageManager withResize() {
            bitmap = getResizedBitmap(bitmap, width, height);
            return this;
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
                sysLog("Error: " + e.getMessage());
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
                sysLog("Error: " + e.getMessage());
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
                sysLog("Error: " + e.getMessage());
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
                sysLog("Error: " + e.getMessage());
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
                sysLog("ERROR_IMAGE_SIZE");
                return argBitmap;
            }
        }

        public String getNewName(String argName, ImageFormat argImageFormat) {
            return argName + "-" + fileTimeStamp + "." + argImageFormat.getValue();
        }
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
    /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_drawable_image);
    //Setting up converted bitmap image inside imageview.
    image.setImageBitmap(bitmap);*/
}
