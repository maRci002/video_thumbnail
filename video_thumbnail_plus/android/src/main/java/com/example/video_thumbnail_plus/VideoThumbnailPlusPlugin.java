package com.example.video_thumbnail_plus;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * VideoThumbnailPlusPlugin
 */
public class VideoThumbnailPlusPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String TAG = "ThumbnailPlugin";

    private Context context;
    private ExecutorService executor;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        executor = Executors.newCachedThreadPool();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "plugins.com.example/video_thumbnail_plus");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
        executor.shutdown();
        executor = null;
        context = null;
    }

    private static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private void onSuccess(final Result result, Object object) {
        runOnUiThread(() -> result.success(object));
    }

    private void onError(final Result result, Throwable e) {
        runOnUiThread(() -> {
            Log.e(TAG, "Error generating thumbnail: " + e.getMessage(), e);
            result.error("ERROR_GENERATING_THUMBNAIL", e.getMessage(), null);
        });
    }

    private void onNoImplemented(final Result result) {
        runOnUiThread(result::notImplemented);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        final String method = call.method;
        final Map<String, Object> args = call.arguments();

        executor.execute(() -> {
            try {
                if (method.equals("file")) {
                    final String video = (String) args.get("video");
                    final HashMap<String, String> headers = (HashMap<String, String>) args.get("headers");
                    final String thumbnailPath = (String) args.get("thumbnailPath");
                    final int imageFormat = (int) args.get("imageFormat");
                    final int maxWidth = (int) args.get("maxWidth");
                    final int maxHeight = (int) args.get("maxHeight");
                    final int timeMs = (int) args.get("timeMs");
                    final int quality = (int) args.get("quality");

                    final String path = buildThumbnailFile(video, headers, thumbnailPath, imageFormat, maxWidth, maxHeight, timeMs, quality);
                    onSuccess(result, path);
                } else if (method.equals("data")) {
                    final String video = (String) args.get("video");
                    final HashMap<String, String> headers = (HashMap<String, String>) args.get("headers");
                    final int imageFormat = (int) args.get("imageFormat");
                    final int maxWidth = (int) args.get("maxWidth");
                    final int maxHeight = (int) args.get("maxHeight");
                    final int timeMs = (int) args.get("timeMs");
                    final int quality = (int) args.get("quality");

                    final byte[] bytes = buildThumbnailData(video, headers, imageFormat, maxWidth, maxHeight, timeMs, quality);
                    onSuccess(result, bytes);
                } else {
                    onNoImplemented(result);
                }
            } catch (Exception e) {
                onError(result, e);
            }
        });
    }

    private static Bitmap.CompressFormat intToFormat(int format) {
        switch (format) {
            case 0:
                return Bitmap.CompressFormat.JPEG;
            case 1:
                return Bitmap.CompressFormat.PNG;
            case 2:
                return Bitmap.CompressFormat.WEBP;
            default:
                throw new IllegalArgumentException("Unexpected ImageFormat value: " + format);
        }
    }

    private static String formatExt(int format) {
        switch (format) {
            case 0:
                return "jpg";
            case 1:
                return "png";
            case 2:
                return "webp";
            default:
                throw new IllegalArgumentException("Unexpected ImageFormat value: " + format);
        }
    }

    private byte[] buildThumbnailData(final String vidPath, final HashMap<String, String> headers, int format,
                                      int maxWidth, int maxHeight, int timeMs, int quality) {
        Bitmap bitmap = createVideoThumbnail(vidPath, headers, maxWidth, maxHeight, timeMs);
        if (bitmap == null) {
            throw new IllegalStateException("Failed to generate video thumbnail. Bitmap is null.");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(intToFormat(format), quality, stream);
        bitmap.recycle();

        return stream.toByteArray();
    }

    private String buildThumbnailFile(final String vidPath, final HashMap<String, String> headers, String path,
                                      int format, int maxWidth, int maxHeight, int timeMs,
                                      int quality) {
        final byte[] bytes = buildThumbnailData(vidPath, headers, format, maxWidth, maxHeight, timeMs, quality);
        final String ext = formatExt(format);

        final int videoPathWithoutExtension = vidPath.lastIndexOf(".");
        String fullVidPath = vidPath.substring(0, videoPathWithoutExtension + 1) + ext;

        final boolean isLocalFile = (vidPath.startsWith("/") || vidPath.startsWith("file://"));

        if (path == null && !isLocalFile) {
            path = context.getCacheDir().getAbsolutePath();
        }

        if (path != null) {
            if (path.endsWith(ext)) {
                fullVidPath = path;
            } else {
                // try to save to same folder as the vidPath
                final int folderPath = fullVidPath.lastIndexOf("/");

                if (path.endsWith("/")) {
                    fullVidPath = path + fullVidPath.substring(folderPath + 1);
                } else {
                    fullVidPath = path + fullVidPath.substring(folderPath);
                }
            }
        }

        try {
            FileOutputStream f = new FileOutputStream(fullVidPath);
            f.write(bytes);
            f.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return fullVidPath;
    }

    public Bitmap createVideoThumbnail(final String video, final HashMap<String, String> headers, int targetWidth, int targetHeight, int timeMs) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            if (video.startsWith("/")) {
                setDataSource(video, retriever);
            } else if (video.startsWith("file://")) {
                setDataSource(video.substring("file://".length()), retriever);
            } else {
                retriever.setDataSource(video, (headers != null) ? headers : new HashMap<>());
            }

            if (targetHeight != 0 || targetWidth != 0) {
                if (android.os.Build.VERSION.SDK_INT >= 27 && targetHeight != 0 && targetWidth != 0) {
                    bitmap = retriever.getScaledFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST, targetWidth, targetHeight);
                } else {
                    bitmap = retriever.getFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST);
                    bitmap = scaleBitmap(bitmap, targetWidth, targetHeight);
                }
            } else {
                bitmap = retriever.getFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return bitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {
        if (bitmap == null) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (targetWidth == 0) targetWidth = Math.round(((float) targetHeight / height) * width);
        if (targetHeight == 0) targetHeight = Math.round(((float) targetWidth / width) * height);

        Log.d(TAG, String.format("original w:%d, h:%d => %d, %d", width, height, targetWidth, targetHeight));
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    private static void setDataSource(String video, final MediaMetadataRetriever retriever) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(video)) {
            retriever.setDataSource(inputStream.getFD());
        }
    }

}
