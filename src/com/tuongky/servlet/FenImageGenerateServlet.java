package com.tuongky.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.common.collect.Lists;

// Red: 帅 仕 相 车 炮 马 兵
// Black: 将 士 象 车 炮 马 卒

@SuppressWarnings("serial")
public class FenImageGenerateServlet extends HttpServlet {

  private static final int K = 1;
  private static final int A = 2;
  private static final int E = 3;
  private static final int R = 4;
  private static final int C = 5;
  private static final int H = 6;
  private static final int P = 7;

  private static final String IMG_DIR = "/angular/app/img/xq3/";

  private static final String PIECE_CHARS = ".kaerchp";

  private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
        1024);
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) >= 0) {
      byteArrayOutputStream.write(buffer, 0, length);
    }
    inputStream.close();
    return byteArrayOutputStream.toByteArray();
  }

  private Image makeImageFromFile(String fileName) throws IOException {
    InputStream inputStream = this.getServletContext().getResourceAsStream(fileName);
    byte[] imageBytes = inputStreamToBytes(inputStream);
    return ImagesServiceFactory.makeImage(imageBytes);
  }

  private String getFileNameForPiece(int piece) {
    String color = piece > 0 ? "r" : "b";
    String pieceName = color + PIECE_CHARS.charAt(Math.abs(piece));
    return IMG_DIR + pieceName + ".png";
  }

  private Composite makeCompositeAt(int row, int col, int piece) throws IOException {
    Image pieceImage = makeImageFromFile(getFileNameForPiece(piece));
    Composite composite = ImagesServiceFactory.makeComposite(
        pieceImage, 308 + 5 + 60 * col, 5 + 60 * row, 1.0f, Anchor.TOP_LEFT);
    return composite;
  }

  private Image createImageFromComposites(List<Composite> composites) {
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    Image image = null;
    int start = 0;
    while (start < composites.size()) {
      int end = Math.min(start + 15 + (image == null ? 1 : 0), composites.size());
      List<Composite> subset = Lists.newArrayList();
      if (image != null) {
        subset.add(ImagesServiceFactory.makeComposite(image, 0, 0, 1.0f, Anchor.TOP_LEFT));
      }
      subset.addAll(composites.subList(start, end));
      image = imagesService.composite(subset, 1165, 609,
          Long.parseLong("FFFFFFFF", 16));
      start = end;
    }
    return image;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Image boardImage = makeImageFromFile(IMG_DIR + "board.png");
    Composite boardComposite =
        ImagesServiceFactory.makeComposite(boardImage, 308, 0, 1.0f, Anchor.TOP_LEFT);

    List<Composite> composites = Lists.newArrayList(boardComposite);

    // BLACK
    composites.add(makeCompositeAt(0, 0, -R));
    composites.add(makeCompositeAt(0, 1, -H));
    composites.add(makeCompositeAt(0, 2, -E));
    composites.add(makeCompositeAt(0, 3, -A));
    composites.add(makeCompositeAt(0, 4, -K));
    composites.add(makeCompositeAt(0, 5, -A));
    composites.add(makeCompositeAt(0, 6, -E));
    composites.add(makeCompositeAt(0, 7, -H));
    composites.add(makeCompositeAt(0, 8, -R));

    composites.add(makeCompositeAt(2, 1, -C));
    composites.add(makeCompositeAt(2, 7, -C));

    composites.add(makeCompositeAt(3, 0, -P));
    composites.add(makeCompositeAt(3, 2, -P));
    composites.add(makeCompositeAt(3, 4, -P));
    composites.add(makeCompositeAt(3, 6, -P));
    composites.add(makeCompositeAt(3, 8, -P));

    // RED
    composites.add(makeCompositeAt(9, 0, R));
    composites.add(makeCompositeAt(9, 1, H));
    composites.add(makeCompositeAt(9, 2, E));
    composites.add(makeCompositeAt(9, 3, A));
    composites.add(makeCompositeAt(9, 4, K));
    composites.add(makeCompositeAt(9, 5, A));
    composites.add(makeCompositeAt(9, 6, E));
    composites.add(makeCompositeAt(9, 7, H));
    composites.add(makeCompositeAt(9, 8, R));

    composites.add(makeCompositeAt(7, 1, C));
    composites.add(makeCompositeAt(7, 7, C));

    composites.add(makeCompositeAt(6, 0, P));
    composites.add(makeCompositeAt(6, 2, P));
    composites.add(makeCompositeAt(6, 4, P));
    composites.add(makeCompositeAt(6, 6, P));
    composites.add(makeCompositeAt(6, 8, P));

    Image result = createImageFromComposites(composites);

    resp.setContentType("image/png");
    OutputStream outputStream = resp.getOutputStream();
    outputStream.write(result.getImageData());
    outputStream.close();
  }
}
