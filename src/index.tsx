import {
  NativeModules,
  Platform,
  requireNativeComponent,
  HostComponent,
} from 'react-native';
import React from 'react';
const LINKING_ERROR =
  `The package 'facepass-react-native-module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const FacePassReactNativeModule = NativeModules.FacePass
  ? NativeModules.FacePass
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export const FacePassViewManager: HostComponent<any> = requireNativeComponent(
  'FacePassViewManager'
);

export const FacePass = FacePassReactNativeModule;

export function startCameraScreen(height: number, width: number, ref: any) {
  return (
    <FacePassViewManager
      style={{
        height: height,
        width: width,
      }}
      ref={ref}
    />
  );
}

export function initData(data: JSON): Promise<void> {
  let jsonString = '';
  if (data != undefined && data != null) {
    jsonString = JSON.stringify(data);
  }
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.initData(
      jsonString,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function setDefaultGroupName(data: String): Promise<void> {
  let name: String = '';
  if (data != undefined && data != null) {
    name = data;
  }
  return FacePassReactNativeModule.setDefaultGroupName(name);
}

export function cameraSetting(data: String): Promise<void> {
  let jsonString = '';
  if (data != undefined && data != null) {
    jsonString = JSON.stringify(data);
  }
  return FacePassReactNativeModule.cameraSetting(jsonString);
}

export function selectImage(): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.selectImage(
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function addFace(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.addFace(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function getFace(data: String): Promise<void> {
  console.log(data);
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.getFace(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function deleteFace(
  faceToken: String,
  groupName: String
): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.deleteFace(
      faceToken,
      groupName,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function bindGroup(faceToken: String, groupName: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.bindGroup(
      faceToken,
      groupName,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function getGroupInfo(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.getGroupInfo(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function unbindFace(
  faceToken: String,
  groupName: String
): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.unbindFace(
      faceToken,
      groupName,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function getAllGroup(): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.getAllGroup(
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function createGroup(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.createGroup(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function deleteGroup(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.deleteGroup(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function changeLight(data: String): Promise<void> {
  return FacePassReactNativeModule.changeLight(data);
}
export function controlDoor(data: String): Promise<void> {
  return FacePassReactNativeModule.controlDoor(data);
}

export function useIRCameraSupport(data: Boolean): Promise<void> {
  return FacePassReactNativeModule.useIRCameraSupport(data);
}

export function restartDevice(): Promise<void> {
  return FacePassReactNativeModule.restartDevice();
}

export function enableTemperature(enable: Boolean): Promise<void> {
  return FacePassReactNativeModule.enableTemperature(enable);
}

export function enableIRPreview(enable: Boolean): Promise<void> {
  return FacePassReactNativeModule.enableIRPreview(enable);
}

export function enableQRScan(enable: Boolean): Promise<void> {
  return FacePassReactNativeModule.enableQRScan(enable);
}

export function setRecognitionDisplayTime(time: Number): Promise<void> {
  return FacePassReactNativeModule.setRecognitionDisplayTime(time);
}

export function releaseFacePassHandler(): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.releaseFacePassHandler(
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}

export function checkDoneInitialize(): Promise<void> {
  return new Promise((resolve) => {
    FacePassReactNativeModule.checkDoneInitialize((success: any) => {
      resolve(success);
    });
  });
}

export function checkGroupExist(data: String): Promise<void> {
  return new Promise((resolve, reject) => {
    FacePassReactNativeModule.checkGroupExist(
      data,
      (success: any) => {
        resolve(success);
      },
      (fail: any) => {
        reject(fail);
      }
    );
  });
}
