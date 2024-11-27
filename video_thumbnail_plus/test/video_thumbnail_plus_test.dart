import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:video_thumbnail_plus/video_thumbnail_plus.dart';
import 'package:video_thumbnail_plus_platform_interface/video_thumbnail_plus_platform_interface.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  VideoThumbnailPlusPlatform.instance = MockMethodChannelVideoThumbnailPlus();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
            MockMethodChannelVideoThumbnailPlus._methodChannel,
            (MethodCall methodCall) async {
      final method = methodCall.method;
      final map = Map<String, dynamic>.from(methodCall.arguments as Map);

      return '$method=${map["video"]}:${map["thumbnailPath"]}:${map["imageFormat"]}:${map["maxWidth"]}:${map["maxHeight"]}:${map["quality"]}';
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
            MockMethodChannelVideoThumbnailPlus._methodChannel, null);
  });

  test('thumbnailData', () async {
    final XFile result = await VideoThumbnailPlus.thumbnailFile(
      video: 'video',
      thumbnailPath: 'path',
      imageFormat: ImageFormat.jpg,
      maxWidth: 100,
      maxHeight: 200,
      quality: 45,
    );

    expect(result.path, 'file=video:path:0:100:200:45');
  });
}

class MockMethodChannelVideoThumbnailPlus extends VideoThumbnailPlusPlatform {
  static const _methodChannel =
      MethodChannel('plugins.com.example/video_thumbnail_plus');

  @override
  Future<XFile> thumbnailFile({
    required String video,
    required Map<String, String>? headers,
    required String? thumbnailPath,
    required ImageFormat imageFormat,
    required int maxHeight,
    required int maxWidth,
    required int timeMs,
    required int quality,
  }) async {
    final reqMap = <String, dynamic>{
      'video': video,
      'headers': headers,
      'thumbnailPath': thumbnailPath,
      'imageFormat': imageFormat.index,
      'maxHeight': maxHeight,
      'maxWidth': maxWidth,
      'timeMs': timeMs,
      'quality': quality
    };

    final path = await _methodChannel.invokeMethod<String>('file', reqMap);
    return XFile(path!);
  }
}
