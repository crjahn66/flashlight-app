# Flashlight App

A simple React Native flashlight app that controls your Android device's built-in LED flash.

## Features

- Simple on/off toggle for the flashlight
- Clean, dark-themed UI
- Real-time status indicator
- Camera permission handling

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```

2. Build and run on Android:
   ```bash
   npm run android
   ```

## Requirements

- Android device with a built-in LED flash
- Android SDK 21+
- Node.js and npm

## Permissions

This app requires the `CAMERA` permission to control the LED flash on your device.

## Architecture

- Uses `react-native-camera` for flashlight control
- Handles Android-specific permissions
- TypeScript for type safety
