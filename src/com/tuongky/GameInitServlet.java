package com.tuongky;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.tuongky.backend.GameDao;
import com.tuongky.model.datastore.Game;
import com.tuongky.service.SearchService;
import com.tuongky.util.JsonUtils;

@SuppressWarnings("serial")
public class GameInitServlet extends HttpServlet {

//  private static final String USERNAME1 = "hieu";
//  private static final String USERNAME2 = "ongbe";
//  private static final String USERNAME3 = "nguoituyet";

  private static final ImmutableList<Game> INIT_GAMES = ImmutableList.of(
//      new Game(USERNAME1, GameCategory.MATCH, "Vong 1: Hua Ngan Xuyen tien thang Trieu Quoc Vinh", "Ca nhan Trung Quoc 1995"),
//      new Game(USERNAME1, GameCategory.MATCH, "Vong 1: Lu Kham tien hoa Ho Vinh Hoa", "Ca nhan Trung Quoc 1995"),
//      new Game(USERNAME2, GameCategory.MATCH, "Vong 1: Boc Phung Ba tien thua Duong Quan Lan", "Ca nhan Trung Quoc 1995"),
//      new Game(USERNAME2, GameCategory.MATCH, "Vong 2: Hua Ngan Xuyen tien hoa Ho Vinh Hoa", "Ca nhan Trung Quoc 1995"),
//      new Game(USERNAME2, GameCategory.MATCH, "Vong 2: Duong Quan Lan tien thua Lu Kham", "Ca nhan Trung Quoc 1995"),
//      new Game(USERNAME2, GameCategory.OPENING, "Cuoc 1: Thuan phao hoanh xe doi truc xe", "Thuan phao cuoc 1984"),
//      new Game(USERNAME2, GameCategory.OPENING, "Cuoc 2: Thuan phao truc xe doi hoanh xe", "Thuan phao cuoc 1984"),
//      new Game(USERNAME3, GameCategory.END_GAME, "Cuoc 1: 1 tot thang 1 tuong", "Co tan thuc dung 1990"),
//      new Game(USERNAME3, GameCategory.END_GAME, "Cuoc 2: xe thang ma 2 si", "Co tan thuc dung 1990"),
//      new Game(USERNAME3, GameCategory.END_GAME, "Cuoc 3: phao tot thang si tuong ben (1)", "Co tan thuc dung 1990"),
//      new Game(USERNAME3, GameCategory.END_GAME, "Cuoc 4: phao tot thang si tuong ben (2)", "Co tan thuc dung 1990"),
//      new Game(USERNAME3, GameCategory.END_GAME, "Cuoc 5: phao tot thang si tuong ben (3)", "Co tan thuc dung 1990"),
//      new Game(USERNAME1, GameCategory.END_GAME, "Cuoc 6: phao tot thang si tuong ben (4)", "Co tan thuc dung 1990"),
//      new Game(USERNAME1, GameCategory.END_GAME, "Cuoc 7: phao tot thang si tuong ben (5)", "Co tan thuc dung 1990")
  );

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    GameDao gameDao = new GameDao();
    for (int i = 0; i < INIT_GAMES.size(); i++) {
      Game game = INIT_GAMES.get(i);
      game.setData("Game data of game " + (i + 1));
      SearchService.indexGame(gameDao.add(game));
    }
    resp.setContentType(Constants.CT_JSON);
    resp.getWriter().println(JsonUtils.toJson("status", "ok"));
  }
}
