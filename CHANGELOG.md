# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


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

