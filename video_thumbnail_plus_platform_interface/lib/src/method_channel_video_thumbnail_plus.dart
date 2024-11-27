import 'package:cross_file/cross_file.dart';
import 'package:flutter/services.dart';
import 'package:video_thumbnail_plus_platform_interface/src/image_format.dart';
import 'package:video_thumbnail_plus_platform_interface/src/video_thumbnail_plus_platform.dart';

/// An implementation of [VideoThumbnailPlusPlatform] that uses method channels.
class MethodChannelVideoThumbnailPlus extends VideoThumbnailPlusPlatform {
  /// The method channel used to interact with the native platform.
  static const _methodChannel =
      MethodChannel('plugins.com.example/video_thumbnail_plus');

  @override
  Future<XFile> thumbnailFile({
    required String video,
    required Map<String, String>? headers,
    required String? thumbnailPath,
    required ImageFormat imageFormat,
    required int maxWidth,
    required int maxHeight,
    required int timeMs,
    required int quality,
  }) async {
    final reqMap = <String, dynamic>{
      'video': video,
      'headers': headers,
      'thumbnailPath': thumbnailPath,
      'imageFormat': imageFormat.index,
      'maxWidth': maxWidth,
      'maxHeight': maxHeight,
      'timeMs': timeMs,
      'quality': quality
    };

    final path = await _methodChannel.invokeMethod<String>('file', reqMap);
    return XFile(path!);
  }

  @override
  Future<Uint8List> thumbnailData({
    required String video,
    required Map<String, String>? headers,
    required ImageFormat imageFormat,
    required int maxWidth,
    required int maxHeight,
    required int timeMs,
    required int quality,
  }) async {
    final reqMap = <String, dynamic>{
      'video': video,
      'headers': headers,
      'imageFormat': imageFormat.index,
      'maxWidth': maxWidth,
      'maxHeight': maxHeight,
      'timeMs': timeMs,
      'quality': quality,
    };
    final bytes = await _methodChannel.invokeMethod('data', reqMap);
    return bytes!;
  }
}
