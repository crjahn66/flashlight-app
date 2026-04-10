package com.flashlight;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class FlashlightModule extends ReactContextBaseJavaModule {
    private CameraManager cameraManager;
    private String cameraId;
    private int currentBrightness = 100;

    public FlashlightModule(ReactApplicationContext reactContext) {
        super(reactContext);
        cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            Log.e("FlashlightModule", "Error getting camera ID", e);
        }
    }

    @Override
    public String getName() {
        return "FlashlightModule";
    }

    @ReactMethod
    public void turnOn(Promise promise) {
        try {
            cameraManager.setTorchMode(cameraId, true);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void turnOff(Promise promise) {
        try {
            cameraManager.setTorchMode(cameraId, false);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void setBrightness(int brightness, Promise promise) {
        try {
            currentBrightness = brightness;
            // Note: Android Camera2 API doesn't support brightness levels for torch mode
            // This stores the value for UI display
            promise.resolve(brightness);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }
}
