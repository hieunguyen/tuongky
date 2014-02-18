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
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class FenImageGenerateServlet extends HttpServlet {

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

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Image backgroundImage = makeImageFromFile("/angular/app/img/xq2/board.jpg");
    Composite backgroundComposite =
        ImagesServiceFactory.makeComposite(backgroundImage, 340 + 0, 0, 1.0f, Anchor.TOP_LEFT);

    Image overlayImage = makeImageFromFile("/angular/app/img/xq2/br.gif");
    Composite overlayComposite = ImagesServiceFactory.makeComposite(
        overlayImage, 340 + 4 + 57 * 0, 0, 1.0f, Anchor.TOP_LEFT);

    Image overlayImage2 = makeImageFromFile("/angular/app/img/xq2/br.gif");
    Composite overlayComposite2 = ImagesServiceFactory.makeComposite(
        overlayImage2, 340 + 4 + 57 * 8, 0, 1.0f, Anchor.TOP_LEFT);

    Image overlayImage3 = makeImageFromFile("/angular/app/img/xq2/rr.gif");
    Composite overlayComposite3 = ImagesServiceFactory.makeComposite(
        overlayImage3, 340 + 4 + 57 * 8, 3 + 57 * 9, 1.0f, Anchor.TOP_LEFT);

    Image overlayImage4 = makeImageFromFile("/angular/app/img/xq2/rh.gif");
    Composite overlayComposite4 = ImagesServiceFactory.makeComposite(
        overlayImage4, 340 + 4 + 57 * 7, 3 + 57 * 9, 1.0f, Anchor.TOP_LEFT);

    List<Composite> list = Lists.newArrayList(
        backgroundComposite, overlayComposite, overlayComposite2, overlayComposite3);
    list.add(overlayComposite4);

//    Image result = ImagesServiceFactory.getImagesService().composite(list, 521, 577,
//        Long.parseLong("FF00FF00", 16));

    Image result = ImagesServiceFactory.getImagesService().composite(list, 1200, 630,
        Long.parseLong("FFFFFFFF", 16));

    resp.setContentType("image/png");
    OutputStream outputStream = resp.getOutputStream();
    outputStream.write(result.getImageData());
    outputStream.close();
  }
}
