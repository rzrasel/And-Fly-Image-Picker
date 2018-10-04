package rz.adept.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import rz.flyimagepicker.FlyImageManager;

public class ActSplash extends AppCompatActivity {
    private Activity activity;
    private Context context;
    private String rootDirectory;
    private String fileDirectory;
    private String fileFullPath;
    private String newImageName;
    private ImageView sysImageViewSample;
    private ImageView sysImageViewTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        activity = this;
        context = this;
        sysImageViewSample = (ImageView) findViewById(R.id.sysImageViewSample);
        sysImageViewTwo = (ImageView) findViewById(R.id.sysImageViewTwo);
        FlyImageManager flyImageManager = new FlyImageManager(context);
        flyImageManager.setCachePath("cow-prof-images");
        rootDirectory = flyImageManager.getWorkingDirectory();
        newImageName = flyImageManager.getNewImageName("abc", FlyImageManager.ImageFormat.PNG);
        fileDirectory = flyImageManager.getWorkingFilePath(newImageName);
        flyImageManager.withName(newImageName)
                .withImageView(sysImageViewSample)
                .write();
        fileFullPath = flyImageManager.getReferFullFilePath();
        log(fileFullPath);
        Bitmap bitmap = flyImageManager.getBitmapFromPath(fileFullPath);
        if (bitmap != null) {
            sysImageViewTwo.setImageBitmap(bitmap);
        }
    }

    private void log(String argMessage) {
        System.out.println("ActSplash_DEBUG_LOG_PRINT: " + argMessage);
    }
}
