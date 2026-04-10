package com.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;
    private int currentTorchStrength = 1;
    private Button toggleButton;
    private TextView statusText;
    private SeekBar brightnessSeekBar;
    private TextView brightnessLabel;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private HandlerThread cameraThread;
    private Handler cameraHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.toggleButton);
        statusText = findViewById(R.id.statusText);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessLabel = findViewById(R.id.brightnessLabel);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            Toast.makeText(this, "No camera found", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }

        toggleButton.setOnClickListener(v -> toggleFlashlight());

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isFlashlightOn && fromUser) {
                    currentTorchStrength = progress + 1;
                    int brightness = (progress + 1) * 10;
                    brightnessLabel.setText("Brightness: " + brightness + "%");
                    updateTorchBrightness(currentTorchStrength);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brightnessSeekBar.setVisibility(android.view.View.GONE);
        brightnessLabel.setVisibility(android.view.View.GONE);
    }

    private void toggleFlashlight() {
        if (isFlashlightOn) {
            turnOffFlashlight();
        } else {
            enableFlashlight();
        }
    }

    private void enableFlashlight() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            cameraManager.setTorchMode(cameraId, true);
            isFlashlightOn = true;
            updateUI();
            updateTorchBrightness(currentTorchStrength);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTorchBrightness(int strength) {
        // Brightness control via SeekBar uses PWM (pulse width modulation)
        // Flash the torch on/off rapidly to simulate brightness levels
        if (!isFlashlightOn) return;

        // Calculate pulse timing based on strength (1-10 = 10%-100%)
        int onTime = strength * 50;  // 50-500ms on
        int offTime = (11 - strength) * 50;  // 500-50ms off

        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isFlashlightOn) {
                    try {
                        cameraManager.setTorchMode(cameraId, false);
                        cameraHandler.postDelayed(() -> {
                            if (isFlashlightOn) {
                                try {
                                    cameraManager.setTorchMode(cameraId, true);
                                    cameraHandler.postDelayed(this, onTime);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, offTime);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void turnOffFlashlight() {
        isFlashlightOn = false;
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        updateUI();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            if (isFlashlightOn) {
                statusText.setText("💡 ON");
                toggleButton.setText("Turn Off");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                brightnessSeekBar.setProgress(currentTorchStrength - 1);
                brightnessLabel.setText("Brightness: " + (currentTorchStrength * 10) + "%");
                brightnessSeekBar.setVisibility(android.view.View.VISIBLE);
                brightnessLabel.setVisibility(android.view.View.VISIBLE);
            } else {
                statusText.setText("⚫ OFF");
                toggleButton.setText("Turn On");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                brightnessSeekBar.setVisibility(android.view.View.GONE);
                brightnessLabel.setVisibility(android.view.View.GONE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
        if (cameraThread != null) {
            cameraThread.quitSafely();
        }
    }
}
