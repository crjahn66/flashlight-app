package com.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
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
    private int maxTorchStrength = 1;
    private Button toggleButton;
    private TextView statusText;
    private SeekBar brightnessSeekBar;
    private TextView brightnessLabel;
    private android.widget.LinearLayout brightnessContainer;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private boolean supportsBrightness = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.toggleButton);
        statusText = findViewById(R.id.statusText);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessLabel = findViewById(R.id.brightnessLabel);
        brightnessContainer = findViewById(R.id.brightnessContainer);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            Toast.makeText(this, "No camera found", Toast.LENGTH_SHORT).show();
        }

        // Check for brightness control support (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && cameraId != null) {
            try {
                CameraCharacteristics chars = cameraManager.getCameraCharacteristics(cameraId);
                Integer maxLevel = chars.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
                if (maxLevel != null && maxLevel > 1) {
                    supportsBrightness = true;
                    maxTorchStrength = maxLevel;
                    brightnessSeekBar.setMax(maxLevel - 1);
                    currentTorchStrength = maxLevel;
                }
            } catch (CameraAccessException e) {
                // Brightness control not available
            }
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
                if (isFlashlightOn && fromUser && supportsBrightness) {
                    currentTorchStrength = progress + 1;
                    int percent = Math.round((float) currentTorchStrength / maxTorchStrength * 100);
                    brightnessLabel.setText("Brightness: " + percent + "%");
                    setTorchStrength(currentTorchStrength);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void toggleFlashlight() {
        if (isFlashlightOn) {
            turnOffFlashlight();
        } else {
            turnOnFlashlight();
        }
    }

    private void turnOnFlashlight() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (supportsBrightness && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cameraManager.turnOnTorchWithStrengthLevel(cameraId, currentTorchStrength);
            } else {
                cameraManager.setTorchMode(cameraId, true);
            }
            isFlashlightOn = true;
            updateUI();
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setTorchStrength(int strength) {
        if (!isFlashlightOn || !supportsBrightness) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                cameraManager.turnOnTorchWithStrengthLevel(cameraId, strength);
            } catch (CameraAccessException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        isFlashlightOn = false;
        updateUI();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            if (isFlashlightOn) {
                statusText.setText("💡 ON");
                toggleButton.setText("Turn Off");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                if (supportsBrightness) {
                    brightnessSeekBar.setProgress(currentTorchStrength - 1);
                    int percent = Math.round((float) currentTorchStrength / maxTorchStrength * 100);
                    brightnessLabel.setText("Brightness: " + percent + "%");
                    brightnessContainer.setVisibility(android.view.View.VISIBLE);
                }
            } else {
                statusText.setText("⚫ OFF");
                toggleButton.setText("Turn On");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                brightnessContainer.setVisibility(android.view.View.GONE);
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
    }
}
