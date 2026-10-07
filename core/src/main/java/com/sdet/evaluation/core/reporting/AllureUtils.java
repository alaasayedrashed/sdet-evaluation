package com.sdet.evaluation.core.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single entry point for adding attachments to the current Allure test or step.
 *
 * <p>All methods are defensive: reporting must never be the reason a test fails, so any problem
 * (null content, missing file, no running test) is logged and swallowed.
 */
public final class AllureUtils {

  private static final Logger LOG = LoggerFactory.getLogger(AllureUtils.class);
  private static final ObjectMapper JSON =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private AllureUtils() {}

  /** Attaches a PNG screenshot. */
  public static void attachScreenshot(String name, byte[] png) {
    attach(name, "image/png", "png", png);
  }

  /** Attaches plain text, e.g. a log excerpt, URL or error message. */
  public static void attachText(String name, String text) {
    attach(name, "text/plain", "txt", toBytes(text));
  }

  /** Attaches JSON, pretty-printed when it is valid JSON and attached as-is otherwise. */
  public static void attachJson(String name, String json) {
    attach(name, "application/json", "json", toBytes(prettyPrint(json)));
  }

  /** Attaches XML, e.g. a mobile page source. */
  public static void attachXml(String name, String xml) {
    attach(name, "text/xml", "xml", toBytes(xml));
  }

  /** Attaches HTML, e.g. a web page snapshot. */
  public static void attachHtml(String name, String html) {
    attach(name, "text/html", "html", toBytes(html));
  }

  /**
   * Attaches a file from disk, e.g. a Playwright trace.
   *
   * @param mimeType content type shown by Allure, e.g. {@code application/zip}
   * @param extension file extension without the dot
   */
  public static void attachFile(String name, Path file, String mimeType, String extension) {
    try {
      attach(name, mimeType, extension, Files.readAllBytes(file));
    } catch (IOException e) {
      LOG.warn("Could not read '{}' to attach as '{}': {}", file, name, e.getMessage());
    }
  }

  private static void attach(String name, String mimeType, String extension, byte[] content) {
    if (content == null || content.length == 0) {
      LOG.warn("Skipping empty Allure attachment '{}'", name);
      return;
    }
    try {
      Allure.getLifecycle()
          .addAttachment(
              name,
              mimeType,
              new ByteArrayInputStream(content),
              AttachmentOptions.withFileExtension(extension));
      LOG.debug("Attached '{}' ({} bytes, {})", name, content.length, mimeType);
    } catch (RuntimeException e) {
      LOG.warn("Could not add Allure attachment '{}': {}", name, e.getMessage());
    }
  }

  private static byte[] toBytes(String text) {
    return text == null ? null : text.getBytes(StandardCharsets.UTF_8);
  }

  private static String prettyPrint(String json) {
    if (json == null || json.isBlank()) {
      return json;
    }
    try {
      return JSON.writeValueAsString(JSON.readTree(json));
    } catch (IOException _) {
      return json;
    }
  }
}
