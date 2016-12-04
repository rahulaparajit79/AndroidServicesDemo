package com.demo.androidservicesdemo.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.demo.androidservicesdemo.activity.DownloadData;
import com.demo.androidservicesdemo.activity.IJsonResponse;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Rahul on 11/23/2016.
 */

public class MyService extends Service {

    private final IBinder iBinder = new LocalBinder();

    private final Random mGenerator = new Random();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("MyService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("MyService onBind");
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("MYService onStartCommand");
        startMusic();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startMusic() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("/storage/emulated/0/Download/hearttouch.mp3");
            mediaPlayer.prepare();
            //mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(100, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });
    }

    /**
     * method for clients
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("MyService onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void fetchServerData(final String url, final IJsonResponse iResponse) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (checkConnection()) {
                    new DownloadData(iResponse).execute(url);
                } else {
                    AlertDialog.Builder adialog = new AlertDialog.Builder(MyService.this);
                    adialog.setMessage("No internet connection");
                    adialog.setTitle("Message");
                    adialog.setCancelable(true);
                    adialog.show();
                }
            }
        });
        Toast.makeText(this, "Fetching data in service", Toast.LENGTH_LONG).show();
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return true;
        } else {
            // display error
            return false;
        }

    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }
}
