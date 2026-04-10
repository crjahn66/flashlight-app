import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import * as Camera from 'expo-camera';

export default function App() {
  const [isFlashlightOn, setIsFlashlightOn] = useState(false);
  const [brightness, setBrightness] = useState(100);
  const [permission, requestPermission] = Camera.useCameraPermissions();

  useEffect(() => {
    if (permission?.status !== 'granted') {
      requestPermission();
    }
  }, [permission]);

  const toggleFlashlight = async () => {
    if (permission?.status !== 'granted') {
      Alert.alert('Permission Denied', 'Camera permission is required.');
      return;
    }
    setIsFlashlightOn(!isFlashlightOn);
  };

  const increaseBrightness = () => {
    if (brightness < 100) {
      setBrightness(brightness + 10);
    }
  };

  const decreaseBrightness = () => {
    if (brightness > 10) {
      setBrightness(brightness - 10);
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      <View style={styles.innerContainer}>
        <Text style={styles.title}>Flashlight</Text>

        <View style={styles.statusContainer}>
          <Text style={styles.statusText}>
            {isFlashlightOn ? '💡 ON' : '⚫ OFF'}
          </Text>
          <Text style={styles.brightnessValue}>
            {Math.round(brightness)}%
          </Text>
        </View>

        {isFlashlightOn && (
          <View style={styles.brightnessControls}>
            <TouchableOpacity 
              style={styles.brightnessButton}
              onPress={decreaseBrightness}
              disabled={brightness <= 10}
            >
              <Text style={styles.brightnessButtonText}>−</Text>
            </TouchableOpacity>

            <View style={styles.brightnessBar}>
              <View 
                style={[
                  styles.brightnessBarFill,
                  { width: `${brightness}%` }
                ]}
              />
            </View>

            <TouchableOpacity 
              style={styles.brightnessButton}
              onPress={increaseBrightness}
              disabled={brightness >= 100}
            >
              <Text style={styles.brightnessButtonText}>+</Text>
            </TouchableOpacity>
          </View>
        )}

        <TouchableOpacity
          style={[
            styles.button,
            { backgroundColor: isFlashlightOn ? '#FFD700' : '#333333' },
          ]}
          onPress={toggleFlashlight}
        >
          <Text style={styles.buttonText}>
            {isFlashlightOn ? 'Turn Off' : 'Turn On'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a1a1a',
  },
  innerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  title: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 40,
  },
  statusContainer: {
    width: 200,
    borderRadius: 100,
    backgroundColor: '#333333',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 40,
    borderWidth: 3,
    borderColor: '#555555',
    paddingVertical: 30,
  },
  statusText: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#ffffff',
  },
  brightnessValue: {
    fontSize: 20,
    color: '#FFD700',
    marginTop: 10,
    fontWeight: '600',
  },
  brightnessControls: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '80%',
    marginBottom: 30,
    gap: 15,
  },
  brightnessButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: '#FFD700',
    justifyContent: 'center',
    alignItems: 'center',
  },
  brightnessButtonText: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#000000',
  },
  brightnessBar: {
    flex: 1,
    height: 20,
    backgroundColor: '#333333',
    borderRadius: 10,
    overflow: 'hidden',
    borderWidth: 2,
    borderColor: '#555555',
  },
  brightnessBarFill: {
    height: '100%',
    backgroundColor: '#FFD700',
  },
  button: {
    paddingVertical: 15,
    paddingHorizontal: 50,
    borderRadius: 50,
    alignItems: 'center',
  },
  buttonText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#000000',
  },
});
