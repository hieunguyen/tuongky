'use strict';

describe('tkApp.services:', function() {
  var FEN = 'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR w - - - 1';
  var URL = 'rheakaehr.9.1c5c1.p1p1p1p1p.9.9.P1P1P1P1P.1C5C1.9.RHEAKAEHR_w_-_-_-_1';

  var FEN2 = '7r1/5k3/9/5PP1H/9/9/9/9/9/4K4 w - - - 1';
  var URL2 = '7r1.5k3.9.5PP1H.9.9.9.9.9.4K4_w_-_-_-_1';

  var FEN3 = '3ak1b2/r1P1a4/3n5/2RR5/6b2/9/9/4BA3/4A4/2B1K4 b - - 48 72';
  var FEN4 = '3ak1e2/r1P1a4/3n5/2RR5/6b2/9/9/4EA3/4A4/2E1K4 b - - 48 72';
  var FEN5 = '4k4/4a4/3abn3/1N7/6b2/2B3B2/2C6/3A5/4AK3/9 w - - 121 129';
  var FEN6 = '4k4/4a4/3aeh3/1H7/6e2/2E3E2/2C6/3A5/4AK3/9 w - - 121 129';

  var INVALID_FENS = [
    '7r1/5k3/9/5PP4H/9/9/9/9/9/4K3 w - - - 1',
    '7r1/5k3/9/5PP1H/9/9/9/9/8/4K4 w - - - 1',
    '7r1/5k3/9/5PP1H/9/9/9/9/94K4 w - - - 1',
    '7r1/5k3/9/5PP4H/9/9/9/9/94K4 w - - - 1',
    'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR'
  ];

  beforeEach(module('tkApp.services'));

  var fenService, vnService, annotateService, gameService;

  beforeEach(inject(function(_fenService_, _vnService_, _annotateService_, _gameService_) {
    fenService = _fenService_;
    vnService = _vnService_;
    annotateService = _annotateService_;
    gameService = _gameService_;
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

    it('should recognize either b or e for elephant.', function() {
      expect(fenService.fen2pos(FEN3)).toBeTruthy();
      expect(fenService.fen2pos(FEN4)).toBeTruthy();
      expect(fenService.fen2pos(FEN5)).toBeTruthy();
      expect(fenService.fen2pos(FEN6)).toBeTruthy();
    });
  });

  describe('vnService', function() {

    it('should remove annotation correctly.', function() {
      expect(vnService.removeAnnotation('à')).toBe('a');
      expect(vnService.removeAnnotation(' ')).toBe(' ');
      expect(vnService.removeAnnotation('Ô')).toBe('o');
      expect(vnService.removeAnnotation('Ư')).toBe('u');
      expect(vnService.removeAnnotation('Ế')).toBe('e');
      expect(vnService.removeAnnotation('đại việt')).toBe('dai viet');
      expect(vnService.removeAnnotation(
          'Hứa Ngân Xuyên')).toBe('hua ngan xuyen');
      expect(vnService.removeAnnotation(
          'Nguyễn Anh Quân')).toBe('nguyen anh quan');
      expect(vnService.removeAnnotation(
          'Phạm Quốc Hương')).toBe('pham quoc huong');
      expect(vnService.removeAnnotation(
          'Hồ Vinh Hoa')).toBe('ho vinh hoa');
      expect(vnService.removeAnnotation(
          'Lý Lai Quần')).toBe('ly lai quan');
      expect(vnService.removeAnnotation(
          'Thăng Long Kỳ Đạo')).toBe('thang long ky dao');
      expect(vnService.removeAnnotation(
          'Quất Trung Bí')).toBe('quat trung bi');
      expect(vnService.removeAnnotation(
          'Pháo Đầu Mã Đội')).toBe('phao dau ma doi');
      expect(vnService.removeAnnotation(
          'Bình Phong Mã Tiến tam binh')).toBe('binh phong ma tien tam binh');
      expect(vnService.removeAnnotation(
          'Ngũ Dương Bôi')).toBe('ngu duong boi');
      expect(vnService.removeAnnotation(
          'Đặc cấp quốc tế đại sư')).toBe('dac cap quoc te dai su');
      expect(vnService.removeAnnotation(
          'Vương "Trùng  ""Dương')).toBe('vuong "trung  ""duong');
      expect(vnService.removeAnnotation(
          'Trương "Vô" Kỵ')).toBe('truong "vo" ky');
    });

    it('should do the stemming correctly.', function() {
      expect(vnService.stems(
          'xe Xe Xa xa')).toBe('xe xe xe xe');
      expect(vnService.stems(
          'Thuan phao truc xa')).toBe('thuan phao truc xe');
    });

    it('should do the normalization correctly.', function() {
      expect(vnService.normalize(
          'Thuận pháo trực xa')).toBe('thuan phao truc xe');
      expect(vnService.normalize(
          'Thuận pháo "trực xa"')).toBe('thuan phao truc xe');
      expect(vnService.normalize(
          'Tam bộ hổ')).toBe('tam bo ho');
      expect(vnService.normalize(
          'Tam   "bộ" hổ""')).toBe('tam bo ho');
      expect(vnService.normalize(
          'Tam   "bộ" hổ""123-xa9-xa')).toBe('tam bo ho 123 xa9 xe');
      expect(vnService.normalize(
          'Pháo tốt thắng sỹ voi bền')).toBe('phao tot thang si tuong ben');
      expect(vnService.normalize(undefined)).toBe(undefined);
      expect(vnService.normalize(' ')).toBe('');
    });
  });

  describe('annotateService', function() {

    it('should annotate correctly.', function() {
      expect(annotateService.annotate('test', 'test'))
          .toBe('<em>test</em>');
      expect(annotateService.annotate('Nguyễn Anh Quân', 'quan'))
          .toBe('Nguyễn Anh <em>Quân</em>');
      expect(annotateService.annotate('xã', 'xe'))
          .toBe('<em>xã</em>');
      expect(annotateService.annotate('xã đ-', 'xe "phao ma" đ', 'b'))
          .toBe('<b>xã</b> <b>đ</b>-');
      expect(annotateService.annotate('xã "pháo mã"', 'xe phao ma'))
          .toBe('<em>xã</em> "<em>pháo</em> <em>mã</em>"');
      expect(annotateService.annotate(' -+xã   "pháo "mã"787 ', 'xa phao ma 787'))
          .toBe(' -+<em>xã</em>   "<em>pháo</em> "<em>mã</em>"<em>787</em> ');
    });
  });

  describe('gameService', function() {

    function createBoard1() {
      return [
        [-R, 0, -E, -A, -K, -A, -E, -H, -R],
        [0, 0, 0, P, 0, P, 0, 0, 0],
        [0, -C, 0, P, 0, P, 0, 0, 0],
        [-P, 0, -P, P, -P, P, -P, 0, -P],
        [0, -H, 0, 0, 0, P, 0, 0, 0],
        [0, 0, E, 0, 0, 0, 0, C, 0],
        [P, 0, 0, 0, 0, H, 0, 0, 0],
        [0, -C, 0, 0, 0, 0, 0, C, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [R, H, E, A, K, A, 0, 0, R]
      ];
    }

    function createPosition() {
      var encodedFen = '3a1RC2.4k4.h3e4.p3p2rp.9.4c4.P5P1P.4E1C2.4A4.2EA1K3_w_-_-_-_1';
      return fenService.fen2pos(fenService.url2fen(encodedFen));
    }

    function ept(humanMove, x, y, u, v) {
      var move = gameService.parseHumanMove(humanMove);
      if (x === undefined) {
        expect(move).not.toBeDefined();
        return;
      }
      expect(move).toBeDefined();
      expect(move.x).toBe(x);
      expect(move.y).toBe(y);
      expect(move.u).toBe(u);
      expect(move.v).toBe(v);
    }

    it('should produce correct human position', function() {
      var pos = createPosition();
      gameService.init(pos.board, pos.turn);
      expect(gameService.produceHumanPosition(0, 6)).toBe('Pt');
    })

    it('should produce correct human moves.', function() {
      gameService.init();
      expect(gameService.produceHumanMove(7, 7, 7, 4)).toBe('P2-5');

      gameService.init(createBoard1());
      expect(gameService.produceHumanMove(7, 7, 7, 4)).toBe('Ps-5');
      expect(gameService.produceHumanMove(5, 7, 5, 4)).toBe('Pt-5');
      expect(gameService.produceHumanMove(1, 5, 1, 4)).toBe('C41-5');
      expect(gameService.produceHumanMove(4, 5, 4, 4)).toBe('C44-5');
      expect(gameService.produceHumanMove(1, 3, 1, 2)).toBe('C6t-7');
      expect(gameService.produceHumanMove(2, 3, 2, 2)).toBe('C6g-7');
      expect(gameService.produceHumanMove(3, 3, 3, 2)).toBe('C6s-7');
      expect(gameService.produceHumanMove(9, 2, 7, 4)).toBe('Vs.5');
      expect(gameService.produceHumanMove(5, 2, 7, 4)).toBe('Vt/5');

      gameService.init(createBoard1(), BLACK);
      expect(gameService.produceHumanMove(2, 1, 2, 3)).toBe('Ps-4');
      expect(gameService.produceHumanMove(7, 1, 7, 3)).toBe('Pt-4');
    });

    it('should parse human moves correctly.', function() {
      gameService.init();
      ept('P2-5', 7, 7, 7, 4);
      ept('P2.7', 7, 7, 0, 7);
      ept('M2.5');
      ept('M2.3', 9, 7, 7, 6);
      ept('M2.1', 9, 7, 7, 8);
      ept('V3.5', 9, 6, 7, 4);
      ept('V7.5', 9, 2, 7, 4);

      gameService.init(createBoard1());
      ept('Ps-5', 7, 7, 7, 4);
      ept('Pt-5', 5, 7, 5, 4);
      ept('C41-5', 1, 5, 1, 4);
      ept('C44-5', 4, 5, 4, 4);
      ept('C6t-7', 1, 3, 1, 2);
      ept('C6g-7', 2, 3, 2, 2);
      ept('C6s-7', 3, 3, 3, 2);
      ept('Vs.5', 9, 2, 7, 4);
      ept('Vt/5', 5, 2, 7, 4);
      ept('M4.5', 6, 5, 4, 4);
      ept('X1.4', 9, 8, 5, 8);
      ept('X9.4', 9, 0, 5, 0);

      gameService.init(createBoard1(), BLACK);
      ept('Ps-4', 2, 1, 2, 3);
      ept('Pt-4', 7, 1, 7, 3);
      ept('M2.4', 4, 1, 5, 3);

      var pos = createPosition();
      gameService.init(pos.board, pos.turn);
      ept('pt/3', 0, 6, 3, 6);
    });
  });
});
