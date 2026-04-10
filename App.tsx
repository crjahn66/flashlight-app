import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert } from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { StatusBar } from 'expo-status-bar';
import Slider from '@react-native-community/slider';

export default function App() {
  const [isFlashlightOn, setIsFlashlightOn] = useState(false);
  const [brightness, setBrightness] = useState(100);
  const [permission, requestPermission] = useCameraPermissions();

  useEffect(() => {
    if (permission === null) {
      requestPermission();
    }
  }, [permission]);

  const toggleFlashlight = async () => {
    if (permission?.status !== 'granted') {
      Alert.alert('Permission Denied', 'Camera permission is required to use the flashlight.');
      return;
    }

    try {
      setIsFlashlightOn(!isFlashlightOn);
    } catch (error) {
      Alert.alert('Error', 'Failed to toggle flashlight.');
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
          <Text style={styles.brightnessLabel}>
            Brightness: {Math.round(brightness)}%
          </Text>
        </View>

        {isFlashlightOn && (
          <View style={styles.sliderContainer}>
            <Slider
              style={styles.slider}
              minimumValue={10}
              maximumValue={100}
              step={10}
              value={brightness}
              onValueChange={setBrightness}
              minimumTrackTintColor="#FFD700"
              maximumTrackTintColor="#555555"
              thumbTintColor="#FFD700"
            />
            <View style={styles.sliderLabels}>
              <Text style={styles.sliderLabel}>Low</Text>
              <Text style={styles.sliderLabel}>High</Text>
            </View>
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
  brightnessLabel: {
    fontSize: 14,
    color: '#FFD700',
    marginTop: 10,
    fontWeight: '600',
  },
  sliderContainer: {
    width: '80%',
    marginBottom: 30,
    paddingHorizontal: 10,
  },
  slider: {
    width: '100%',
    height: 50,
  },
  sliderLabels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 5,
  },
  sliderLabel: {
    fontSize: 12,
    color: '#888888',
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
