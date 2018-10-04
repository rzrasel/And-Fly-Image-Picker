package rz.adept.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import rz.flyimagepicker.DirFileManager;
import rz.flyimagepicker.FlyCRUDPathManager;
import rz.flyimagepicker.FlyImageManager;
import rz.flyimagepicker.FlyImagePickerManager;
import rz.flyimagepicker.FlyPathManager;

public class ActSplash extends AppCompatActivity {
    private Activity activity;
    private Context context;
    private String rootDirectory;
    private String fileDirectory;
    private String fileFullPath;
    private String newImageName;
    private ImageView sysImageViewSample;
    private ImageView sysImageViewTwo;
    private Button sysBtnGallery;
    private Button sysBtnCamera;
    private FlyImageManager flyImageManager;
    private FlyImagePickerManager flyImagePickerManager;
    private FlyImagePickerManager.CameraManager cameraManager;
    private FlyImagePickerManager.GalleryManager galleryManager;
    private String saveImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        activity = this;
        context = this;
        sysImageViewSample = (ImageView) findViewById(R.id.sysImageViewSample);
        sysImageViewTwo = (ImageView) findViewById(R.id.sysImageViewTwo);
        sysBtnGallery = (Button) findViewById(R.id.sysBtnGallery);
        sysBtnCamera = (Button) findViewById(R.id.sysBtnCamera);
        ///usage
        flyImageManager = new FlyImageManager(context);
        flyImageManager.setCachePath("cow-prof-images");
        flyImagePickerManager = new FlyImagePickerManager(context);
        galleryManager = flyImagePickerManager.getGalleryManager();
        cameraManager = flyImagePickerManager.getCameraManager();
        sysBtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View argView) {
                saveImageName = flyImageManager.getNewImageName("image-name", FlyImageManager.ImageFormat.PNG);
                galleryManager.open();
            }
        });
        sysBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View argView) {
                saveImageName = flyImageManager.getNewImageName("image-name", FlyImageManager.ImageFormat.PNG);
                cameraManager.open();
            }
        });
        ////
    }

    @Override
    protected void onActivityResult(int argRequestCode, int argResultCode, Intent argData) {
        //Bundle bundle = argData.getExtras();
        //System.out.println("BUNDLE: " + bundle.size());
        Bitmap bitmap = null;
        if (argRequestCode == cameraManager.CAMERA_REQUEST) {
            bitmap = cameraManager.onActivityResult(argRequestCode, argResultCode, argData);
            sysImageViewSample.setImageBitmap(bitmap);
            onSaveMediaImage(bitmap);
        } else if (argRequestCode == galleryManager.GALLERY_REQUEST) {
            bitmap = galleryManager.onActivityResult(argRequestCode, argResultCode, argData);
            sysImageViewSample.setImageBitmap(bitmap);
            onSaveMediaImage(bitmap);
        }
        //super.onActivityResult(argRequestCode, argResultCode, argData);
    }

    private void onSaveMediaImage(Bitmap argBitmap) {
        if (argBitmap != null) {
            saveImageName = saveImageName.replaceAll("-(\\s*-)+", "-");
            flyImageManager.withBitmap(argBitmap)
                    .withName(saveImageName)
                    .withQuality(100)
                    .withCompressFormat(Bitmap.CompressFormat.PNG)
                    .withResize()
                    .write();
            argBitmap = null;
            log("IMAGE_FILE_PATH: " + saveImageName);
            fileFullPath = flyImageManager.getReferFullFilePath();
            log(fileFullPath);
            Bitmap bitmap = flyImageManager.getBitmapFromPath(fileFullPath);
            if (bitmap != null) {
                sysImageViewTwo.setImageBitmap(bitmap);
            }
            String temp = new FlyPathManager(context).getRootDirectory(context.getPackageName());
            FlyCRUDPathManager.onCreateDirs(temp);
            FlyCRUDPathManager.onFileCopy(fileFullPath, temp + "/" + saveImageName);
            log(temp);
            //imageView.setImageBitmap(dirFileManager.getBitmapFromPath(imageManager.getFullPath(saveImageName)));
            //String imageStr = dirFileManager.getEncodeImage(dirFileManager.getBitmapFromPath(saveImageName));
            //hashMap.put("prof_img", imageStr);
        } else {
            System.out.println("ERROR_NULL_BITMAP");
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //permissionChecker.onActivityResult(requestCode);
        }
    }*/

    private void usageOne() {
        FlyImageManager flyImageManager = new FlyImageManager(context);
        flyImageManager.setCachePath("cow-prof-images");
        rootDirectory = flyImageManager.getWorkingDirectory();
        newImageName = flyImageManager.getNewImageName("abc", FlyImageManager.ImageFormat.PNG);
        fileDirectory = flyImageManager.getWorkingFilePath(newImageName);
        flyImageManager.withName(newImageName)
                .withImageView(sysImageViewSample)
                .withWidth(200)
                //.withHeight(200)
                .withResize()
                .write();
        fileFullPath = flyImageManager.getReferFullFilePath();
        log(fileFullPath);
        Bitmap bitmap = flyImageManager.getBitmapFromPath(fileFullPath);
        if (bitmap != null) {
            sysImageViewTwo.setImageBitmap(bitmap);
        }
        String temp = new FlyPathManager(context).getRootDirectory(context.getPackageName());
        FlyCRUDPathManager.onCreateDirs(temp);
        FlyCRUDPathManager.onFileCopy(fileFullPath, temp + "/" + newImageName);
        log(temp);
    }

    private void log(String argMessage) {
        System.out.println("ActSplash_DEBUG_LOG_PRINT: " + argMessage);
    }
}
//https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android