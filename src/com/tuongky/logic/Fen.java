package com.tuongky.logic;

import com.google.common.base.Preconditions;

public class Fen {

  private final int[][] board;
  private final int turn;
  private final int halfMoveClock;
  private final int fullMove;

  public Fen(int[][] board, int turn, int halfMoveClock, int fullMove) {
    Preconditions.checkArgument(board.length == Constants.ROWS);
    Preconditions.checkArgument(board[0].length == Constants.COLS);
    this.board = cloneArray(board);
    this.turn = turn;
    this.halfMoveClock = halfMoveClock;
    this.fullMove = fullMove;
  }

  private int[][] cloneArray(int[][] a) {
    int[][] b = new int[a.length][a[0].length];
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < a[0].length; j++) {
        b[i][j] = a[i][j];
      }
    }
    return b;
  }

  public int[][] getBoard() {
    return cloneArray(board);
  }

  public int getTurn() {
    return turn;
  }

  public int getHalfMoveClock() {
    return halfMoveClock;
  }

  public int getFullMove() {
    return fullMove;
  }
}
