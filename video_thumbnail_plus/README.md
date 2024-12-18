# video_thumbnail_plus

This plugin generates thumbnail from video file or URL.  It returns image in memory or writes into a file.  It offers rich options to control the image format, resolution and quality.  Supports iOS / Android / web.

[![pub ver](https://img.shields.io/badge/pub-v1.0.0-blue)](https://pub.dev/packages/video_thumbnail_plus)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/maRci002/)

![video_thumbnail_plus](https://raw.githubusercontent.com/maRci002/video_thumbnail_plus/master/assets/video_thumbnail_plus.gif)

## Methods
| function      | parameter                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | description                                        | return                |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------- | --------------------- |
| thumbnailData | String `[video]`, optional Map<String, dynamic> `[headers]`, ImageFormat `[imageFormat]`(jpg/png/webp), int `[maxHeight]`(0: for the original resolution of the video, or scaled by the source aspect ratio), [maxWidth]`(0: for the original resolution of the video, or scaled by the source aspect ratio), int `[timeMs]` generates the thumbnail from the frame around the specified millisecond, int `[quality]`(0-100)                                                                                                                                                              | generates thumbnail from `[video]`                 | `[Future<Uint8List>]` |
| thumbnailFile | String `[video]`, optional Map<String, dynamic> `[headers]`, String `[thumbnailPath]`(folder or full path where to store the thumbnail file, null to save to same folder as the video file) this ignored on the web, ImageFormat `[imageFormat]`(jpg/png/webp), int `[maxHeight]`(0: for the original resolution of the video, or scaled by the source aspect ratio), int `[maxWidth]`(0: for the original resolution of the video, or scaled by the source aspect ratio), int `[timeMs]` generates the thumbnail from the frame around the specified millisecond, int `[quality]`(0-100) | creates a file of the thumbnail from the `[video]` | `[Future<String>]`    |

Warning:
> Giving both the `maxHeight` and `maxWidth` has different result on Android platform, it actually scales the thumbnail to the specified maxHeight and maxWidth.
> To generate the thumbnail from a network resource, the `video` must be properly URL encoded.

## Usage

**Installing**
add [video_thumbnail_plus](https://pub.dev/packages/video_thumbnail_plus) as a dependency in your pubspec.yaml file.
```yaml
dependencies:
  video_thumbnail_plus: ^1.0.0
```
**import**
```dart
import 'package:video_thumbnail_plus/video_thumbnail_plus.dart';
```
**Generate a thumbnail in memory from video file**
```dart
final uint8list = await VideoThumbnailPlus.thumbnailData(
  video: videofile.path,
  imageFormat: ImageFormat.jpg,
  maxWidth: 128, // specify the width of the thumbnail, let the height auto-scaled to keep the source aspect ratio
  quality: 25,
);
```

**Generate a thumbnail file from video URL**
```dart
XFile thumbnailFile = await VideoThumbnailPlus.thumbnailFile(
  video: "https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4",
  thumbnailPath: (await getTemporaryDirectory()).path,
  imageFormat: ImageFormat.webp,
  maxHeight: 64, // specify the height of the thumbnail, let the width auto-scaled to keep the source aspect ratio
  quality: 75,
);

final image = kIsWeb ? Image.network(thumbnailFile.path) : Image.file(File(thumbnailFile.path));
```

**Generate a thumbnail file from video Assets declared in pubspec.yaml**
```dart
final byteData = await rootBundle.load("assets/my_video.mp4");
Directory tempDir = await getTemporaryDirectory();

File tempVideo = File("${tempDir.path}/assets/my_video.mp4")
  ..createSync(recursive: true)
  ..writeAsBytesSync(byteData.buffer.asUint8List(byteData.offsetInBytes, byteData.lengthInBytes));

final fileName = await VideoThumbnailPlus.thumbnailFile(
  video: tempVideo.path,
  thumbnailPath: (await getTemporaryDirectory()).path,
  imageFormat: ImageFormat.png,  
  quality: 100,
);
```

## Limitations on the Web platform

Video Thumbnail Plus on the Web platform has some limitations that might surprise developers more familiar with mobile/desktop targets.

In no particular order:

### CORS headers
This plugin requires the server hosting the video to include appropriate CORS headers in the response. Specifically, the server must include the `Access-Control-Allow-Origin` and `Access-Control-Allow-Methods` headers in the response to the request.
For more information, please refer to the [Mozilla Developer Network documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS).

### HTTP range headers
This plugin requires the server hosting the video to support HTTP range headers. If the server does not support range requests, the plugin may generate a thumbnail from the first frame of the video instead of the desired frame.
For more information, please refer to the [Mozilla Developer Network documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests).

## Notes
Fork or pull requests are always welcome. Currently it seems have a little performance issue while generating WebP thumbnail by using libwebp under iOS.

## Acknowledgments

This package is a fork of the original [video_thumbnail](https://github.com/justsoft/video_thumbnail) package. Since the original appears to be no longer maintained, this version (`video_thumbnail_plus`) adds support for web and WebAssembly.
