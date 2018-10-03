package rz.adept.flyimagepicker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rz.flyimagepicker.FlyImageManager;

public class ActSplash extends AppCompatActivity {
    private Activity activity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        activity = this;
        context = this;
        FlyImageManager flyImageManager = new FlyImageManager(context);
        /*flyImageManager.setRootDirectory("me");
        flyImageManager.setRootCacheDirectory();*/
        flyImageManager.set("");
        /*log(flyImageManager.getFilePath("test"));
        log(flyImageManager.withDirectoryPath("dir1").getFilePath("test_file"));
        log(flyImageManager.withDirectoryPath("dir2").getFilePath("test_file1"));*/
    }

    private void log(String argMessage) {
        System.out.println("DEBUG_LOG_PRINT: " + argMessage);
    }
}
