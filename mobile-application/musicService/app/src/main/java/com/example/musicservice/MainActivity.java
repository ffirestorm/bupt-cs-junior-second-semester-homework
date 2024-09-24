package com.example.musicservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.musicservice.MyMusicService.ServiceBinder;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private SeekBar processBar;     // 可以拖拉的进度条
    private int musicProcess = 0;   // 音乐的进度
    private boolean isMusicStart = false;
    private Timer musicTimer;

    private ServiceBinder binder;
    private boolean isPause = true;
    private Button playPauseButton;
    TextView tv_1;
    TextView tv_l;
    TextView tv_r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processBar = (SeekBar)findViewById(R.id.seekbar) ;
        tv_1 = (TextView)findViewById(R.id.tv_1);
        tv_l = (TextView)findViewById(R.id.time_l);
        tv_r = (TextView)findViewById(R.id.time_r);
        tv_1.setText("停止播放。。。");
        playPauseButton = (Button)findViewById(R.id.playPauseButton);
        // 建立服务连接
        PlayerConnection serviceConnection = new PlayerConnection();
        bindService(new Intent(this,MyMusicService.class), serviceConnection,BIND_AUTO_CREATE);

        //  进度条处理
        processBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicProcess = progress;
                // 可以顺便设置一下进度显示
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(musicTimer!=null) musicTimer.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binder.set_progress(musicProcess);
                restartTimer(200);
            }
        });

        reset();
    }

    private void reset(){
        if(musicTimer!=null) musicTimer.cancel();
        isMusicStart=false;
        isPause = true;
        processBar.setProgress(0);
        musicProcess=0;
        tv_1.setText("新年好 停止");
        tv_l.setText("00:00");
    }
    public void play_pause_onclick(View view){
        if(isPause){
            play_onclick(view);
            playPauseButton.setText("暂停");
        }else {
            pause_onclick(view);
            playPauseButton.setText("播放");
        }
        isPause = !isPause;
    }

    public void play_onclick(View view)
    {
        Intent intent = new Intent(this,MyMusicService.class);
        intent.putExtra("action","play");
        startService(intent);

        tv_1.setText("新年好 播放中");
        if(!isMusicStart){
            restartTimer(100);
            isMusicStart = true;
        }
    }

    public void stop_onclick(View view)
    {
        Intent intent = new Intent(this,MyMusicService.class);

        intent.putExtra("action","stop");

        startService(intent);
        playPauseButton.setText("播放");
        reset();

    }
    public void pause_onclick(View view)
    {
        musicTimer.cancel();

        Intent intent = new Intent(this,MyMusicService.class);
        intent.putExtra("action","pause");
        startService(intent);
        tv_1.setText("新年好 暂停中");


    }
    public void exit_onclick(View view)
    {
        stop_onclick(view);
        finish();
    }

    private void restartTimer(int delay){
        if(musicTimer != null) musicTimer.cancel();
        musicTimer = new Timer();
        musicTimer.schedule(new ProcessTask(),delay,200);
    }

    class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder = (ServiceBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            binder = null;
        }

    }
    class ProcessTask extends TimerTask{

        @Override
        public void run() {
            if(binder == null || !isMusicStart)
                return;
            musicProcess = binder.get_progress();
            processBar.setProgress(musicProcess);
            tv_l.setText(binder.get_current_str());
            tv_r.setText(binder.get_duration_str());
            if(binder.is_complete()){
                this.cancel();
                stop_onclick(null);
            }
        }
    }
}