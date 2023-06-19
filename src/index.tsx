import { NativeModules, Platform, requireNativeComponent, HostComponent } from 'react-native';
import React from 'react'
const LINKING_ERROR =
  `The package 'facepass-react-native-module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const FppReactNativeModule = NativeModules.FacePass
  ? NativeModules.FacePass
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );

export const FacePassViewManager: HostComponent<any> =
  requireNativeComponent('FacePassViewManager');

export const FacePass=FppReactNativeModule;

export function startCameraScreen(height: number, width: number, ref: any) {
  return <FacePassViewManager
    style={{
      height: height,
      width: width,
    }}

    ref={ref}
  />;
}


export function initData(data: JSON): Promise<void> {
  const jsonString = JSON.stringify(data)
  return FppReactNativeModule.initData(jsonString)
}

export function setDefaultGroupName(data: String): Promise<void> {
  return FppReactNativeModule.setDefaultGroupName(data)
}

export function cameraSetting(data: String): Promise<void> {
  const jsonString = JSON.stringify(data)
  return FppReactNativeModule.cameraSetting(jsonString)
}


export function selectImage(): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.selectImage((success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })


}


export function addFace(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.addFace(data, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}

export function getFace(data: String): Promise<void> {
  console.log(data)
  return new Promise((resolve, reject) => {
    FppReactNativeModule.getFace(data, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}

export function deleteFace(faceToken: String, groupName: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.deleteFace(faceToken, groupName, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}


export function bindGroup(faceToken: String, groupName: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.bindGroup(faceToken, groupName, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}

export function getGroupInfo(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.getGroupInfo(data, (success: any) => {
      resolve(success);
    }, (
      fail: any
    ) => {
      reject(fail);
    })
  })
}

export function unbindFace(faceToken: String, groupName: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.unbindFace(faceToken, groupName, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}


export function getAllGroup(): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.getAllGroup((success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}


export function createGroup(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.createGroup(data, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}


export function deleteGroup(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FppReactNativeModule.deleteGroup(data, (success: any) => {
      resolve(success)
    }, (
      fail: any
    ) => {
      reject(fail)
    })
  })
}
