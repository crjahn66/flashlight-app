package com.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
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

import java.lang.reflect.Field;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private boolean isFlashlightOn = false;
    private int currentTorchStrength = 1;
    private Button toggleButton;
    private TextView statusText;
    private SeekBar brightnessSeekBar;
    private TextView brightnessLabel;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private Field torchStrengthField;

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

        // Try to get TORCH_STRENGTH field for Android 13+
        try {
            Class<?> captureRequestClass = Class.forName("android.hardware.camera2.CaptureRequest");
            torchStrengthField = captureRequestClass.getDeclaredField("TORCH_STRENGTH");
        } catch (Exception e) {
            torchStrengthField = null;
        }

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

        if (torchStrengthField != null) {
            // Use Camera2 API with TORCH_STRENGTH
            openCameraWithTorchStrength();
        } else {
            // Fallback to simple setTorchMode
            try {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                updateUI();
            } catch (CameraAccessException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCameraWithTorchStrength() {
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    createCaptureSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    Toast.makeText(MainActivity.this, "Camera error: " + error, Toast.LENGTH_SHORT).show();
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createCaptureSession() {
        try {
            cameraDevice.createCaptureSession(Collections.emptyList(), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    captureSession = session;
                    isFlashlightOn = true;
                    updateUI();
                    updateTorchBrightness(currentTorchStrength);
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(MainActivity.this, "Failed to configure camera", Toast.LENGTH_SHORT).show();
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTorchBrightness(int strength) {
        if (!isFlashlightOn || captureSession == null) return;

        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

            // Set TORCH_STRENGTH using reflection
            if (torchStrengthField != null) {
                try {
                    @SuppressWarnings("unchecked")
                    CaptureRequest.Key<Integer> torchStrengthKey = (CaptureRequest.Key<Integer>) torchStrengthField.get(null);
                    builder.set(torchStrengthKey, strength);
                } catch (Exception e) {
                    // If setting fails, just use torch without strength control
                }
            }

            CaptureRequest request = builder.build();
            captureSession.setRepeatingRequest(request, null, cameraHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffFlashlight() {
        isFlashlightOn = false;
        
        if (captureSession != null) {
            try {
                captureSession.abortCaptures();
                captureSession.close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            captureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        } else {
            // Fallback for simple torch mode
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
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
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        if (cameraThread != null) {
            cameraThread.quitSafely();
        }
    }
}
