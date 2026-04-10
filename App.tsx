import React, { useState } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert, NativeModules } from 'react-native';
import { StatusBar } from 'react-native';

const FlashlightModule = NativeModules.FlashlightModule;

export default function App() {
  const [isFlashlightOn, setIsFlashlightOn] = useState(false);
  const [brightness, setBrightness] = useState(100);

  const toggleFlashlight = async () => {
    try {
      if (!isFlashlightOn) {
        await FlashlightModule.turnOn();
        setIsFlashlightOn(true);
      } else {
        await FlashlightModule.turnOff();
        setIsFlashlightOn(false);
      }
    } catch (error) {
      Alert.alert('Error', 'Failed to toggle flashlight');
    }
  };

  const increaseBrightness = () => {
    if (brightness < 100) {
      const newBrightness = brightness + 10;
      setBrightness(newBrightness);
      FlashlightModule.setBrightness(newBrightness);
    }
  };

  const decreaseBrightness = () => {
    if (brightness > 10) {
      const newBrightness = brightness - 10;
      setBrightness(newBrightness);
      FlashlightModule.setBrightness(newBrightness);
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#1a1a1a" />
      
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
              style={[styles.brightnessButton, brightness <= 10 && styles.disabled]}
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
              style={[styles.brightnessButton, brightness >= 100 && styles.disabled]}
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
  disabled: {
    opacity: 0.5,
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
