package com.nat.device_screen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import java.util.HashMap;

/**
 * Created by xuqinchao on 17/2/7.
 * Copyright (c) 2017 Nat. All rights reserved.
 */

public class HLScreenModule {
    private static final String ANY = "any";
    private static final String PORTRAIT_PRIMARY = "portrait-primary";
    private static final String PORTRAIT_SECONDARY = "portrait-secondary";
    private static final String LANDSCAPE_PRIMARY = "landscape-primary";
    private static final String LANDSCAPE_SECONDARY = "landscape-secondary";
    private static final String PORTRAIT = "portrait";
    private static final String LANDSCAPE = "landscape";

    private Context mContext;
    private static volatile HLScreenModule instance = null;

    private HLScreenModule(Context context){
        mContext = context;
    }

    public static HLScreenModule getInstance(Context context) {
        if (instance == null) {
            synchronized (HLScreenModule.class) {
                if (instance == null) {
                    instance = new HLScreenModule(context);
                }
            }
        }

        return instance;
    }

    public void info(HLModuleResultListener listener){
        HashMap<String, Object> result = new HashMap<>();
        result.put("height", HLUtil.getScreenHeight(mContext));
        result.put("width", HLUtil.getScreenWidth(mContext));
        result.put("scale", HLUtil.getDensity(mContext));
        result.put("dpiX", (int)HLUtil.getScreenDpiX(mContext));
        result.put("dpiY", (int)HLUtil.getScreenDpiY(mContext));
        listener.onResult(result);
    }

    public void getBrightness(HLModuleResultListener listener){
        try {
            int screenBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float brightness = screenBrightness / 255.0f;
            HashMap<String, Float> result = new HashMap<>();
            result.put("brightness", brightness);
            listener.onResult(result);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            listener.onResult(HLUtil.getError(e.getMessage(), -1));
        }
    }

    public void getOrientation(HLModuleResultListener listener){
        String orientation;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = "landscape";
        } else {
            orientation = "portrait";
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("orientation", orientation);
        listener.onResult(result);
    }

    public void lockOrientation(Activity activity, String orientation, HLModuleResultListener listener){
        if (activity == null || listener == null) return;
        if (orientation.equals(ANY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else if (orientation.equals(LANDSCAPE_PRIMARY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation.equals(PORTRAIT_PRIMARY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation.equals(LANDSCAPE)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if (orientation.equals(PORTRAIT)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (orientation.equals(LANDSCAPE_SECONDARY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else if (orientation.equals(PORTRAIT_SECONDARY)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        getOrientation(listener);
    }

    public void unlockOrientation(Activity activity, HLModuleResultListener listener){
        if (activity == null || listener == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getOrientation(listener);
    }

    private void setScreenMode(int value) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, value);
    }

    private void setBrightness(Context context, float value) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

//        // 如果当前的屏幕亮度调节调节模式为自动调节，则改为手动调节屏幕亮度
        if(screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
            setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        // 保存设置的屏幕亮度值
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);
    }
}
