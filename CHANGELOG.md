# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.18] - 2023-07-05

### Added

- Changelog.md to see what changes

### Fixed



### Changed

- Data returned from FaceDetectedEndEvent and UnknownFaceDetectedEvent, from jsonString to json
- Way to get listenter data changed from JSON.parse(params.jsonString) to direct get using params.faceToken,params.trackID and etc
- Update readme

### Removed


## [0.1.17] - 2023-07-04

### Added

- UnknownFaceDetectedEvent listener

### Fixed



### Changed

- Data returned from FaceDetectedEndEvent and UnknownFaceDetectedEvent, from only faceToken to jsonString contain of more information
- New way to get listenter data, from params.faceToken become params.jsonString, JSON.parse(params.jsonString) is required to get single data in json

### Removed

