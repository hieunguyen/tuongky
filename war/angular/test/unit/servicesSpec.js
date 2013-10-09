'use strict';

describe('tkApp.services:', function() {
  var FEN = 'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR w - - - 1';
  var URL = 'rheakaehr.9.1c5c1.p1p1p1p1p.9.9.P1P1P1P1P.1C5C1.9.RHEAKAEHR_w_-_-_-_1';

  var FEN2 = '7r1/5k3/9/5PP1H/9/9/9/9/9/4K4 w - - - 1';
  var URL2 = '7r1.5k3.9.5PP1H.9.9.9.9.9.4K4_w_-_-_-_1';

  var INVALID_FENS = [
    '7r1/5k3/9/5PP4H/9/9/9/9/9/4K3 w - - - 1',
    '7r1/5k3/9/5PP1H/9/9/9/9/8/4K4 w - - - 1',
    '7r1/5k3/9/5PP1H/9/9/9/9/94K4 w - - - 1',
    '7r1/5k3/9/5PP4H/9/9/9/9/94K4 w - - - 1',
    'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR'
  ];

  beforeEach(module('tkApp.services'));

  var fenService;

  beforeEach(inject(function(_fenService_) {
    fenService = _fenService_;
  }));

  describe('fenService', function() {
    it('should convert FEN to URL correctly.', function() {
      expect(fenService.fen2url(FEN)).toBe(URL);
      expect(fenService.fen2url(FEN2)).toBe(URL2);
    });

    it('should convert URL to FEN correctly.', function() {
      expect(fenService.url2fen(URL)).toBe(FEN);
      expect(fenService.url2fen(URL2)).toBe(FEN2);
    });

    it('should convert FEN to Position correctly.', function() {
      var pos = fenService.fen2pos(FEN);
      var board = pos.board;
      expect(board[0][0]).toBe(-R);
      expect(board[0][2]).toBe(-E);
      expect(board[ROWS-1][0]).toBe(R);
      expect(board[ROWS-1][COLS-2]).toBe(H);
      expect(pos.turn).toBe(RED);
      expect(pos.fullMoveNumber).toBe(1);
      expect(pos.halfMoveClock).toBe(-1);
    });

    it('should convert Position to FEN correctly.', function() {
      var pos = fenService.fen2pos(FEN);
      var fen = fenService.pos2fen(pos);
      expect(fen).toBe(FEN);

      var pos2 = fenService.fen2pos(FEN2);
      var fen2 = fenService.pos2fen(pos2);
      expect(fen2).toBe(FEN2);
    });

    it('should recognize invalid FENs correctly.', function() {
      for (var i = 0; i < INVALID_FENS.length; i++) {
        expect(fenService.fen2pos(INVALID_FENS[i])).toBeFalsy();
      }
    });
  });
});
