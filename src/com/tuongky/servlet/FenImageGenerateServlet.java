package com.tuongky.servlet;

import static com.tuongky.logic.Constants.*;

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
import com.tuongky.backend.ProblemDao;
import com.tuongky.logic.Fen;
import com.tuongky.logic.FenParser;
import com.tuongky.model.datastore.Problem;

// Red: 帅 仕 相 车 炮 马 兵
// Black: 将 士 象 车 炮 马 卒

@SuppressWarnings("serial")
public class FenImageGenerateServlet extends HttpServlet {

  private static final String IMG_DIR = "/angular/app/img/xq3/";
  private static final String PIECE_CHARS = ".kaerchp";

  private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
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

  private String extractProblemId(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    if (pathInfo == null || pathInfo.length() < 2) {
      return null;
    }
    try {
      int problemId = Integer.parseInt(pathInfo.substring(1));
      return String.valueOf(problemId);
    } catch(NumberFormatException e) {
      return null;
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String problemId = extractProblemId(req);
    if (problemId == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Problem not found.");
      return;
    }

    Problem problem = ProblemDao.instance.getById(Long.parseLong(problemId));
    String fenString = problem.getFen();

    Image boardImage = makeImageFromFile(IMG_DIR + "board.png");
    Composite boardComposite =
        ImagesServiceFactory.makeComposite(boardImage, 308, 0, 1.0f, Anchor.TOP_LEFT);

//    fenString = "6P2/2H2h3/3a1kC2/5C3/4P4/9/7cR/5c3/4p2r1/5K3 w - - - 1";

    Fen fen = null;
    if (fenString != null) {
      fen = FenParser.parse(fenString);
    }

    if (fenString == null || fen == null) {
      fen = FenParser.parse(STARTING_FEN);
    }

    List<Composite> composites = Lists.newArrayList(boardComposite);

    int[][] board = fen.getBoard();
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        if (board[i][j] != EMPTY) {
          composites.add(makeCompositeAt(i, j, board[i][j]));
        }
      }
    }
    Image result = createImageFromComposites(composites);

    resp.setContentType("image/png");
    OutputStream outputStream = resp.getOutputStream();
    outputStream.write(result.getImageData());
    outputStream.close();
  }
}
