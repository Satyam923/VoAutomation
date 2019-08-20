package e.satyam.voautomation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button speak;
    TextView msg;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speak=findViewById(R.id.speak);
        msg=findViewById(R.id.msg);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Now...");
                startActivityForResult(intent,1);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1)
        {
            ArrayList<String> arrayList= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            msg.setText(arrayList.get(0));

            /*//to share msg to social media
            Intent share=new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT,arrayList.get(0));
            share.setType("text/plain");
            startActivity(Intent.createChooser(share,"Share via"));*/
            switch (arrayList.get(0))
            {
                case "open camera":
                    Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(camera);
                    break;
                case "open Wi-Fi":
                    WifiManager wm=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                    if (!wm.isWifiEnabled())
                    {
                        wm.setWifiEnabled(true);
                    }
                    break;
                case "close Wi-Fi":
                    WifiManager w=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                    if (w.isWifiEnabled())
                    {
                        w.setWifiEnabled(false);
                    }
                    break;

                case "call Kajal":
                    callphone();
                    break;
                case "open gallery":
                    Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery,2);
                    break;
                case "open music":
                    Intent music=new Intent(Intent.ACTION_GET_CONTENT);
                    music.setType("audio/*");
                    startActivityForResult(music,2);
                    break;
                case "open video":
                    Intent video=new Intent(Intent.ACTION_GET_CONTENT);
                    video.setType("video/*");
                    startActivityForResult(video,2);
                    break;
                case "share app":
                    Intent share=new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT,"Please download this"+"\n"+"https://play.google.com/store/apps/details?id=com.beebom.app.beebom");
                    share.setType("text/plain");
                    startActivity(Intent.createChooser(share,"share via"));
                    break;
                case "play music":

                    mediaPlayer=MediaPlayer.create(this,R.raw.hornblow);
                    mediaPlayer.start();
                    break;
                case "open torch":
                    final boolean hasCameraFlash=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    boolean isEnable= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.CAMERA},
                            2);

                    CameraManager cameraManager = (CameraManager)
                            getSystemService(Context.CAMERA_SERVICE);

                    try {
                        String cameraId = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraId, true);

                    } catch (CameraAccessException e) {
                    }

                    break;
            }
        }

    }

    private void callphone() {

        //to check the user mobile version is greater than and equal to marshmallow

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
            {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE))
                {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                }
            }
            else{
                Intent intent=new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:8447115903"));
                startActivity(intent);
            }
        }
        else {
            Intent intent=new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:8447115903"));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                callphone();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}