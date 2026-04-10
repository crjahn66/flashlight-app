package com.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
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
    private Button toggleButton;
    private TextView statusText;
    private SeekBar brightnessSeekBar;
    private TextView brightnessLabel;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.toggleButton);
        statusText = findViewById(R.id.statusText);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessLabel = findViewById(R.id.brightnessLabel);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

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
                int brightness = (progress / 10) * 10 + 10;
                brightnessLabel.setText("Brightness: " + brightness + "%");
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
        try {
            if (isFlashlightOn) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                statusText.setText("⚫ OFF");
                toggleButton.setText("Turn On");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                brightnessSeekBar.setVisibility(android.view.View.GONE);
                brightnessLabel.setVisibility(android.view.View.GONE);
            } else {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                statusText.setText("💡 ON");
                toggleButton.setText("Turn Off");
                toggleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                brightnessSeekBar.setVisibility(android.view.View.VISIBLE);
                brightnessLabel.setVisibility(android.view.View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        try {
            if (isFlashlightOn) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
