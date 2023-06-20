import React from 'react';
import { NavigationContainer, } from '@react-navigation/native';
import FaceManagement from '../pages/FaceManagement';
import Home from '../pages/Home';
import AddFace from '../pages/AddFace'
import FaceGroupSettingScreen from '../pages/FaceGroupSettingScreen'
import FacialRecognitionSettingScreen from '../pages/FacialRecognitionSettingScreen'
import SettingScreen from '../pages/SettingScreen'
// import { createStackNavigator } from '@react-navigation/stack';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { SafeAreaProvider } from 'react-native-safe-area-context';
export default function Routes() {
    // const Stack = createStackNavigator();
    const Stack = createNativeStackNavigator();

    return (
        <SafeAreaProvider style={{ backgroundColor: '#000' }}>

            <NavigationContainer >
                <Stack.Navigator>

                    <Stack.Screen
                        name="Home"
                        component={Home}

                    />
                    <Stack.Screen
                        name="FaceManagement"
                        component={FaceManagement}
                    />
                    <Stack.Screen
                        name="AddFace"
                        component={AddFace}
                    />
                                        <Stack.Screen
                        name="FaceGroupSettingScreen"
                        component={FaceGroupSettingScreen}
                    />
                                        <Stack.Screen
                        name="FacialRecognitionSettingScreen"
                        component={FacialRecognitionSettingScreen}
                    />
                                        <Stack.Screen
                        name="SettingScreen"
                        component={SettingScreen}
                    />
                </Stack.Navigator>
            </NavigationContainer>
        </SafeAreaProvider>

    );
}