enum ImageFormat {
  jpg(mimeType: 'image/jpeg'),
  png(mimeType: 'image/png'),
  webp(mimeType: 'image/webp');

  const ImageFormat({required this.mimeType});

  final String mimeType;
}
