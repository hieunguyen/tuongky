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

  var fenService, vnService, annotateService;

  beforeEach(inject(function(_fenService_, _vnService_, _annotateService_) {
    fenService = _fenService_;
    vnService = _vnService_;
    annotateService = _annotateService_;
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
});
