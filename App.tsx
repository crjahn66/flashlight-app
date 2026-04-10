import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert } from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { StatusBar } from 'expo-status-bar';

export default function App() {
  const [isFlashlightOn, setIsFlashlightOn] = useState(false);
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
        </View>

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
  camera: {
    position: 'absolute',
    width: 0,
    height: 0,
  },
  statusContainer: {
    width: 200,
    height: 200,
    borderRadius: 100,
    backgroundColor: '#333333',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 40,
    borderWidth: 3,
    borderColor: '#555555',
  },
  statusText: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#ffffff',
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
