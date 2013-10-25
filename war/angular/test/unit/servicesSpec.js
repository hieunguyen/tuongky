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

  var fenService, vnService;

  beforeEach(inject(function(_fenService_, _vnService_) {
    fenService = _fenService_;
    vnService = _vnService_;
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

  describe('vnService', function() {

    it('should remove annotation correctly.', function() {
      expect(vnService.normalize('à')).toBe('a');
      expect(vnService.normalize(' ')).toBe(' ');
      expect(vnService.normalize('Ô')).toBe('o');
      expect(vnService.normalize('Ư')).toBe('u');
      expect(vnService.normalize('Ế')).toBe('e');
      expect(vnService.normalize('đại việt')).toBe('dai viet');
      expect(vnService.normalize(
          'Hứa Ngân Xuyên')).toBe('hua ngan xuyen');
      expect(vnService.normalize(
          'Nguyễn Anh Quân')).toBe('nguyen anh quan');
      expect(vnService.normalize(
          'Phạm Quốc Hương')).toBe('pham quoc huong');
      expect(vnService.normalize(
          'Hồ Vinh Hoa')).toBe('ho vinh hoa');
      expect(vnService.normalize(
          'Lý Lai Quần')).toBe('ly lai quan');
      expect(vnService.normalize(
          'Thăng Long Kỳ Đạo')).toBe('thang long ky dao');
      expect(vnService.normalize(
          'Quất Trung Bí')).toBe('quat trung bi');
      expect(vnService.normalize(
          'Pháo Đầu Mã Đội')).toBe('phao dau ma doi');
      expect(vnService.normalize(
          'Bình Phong Mã Tiến tam binh')).toBe('binh phong ma tien tam binh');
      expect(vnService.normalize(
          'Ngũ Dương Bôi')).toBe('ngu duong boi');
      expect(vnService.normalize(
          'Đặc cấp quốc tế đại sư')).toBe('dac cap quoc te dai su');
      expect(vnService.normalize(
          'Vương Trùng Dương')).toBe('vuong trung duong');
      expect(vnService.normalize(
          'Trương Vô Kỵ')).toBe('truong vo ky');
    });

    it('should do the stemming correctly.', function() {
      expect(vnService.normalize(
          'xe Xe Xa xa')).toBe('xe xe xe xe');
      expect(vnService.normalize(
          'Thuan phao truc xa')).toBe('thuan phao truc xe');
    });

    it('should do the normalization correctly.', function() {
      expect(vnService.normalize(
          'Thuận pháo trực xa')).toBe('thuan phao truc xe');
      expect(vnService.normalize(
          'Tam bộ hổ')).toBe('tam bo ho');
    });
  });
});
