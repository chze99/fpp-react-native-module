import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'fpp-react-native-module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const FppReactNativeModule = NativeModules.FppReactNativeModule
  ? NativeModules.FppReactNativeModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiple(a: number, b: number): Promise<number> {
  return FppReactNativeModule.multiple(a, b);
}
