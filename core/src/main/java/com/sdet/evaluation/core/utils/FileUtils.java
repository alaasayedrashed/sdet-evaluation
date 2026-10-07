package com.sdet.evaluation.core.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/** File and classpath helpers. */
public final class FileUtils {

  private FileUtils() {}

  /**
   * Resolves a classpath resource to an absolute file path, e.g. to hand an APK to Appium.
   *
   * @throws IllegalArgumentException if the resource does not exist or is not a plain file
   */
  public static Path classpathResourcePath(String resourcePath) {
    URL url = classLoader().getResource(resourcePath);
    if (url == null) {
      throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
    }
    try {
      return Path.of(url.toURI()).toAbsolutePath();
    } catch (URISyntaxException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Resource is not a file on disk: " + url, e);
    }
  }

  /** Creates a directory (and parents) if needed and returns it. */
  public static Path ensureDirectory(Path directory) {
    try {
      return Files.createDirectories(directory);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to create directory: " + directory, e);
    }
  }

  private static ClassLoader classLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
}
