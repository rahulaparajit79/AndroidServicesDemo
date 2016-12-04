package com.demo.androidservicesdemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
    private EditText editText2;
    private MyService mService;
    private TextView textView;
    private boolean bound = false;
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
////
////        for (int i = 0; i < 15; i++) {
////            try {
////                Thread.sleep(2000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////                System.out.println(e.getMessage());
////            }
////            System.out.println(Thread.currentThread().getName() + "-" + i);
////            if (i == 10) {
////
////                Thread.currentThread().interrupt();
////            }
//
//        }

        btnStartService = (Button) findViewById(R.id.btn_startService);
        btnStopService = (Button) findViewById(R.id.btn_stopService);
        btnBindService = (Button) findViewById(R.id.btn_bindService);
        btnMsgButton = (Button) findViewById(R.id.btn_showServiceMsg);
        btnUnbindService = (Button) findViewById(R.id.btn_unbindService);
        editText1 = (EditText) findViewById(R.id.edt_1);
        editText2 = (EditText) findViewById(R.id.edt_2);
        textView = (TextView) findViewById(R.id.txt_view);

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
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (editText2.isFocused()) {
                    editText2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.zoom_in));
                    editText2.setElevation(16);
                } else {
                    editText2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.zoom_out));
                    editText2.setElevation(2);
                }
            }
        });//endregion

        //region

        // endregion

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
                            Log.i(TAG,"jsonResult");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    newtext.append(stringjson);
                                    textView.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            textView.setText(newtext);
                                            Log.i(TAG,"text set");
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

    }
}
