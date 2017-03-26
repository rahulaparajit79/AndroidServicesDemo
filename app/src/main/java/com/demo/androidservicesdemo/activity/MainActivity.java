package com.demo.androidservicesdemo.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.androidservicesdemo.R;
import com.demo.androidservicesdemo.services.MyService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final String ServerURL = "https://jsonplaceholder.typicode.com/todos";//  /albums, /photos, /comments
    StringBuilder newtext = new StringBuilder();
    private Button btnStartService;
    private Button btnStopService;
    private Button btnBindService;
    private Button btnMsgButton;
    private Button btnUnbindService;
    private Thread t;
    private EditText editText1;
    private Intent intentMyService;
    private Button button2;
    private MyService mService;
    private TextView textView;
    private boolean bound = false;
    private Button btnplay1;
    private Button btnplay2;
    private Button btnplay3;
    private Button btnplay4;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Thread musicThread;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected");
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button) findViewById(R.id.btn_startService);
        btnStopService = (Button) findViewById(R.id.btn_stopService);
        btnBindService = (Button) findViewById(R.id.btn_bindService);
        btnMsgButton = (Button) findViewById(R.id.btn_showServiceMsg);
        btnUnbindService = (Button) findViewById(R.id.btn_unbindService);
        editText1 = (EditText) findViewById(R.id.edt_1);
        button2 = (Button) findViewById(R.id.edt_2);
        textView = (TextView) findViewById(R.id.txt_view);
        btnplay1 = (Button) findViewById(R.id.btn_play1);
        btnplay2 = (Button) findViewById(R.id.btn_play2);
        btnplay3 = (Button) findViewById(R.id.btn_play3);
        btnplay4 = (Button) findViewById(R.id.btn_play4);

        //region editText.setOnFocusChangeListener
        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (editText1.isFocused()) {
                    editText1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            android.support.v7.appcompat.R.anim.abc_fade_in));
                    editText1.setElevation(16);
                } else {
                    editText1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            android.support.v7.appcompat.R.anim.abc_fade_out));
                    editText1.setElevation(2);
                }
            }
        });
        button2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (button2.isFocused()) {
                    button2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.zoom_in));
                    button2.setElevation(2);
                } else {
                    button2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.zoom_out));
                    button2.setElevation(16);
                }
            }
        });//endregion


        //region btn unbind service
        btnUnbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceConnection != null)
                    unbindService(serviceConnection);
                textView.setText("");
            }
        });
        //endregion

        btnMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null)
                    mService.fetchServerData(ServerURL, new IJsonResponse() {
                        @Override
                        public void jsonResult(final String stringjson) {
                            Log.i(TAG, "jsonResult");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    newtext.append(stringjson);
                                    textView.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            textView.setText(newtext);
                                            Log.i(TAG, "text set");
                                        }
                                    });
                                }
                            }).start();

                        }
                    });
            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentMyService = new Intent(MainActivity.this, MyService.class);
                startService(intentMyService);


            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentMyService != null)
                    stopService(intentMyService);
            }
        });


        btnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                if (bound) {
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    int num = mService.getRandomNumber();
                    Toast.makeText(MainActivity.this, "number: " + num, Toast.LENGTH_SHORT).show();
                }

            }
        });


        setBtnPlayListners();

    }

    private void setBtnPlayListners() {
        btnplay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnplay2.getText().toString().equals("Play")) {
                    String url = "http://app.octaveobsession.com/music/3/bollywood/iktara.mp3";
                    url = url.replaceAll(" ", "%20");
                    setSongToPlayer(url);
                    btnplay1.setText("Stop");
                    btnplay2.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                } else {
                    mediaPlayer.stop();
                    btnplay2.setText("Play");
                    btnplay1.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                }

            }
        });

        btnplay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnplay2.getText().toString().equals("Play")) {
                    String url = "http://app.octaveobsession.com/music/3/bollywood/Alisha Chinnoy - Tinka Tinka.mp3";
                    url = url.replaceAll(" ", "%20");
                    setSongToPlayer(url);
                    btnplay2.setText("Stop");
                    btnplay1.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                } else {
                    mediaPlayer.stop();
                    btnplay2.setText("Play");
                    btnplay1.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                }

            }
        });

        btnplay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnplay3.getText().toString().equals("Play")) {
                    String url = "http://app.octaveobsession.com/music/3/bollywood/Atif Aslam - Woh Lamhe.mp3";
                    url = url.replaceAll(" ", "%20");
                    setSongToPlayer(url);
                    btnplay3.setText("Stop");
                    btnplay2.setText("Play");
                    btnplay1.setText("Play");
                    btnplay4.setText("Play");
                } else {
                    mediaPlayer.stop();
                    btnplay2.setText("Play");
                    btnplay1.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                }
            }
        });

        btnplay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnplay4.getText().toString().equals("Play")) {
                    String url = "http://app.octaveobsession.com/music/3/bollywood/Lucky Ali - Hairat - wwwSongsPK.mp3";
                    url = url.replaceAll(" ", "%20");
                    setSongToPlayer(url);
                    btnplay4.setText("Stop");
                    btnplay2.setText("Play");
                    btnplay3.setText("Play");
                    btnplay1.setText("Play");
                } else {
                    mediaPlayer.stop();
                    btnplay2.setText("Play");
                    btnplay1.setText("Play");
                    btnplay3.setText("Play");
                    btnplay4.setText("Play");
                }
            }
        });
    }

    private void setSongToPlayer(final String url) {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();

        musicThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    //mediaPlayer.setLooping(true);
                    //mediaPlayer.setVolume(100, 100);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        System.out.println("mp onPrepared");
                    }
                });
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        System.out.println("Buffer " + percent + "%");
                        if (percent > 25) {
                            pd.dismiss();
                            if (!mp.isPlaying()) {
                                System.out.println("mp.start()");
                                mp.start();
                                mp.seekTo(0);
                            }

                        }
                    }
                });


            }
        });
        musicThread.start();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            Toast.makeText(MainActivity.this, "player released", Toast.LENGTH_SHORT).show();
        }
    }
}
