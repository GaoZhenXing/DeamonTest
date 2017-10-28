package com.jason.deamontest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jason on 2017/10/25.
 */

public class RemoteService extends Service {

    RemoteServiceBinder remoteServiceBinder;
    RemoteServiceConn remoteServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();
        if (remoteServiceBinder == null) {
            remoteServiceBinder = new RemoteServiceBinder();
        }
        if (remoteServiceConn == null) {
            remoteServiceConn = new RemoteServiceConn();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class)
                , remoteServiceConn, Context.BIND_IMPORTANT);
        startTimer();
        //设置为前台进程
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setVibrate(null);
        builder.setContentText("远程服务线程保护");
        builder.setContentTitle("远程服务正在运行。。。");
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
        return remoteServiceBinder;
    }

    class RemoteServiceBinder extends IServiceAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    class RemoteServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //当服务连接断开时，去唤醒本地服务
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), remoteServiceConn, Context.BIND_IMPORTANT);
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
                Log.d("RemoteService", "run: ++++++++" + (count++));

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
