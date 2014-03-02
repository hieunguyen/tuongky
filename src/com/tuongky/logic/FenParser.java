package com.tuongky.logic;

import static com.tuongky.logic.Constants.*;

public class FenParser {

  private static final String RED_PIECES    = ".KAERCHP";

  private FenParser() {}

  public static Fen parse(String fenString) {
    int[][] board = new int[ROWS][COLS];
    int turn;
    int halfMoveClock = -1;
    int fullMove = -1;

    String[] s = fenString.split(" ");
    if (s.length < 2) { // has <2 fields.
      return null;
    }

    String[] rows = s[0].split("/");
    if (rows.length < 10) { // has <10 rows.
      return null;
    }

    for (int i = 0; i < ROWS; i++) {
      int col = 0;
      for (int j = 0; j < rows[i].length() && col < COLS; j++) {
        char c = rows[i].charAt(j);
        if (Character.isDigit(c)) {
          col += c - '0';
        } else {
          int pieceType = RED_PIECES.indexOf(Character.toUpperCase(c));
          if (pieceType <= 0) {
            return null;
          }
          board[i][col] = pieceType;
          if (Character.isLowerCase(c)) {
            board[i][col] *= -1;
          }
          col++;
        }
      }
      if (col != COLS) {
        return null;
      }
    }
    turn = s[1].toLowerCase().equals("w") ? RED : BLACK;
    if (s.length >= 5) {
      try {
        halfMoveClock = Integer.parseInt(s[4]);
      } catch (Exception e) {
        halfMoveClock = -1;
      }
    }
    if (s.length >= 6) {
      try {
        fullMove = Integer.parseInt(s[5]);
      } catch (Exception e) {
        fullMove = -1;
      }
    }

    return new Fen(board, turn, halfMoveClock, fullMove);
  }

  public static boolean isValidFen(String fen) {
    return fen != null && parse(fen) != null;
  }
}
