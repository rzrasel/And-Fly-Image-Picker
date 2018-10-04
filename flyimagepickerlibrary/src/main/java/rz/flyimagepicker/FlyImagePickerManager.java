package rz.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

public class FlyImagePickerManager {
    private Context context;
    public static boolean isDebug = true;

    public FlyImagePickerManager(Context argContext) {
        context = argContext;
    }

    public CameraManager getCameraManager() {
        return new CameraManager();
    }

    public GalleryManager getGalleryManager() {
        return new GalleryManager();
    }

    public class CameraManager {
        public static final int CAMERA_REQUEST = 4444;
        private String cameraCacheDirectory = "";
        private String cameraCashFilePath = "";

        public CameraManager() {
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= 23) {
                if (!FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
                    FlyPermissionsManager.requestPermissions(context, CAMERA_REQUEST, PERMISSIONS);
                    log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
                    return;
                }
            }
        }

        public void open() {
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= 23) {
                if (FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
                    FlyPermissionsManager.requestPermissions(context, CAMERA_REQUEST, PERMISSIONS);
                    log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
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
                    //bitmap = getBitmapFromPath(cameraCashFilePath);
                    bitmap = null;
                }
            }
            //----deleteFile(cameraCashFilePath);
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
                if (FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
                    FlyPermissionsManager.requestPermissions(context, GALLERY_REQUEST, PERMISSIONS);
                    log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
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
                if (FlyPermissionsManager.hasPermissions(context, PERMISSIONS)) {
                    FlyPermissionsManager.requestPermissions(context, GALLERY_REQUEST, PERMISSIONS);
                    log("ERROR_PERMISSION: " + StringUtils.join(PERMISSIONS, ", "));
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
                    log("PICTURE_PATH_BROWSED: " + picturePath);
                    cursor.close();
                    //bitmap = getBitmapFromPath(picturePath);
                    FlyImageManager flyImageManager = new FlyImageManager(context);
                    bitmap = flyImageManager.getBitmapFromPath(picturePath);
                } else {
                    //bitmap = getBitmapFromPath(galleryCashFilePath);
                    bitmap = null;
                    log("PICTURE_PATH_ELSE: " + galleryCashFilePath);
                }
            }
            //----deleteFile(galleryCashFilePath);
            return bitmap;
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
            System.out.println("FlyCRUDPathManager_DEBUG_LOG_PRINT: " + argMessage);
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