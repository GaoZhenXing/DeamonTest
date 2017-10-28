package com.jason.deamontest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaCasException;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jason on 2017/10/24.
 */

public class LocalService extends Service {

    LocalServiceBinder localBinder;
    LocalServiceConn localServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();
        if (localBinder == null) {
            localBinder = new LocalServiceBinder();
        }
        if (localServiceConn == null) {
            localServiceConn = new LocalServiceConn();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), localServiceConn, Context.BIND_IMPORTANT);
        startTimer();
        //设置为前台进程
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        builder.setContentText("本地服务线程保护");
        builder.setContentTitle("本地服务正在运行。。。");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentInfo("info");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        startForeground(startId, builder.build());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return localBinder;
    }


    class LocalServiceBinder extends IServiceAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    //本地服务连接
    class LocalServiceConn implements ServiceConnection {

        //            服务已连接
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //当服务连接断开时，去唤醒远程服务
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), localServiceConn, Context.BIND_IMPORTANT);
        }


    }

    Timer timer;
    TimerTask timerTask;
    int count = 0;

    void startTimer() {
        if (timer == null) {
            timer = new Timer();
            initTimerTask();
            timer.schedule(timerTask, 1000, 1000);
        }
    }

    private void initTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("LocalService", "run: ++++++++" + (count++));

            }
        };
    }

    void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
