# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.46] - 2025-05-22

- Based on 0.1.43,fix some memory leak issue

## [0.1.44-0.1.45] - 2024-10-15

## UPDATE

- change height and width to smaller

## [0.1.43] - 2023-11-20

## UPDATE

- remove detected from sendData function

## [0.1.42] - 2023-08-24

## Add

-function to get the path to the facepass image

## [0.1.41] - 2023-08-16

## Change

-function to get device serial number,now return in json

## [0.1.40] - 2023-08-16

## Added

-function to get device serial number

## [0.1.39] - 2023-08-01

### Fixed

-crash cause by checkgroup


## [0.1.38] - 2023-08-01

### Fixed

-crash cause by native method

## [0.1.36-0.1.37] - 2023-07-26

### Changed

-add support of smile,age gender and rgbir liveness;
-add faceDetectMode and ageGenderEnabled as one new parameter that can be included in initdata -> refer to initdata in readme
-restartApplication() function to restart the application

## [0.1.35] -2023-07-24

### Added

-function to pause listener : pauseListener()

## [0.1.33 - 0.1.34] - 2023-07-20

### Changed

- change way to implement libs
  Before:
  implementation fileTree(dir: "libs", include: ["*.aar"])
  implementation fileTree(dir: 'libs', include: ['*.jar'])

  After:
  implementation (name:"libs/FacePassAndroidSDK-year-release",ext:'aar')
  implementation (name:"libs/q-zhenglib",ext:'aar')
  implementation (name:"libs/yface",ext:'jar')

-due to the changes abort, this need to be added to your code
    inside android/build.gradle
        allprojects {
            repositories {
                ...
                flatDir { dirs "$rootDir/../node_modules/facepass-react-native-module/android/libs"}
            }
        }

## [0.1.32] - 2023-07-17

### Changed

-readme content

## [0.1.31] - 2023-07-17

### Changed

-return Base64 image of captured face

## [0.1.30] - 2023-07-14

### Added

-setExposureCompensation() function

## [0.1.29] - 2023-07-14

### Added

-hideNavigationBar() function

## [0.1.28] - 2023-07-13

### Changed

- make error message more specific

## [0.1.27] - 2023-07-13

### Changed

- make FACEPASS_HANDLER_NULL_ERROR more specific

## [0.1.26] - 2023-07-13

### Fixed

-checkDoneInitialize return undefined

## [0.1.25] - 2023-07-13

### Added

-  checkDoneInitialize() function
- releaseFacePassHandler() function

### Changed

- now initData() will release facepass handler first before initialize, which enable the change of facial recognition parameter without restart application, to do this, need to call initData(newParameter) on apply new parameter

## [0.1.24] - 2023-07-05

### Added

- enableQRScan function
- Ability to check qr content

### Changed

- Optimize code in native module

## [0.1.23] - 2023-07-05

### Added

- Changelog.md to see what changes

### Changed

- Data returned from FaceDetectedEndEvent and UnknownFaceDetectedEvent, from jsonString to json
- Way to get listenter data changed from JSON.parse(params.jsonString) to direct get using params.faceToken,params.trackID and etc
- Update readme

## [0.1.18-0.1.22]

Nothing changed,test publish

## [0.1.17] - 2023-07-04

### Added

- UnknownFaceDetectedEvent listener

### Fixed



### Changed

- Data returned from FaceDetectedEndEvent and UnknownFaceDetectedEvent, from only faceToken to jsonString contain of more information
- New way to get listenter data, from params.faceToken become params.jsonString, JSON.parse(params.jsonString) is required to get single data in json

### Removed

