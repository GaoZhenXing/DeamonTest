package com.jason.deamontest;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;

import java.util.List;

/**
 * Jobservice
 * Created by Jason on 2017/10/25.
 */

@SuppressLint("NewApi")
public class JobHandleService extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        //判读服务是否在运行
        boolean isLocalServiceWorking=false;
        boolean isRemoteServiceWorking=false;
        isLocalServiceWorking=isServiceWork(this,"com.jason.deamontest.LocalService");
        isRemoteServiceWorking=isServiceWork(this,"com.jason.deamontest.RemoteService");

        if (isLocalServiceWorking) {
            startService(new Intent(this, LocalService.class));
        }
        if (isRemoteServiceWorking) {
            startService(new Intent(this, RemoteService.class));

        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        sheduleJob(getJobInfo());

        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sheduleJob(getJobInfo());

        return super.onStartCommand(intent, flags, startId);
    }

    private void sheduleJob(JobInfo jobInfo) {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(0x0008, new ComponentName(this, JobHandleService.class));
        builder.setPeriodic(5000);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder.build();
    }

    boolean isServiceWork(Context context, String serviceName) {
        boolean isWorked = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(128);


        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo info : list) {

            if (serviceName.equals(info.service.getClassName().toString())) {
                isWorked = true;
                break;
            }
        }

        return isWorked;
    }
}
