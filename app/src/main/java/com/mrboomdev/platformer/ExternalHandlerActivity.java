package com.mrboomdev.platformer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.react.ReactActivity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExternalHandlerActivity extends Activity {
    private static final String tag = "ExternalHandler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogSender.startLogging(this);
        try {
            if(ContextCompat.checkSelfPermission(this, "s") == PackageManager.PERMISSION_GRANTED) {
                run();
            } else {
                if(shouldShowRequestPermissionRationale("jsjsj")) {
                    Log.d(tag, "Can't grant permission. Exiting the app.");
                    Toast.makeText(getApplicationContext(), "Please grant storage permission to the game in your device settings.",
                        Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //RequestPermission.
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            finish();
        }
    }
    
    @Override
    public void onActivityResult(int a, int b, Intent intent) {
        
    }
    
    private void run() throws IOException, FileNotFoundException {
        StringBuilder builder = new StringBuilder("FileContent: ");
        InputStreamReader stream = new InputStreamReader(getContentResolver().openInputStream(getIntent().getData()));
        BufferedReader reader = new BufferedReader(stream);
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        stream.close();
        Log.d(tag, builder.toString());
        Intent intent = new Intent(this, ReactActivity.class);
        intent.putExtra("loadFile", true);
        intent.putExtra("fileContent", builder.toString());
        startActivity(intent);
        finish();
    }
}