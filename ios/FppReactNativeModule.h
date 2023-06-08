
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNFppReactNativeModuleSpec.h"

@interface FppReactNativeModule : NSObject <NativeFppReactNativeModuleSpec>
#else
#import <React/RCTBridgeModule.h>

@interface FppReactNativeModule : NSObject <RCTBridgeModule>
#endif

@end
