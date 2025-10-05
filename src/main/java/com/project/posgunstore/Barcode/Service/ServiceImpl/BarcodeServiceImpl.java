// src/main/java/com/project/posgunstore/Barcode/Service/ServiceImpl/BarcodeServiceImpl.java
package com.project.posgunstore.Barcode.Service.ServiceImpl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.project.posgunstore.Barcode.DTO.BarcodeGenerateRequest;
import com.project.posgunstore.Barcode.DTO.BarcodeGenerateResponse;
import com.project.posgunstore.Barcode.Service.BarcodeService;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
public class BarcodeServiceImpl implements BarcodeService {

  @Override
  public BarcodeGenerateResponse generate(BarcodeGenerateRequest req) {
    byte[] png = generatePng(req);
    String b64 = Base64.getEncoder().encodeToString(png);
    String fmt = normalize(req.symbology());
    int width = effectiveWidth(req, fmt);
    int height = effectiveHeight(req, fmt, req.includeText());
    return new BarcodeGenerateResponse(fmt, width, height, "image/png", b64);
  }

  @Override
  public byte[] generatePng(BarcodeGenerateRequest req) {
    String fmt = normalize(req.symbology());
    String data = prepareContent(fmt, req.content());

    int width = effectiveWidth(req, fmt);
    int height = effectiveHeight(req, fmt, req.includeText());
    int margin = req.margin() == null ? 4 : req.margin();

    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.MARGIN, margin);
    if ("QR_CODE".equals(fmt)) {
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    }

    BitMatrix matrix;
    try {
      matrix = new MultiFormatWriter()
          .encode(data, toFormat(fmt), width, baseBarcodeHeight(fmt, height, req.includeText()), hints);
    } catch (WriterException e) {
      throw new ValidationException("Barcode generation failed: " + e.getMessage(), e);
    }

    // Render base barcode image
    BufferedImage core = MatrixToImageWriter.toBufferedImage(matrix);

    // Add human-readable text below for 1D formats if requested
    boolean showText = Boolean.TRUE.equals(req.includeText()) && !"QR_CODE".equals(fmt);
    BufferedImage result = showText ? drawWithText(core, data) : core;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ImageIO.write(result, "png", baos);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("PNG encoding failed", e);
    }
  }

  // ---------- helpers ----------

  private String normalize(String sym) {
    if (sym == null || sym.isBlank()) return "CODE_128";
    return switch (sym.trim().toUpperCase()) {
      case "EAN_13", "QR_CODE", "CODE_128" -> sym.trim().toUpperCase();
      default -> "CODE_128";
    };
  }

  private BarcodeFormat toFormat(String fmt) {
    return switch (fmt) {
      case "EAN_13" -> BarcodeFormat.EAN_13;
      case "QR_CODE" -> BarcodeFormat.QR_CODE;
      default -> BarcodeFormat.CODE_128;
    };
  }

  private int effectiveWidth(BarcodeGenerateRequest req, String fmt) {
    Integer w = req.width();
    if (w != null) return w;
    return "QR_CODE".equals(fmt) ? 512 : 512;
  }

  private int baseBarcodeHeight(String fmt, int totalHeight, Boolean includeText) {
    if ("QR_CODE".equals(fmt)) return totalHeight; // QR has no footer
    boolean showText = Boolean.TRUE.equals(includeText);
    return showText ? Math.max(40, totalHeight - 30) : totalHeight; // leave ~30px for text
  }

  private int effectiveHeight(BarcodeGenerateRequest req, String fmt, Boolean includeText) {
    Integer h = req.height();
    if (h != null) return h;
    if ("QR_CODE".equals(fmt)) return 512;
    // 1D barcodes: default total height ~200 (170 + 30 footer)
    return Boolean.TRUE.equals(includeText) ? 200 : 170;
  }

  private String prepareContent(String fmt, String content) {
    String c = content.trim();
    if ("EAN_13".equals(fmt)) {
      // Accept 12 or 13 digits; compute check if 12 provided
      if (!c.matches("\\d{12,13}"))
        throw new ValidationException("EAN-13 requires 12 or 13 digits");
      if (c.length() == 12) c = c + ean13CheckDigit(c);
    }
    return c;
  }

  private String ean13CheckDigit(String twelveDigits) {
    int sumOdd = 0, sumEven = 0;
    for (int i = 0; i < 12; i++) {
      int d = twelveDigits.charAt(i) - '0';
      if ((i % 2) == 0) sumOdd += d; else sumEven += d;
    }
    int check = (10 - ((sumOdd + sumEven * 3) % 10)) % 10;
    return String.valueOf(check);
  }

  private BufferedImage drawWithText(BufferedImage barcode, String text) {
    int footer = 30;
    int w = barcode.getWidth();
    int h = barcode.getHeight() + footer;

    BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = combined.createGraphics();
    try {
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, w, h);
      g.drawImage(barcode, 0, 0, null);

      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setColor(Color.BLACK);
      Font font = new Font("Monospaced", Font.PLAIN, 14);
      g.setFont(font);
      FontMetrics fm = g.getFontMetrics();
      int textWidth = fm.stringWidth(text);
      int x = Math.max(0, (w - textWidth) / 2);
      int y = barcode.getHeight() + (footer + fm.getAscent()) / 2 - 4;
      g.drawString(text, x, y);
    } finally {
      g.dispose();
    }
    return combined;
  }
}
