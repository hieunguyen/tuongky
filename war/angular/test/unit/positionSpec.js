'use strict';

describe('Position class', function() {

	var FEN = 'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR w - - - 1';

	var fenService, position;

	beforeEach(module('tkApp.services'));

	beforeEach(inject(function(_fenService_) {
	  fenService = _fenService_;
	  var pos = fenService.fen2pos(FEN);
	  position = new Position();
	  position.init(pos.board, pos.turn);
	}));

	function toMove(x, y, u, v) {
		var src = (x + 3) << 4 | (y + 3);
		var dst = (u + 3) << 4 | (v + 3);
		return src << 8 | dst;
	}

	it('should recognize valid moves.', function() {
		// T, S, V, X, P, M, B; . - /; t g s.

		// X9.1
		expect(position.isValidMove(toMove(9, 0, 8, 0))).toBeTruthy();

		// X9.2
		expect(position.isValidMove(toMove(9, 0, 7, 0))).toBeTruthy();

		// X1.1
		expect(position.isValidMove(toMove(9, 8, 8, 8))).toBeTruthy();

		// X1.2
		expect(position.isValidMove(toMove(9, 8, 7, 8))).toBeTruthy();

		// P2-5
		expect(position.isValidMove(toMove(7, 7, 7, 4))).toBeTruthy();

		// P2.7
		expect(position.isValidMove(toMove(7, 7, 0, 7))).toBeTruthy();

		// P8.7
		expect(position.isValidMove(toMove(7, 1, 0, 1))).toBeTruthy();

		// B5.1
		expect(position.isValidMove(toMove(6, 4, 5, 4))).toBeTruthy();

		// B1.1
		expect(position.isValidMove(toMove(6, 0, 5, 0))).toBeTruthy();

		// M2.3
		expect(position.isValidMove(toMove(9, 7, 7, 6))).toBeTruthy();

		// M2.1
		expect(position.isValidMove(toMove(9, 7, 7, 8))).toBeTruthy();

		// M8.9
		expect(position.isValidMove(toMove(9, 1, 7, 0))).toBeTruthy();

		// M8.7
		expect(position.isValidMove(toMove(9, 1, 7, 2))).toBeTruthy();

		// T5.1
		expect(position.isValidMove(toMove(9, 4, 8, 4))).toBeTruthy();

		// S4.5
		expect(position.isValidMove(toMove(9, 5, 8, 4))).toBeTruthy();

		// S6.5
		expect(position.isValidMove(toMove(9, 3, 8, 4))).toBeTruthy();

		// V3.5
		expect(position.isValidMove(toMove(9, 6, 7, 4))).toBeTruthy();

		// V7.5
		expect(position.isValidMove(toMove(9, 2, 7, 4))).toBeTruthy();

		// V3.1
		expect(position.isValidMove(toMove(9, 6, 7, 8))).toBeTruthy();

		// V7.9
		expect(position.isValidMove(toMove(9, 2, 7, 0))).toBeTruthy();
	});

	it('should recognize invalid moves.', function() {
		// X1.1
		expect(position.isValidMove(toMove(0, 0, 1, 0))).toBeFalsy();

		// X9.3
		expect(position.isValidMove(toMove(9, 0, 6, 0))).toBeFalsy();

		// X9-8
		expect(position.isValidMove(toMove(9, 0, 9, 1))).toBeFalsy();

		// X9-5
		expect(position.isValidMove(toMove(9, 0, 9, 4))).toBeFalsy();

		// M2.4
		expect(position.isValidMove(toMove(9, 7, 8, 5))).toBeFalsy();

		// M8.6
		expect(position.isValidMove(toMove(9, 1, 8, 3))).toBeFalsy();

		// P2.5
		expect(position.isValidMove(toMove(7, 7, 2, 7))).toBeFalsy();

		// P2.6
		expect(position.isValidMove(toMove(7, 7, 1, 7))).toBeFalsy();

		// B5-4
		expect(position.isValidMove(toMove(6, 4, 6, 5))).toBeFalsy();

		// B5/1
		expect(position.isValidMove(toMove(6, 4, 7, 4))).toBeFalsy();

		// B5.2
		expect(position.isValidMove(toMove(6, 4, 4, 4))).toBeFalsy();

		// V7.6
		expect(position.isValidMove(toMove(9, 2, 8, 3))).toBeFalsy();

		// T5-4
		expect(position.isValidMove(toMove(9, 4, 9, 5))).toBeFalsy();

		// S4.1
		expect(position.isValidMove(toMove(9, 5, 8, 5))).toBeFalsy();
	});

	it('should makeMove correctly.', function() {
		expect(position.turn).toBe(RED);
		// P2-5
		position.makeMove(toMove(7, 7, 7, 4));
		expect(position.turn).toBe(BLACK);

		// X1.1
		expect(position.isValidMove(toMove(0, 0, 1, 0))).toBeTruthy();

		// M8.7
		expect(position.isValidMove(toMove(0, 7, 2, 6))).toBeTruthy();

		// P8-5
		expect(position.isValidMove(toMove(2, 7, 2, 4))).toBeTruthy();
	});

	it('should unMakeMove correctly.', function() {
		expect(position.turn).toBe(RED);
		// P2-5
		position.makeMove(toMove(7, 7, 7, 4));
		expect(position.turn).toBe(BLACK);
		position.unMakeMove();
		expect(position.turn).toBe(RED);

		// P2-5
		expect(position.isValidMove(toMove(7, 7, 7, 4))).toBeTruthy();

		// X1.1
		expect(position.isValidMove(toMove(0, 0, 1, 0))).toBeFalsy();

		// M8.7
		expect(position.isValidMove(toMove(0, 7, 2, 6))).toBeFalsy();

		// P8-5
		expect(position.isValidMove(toMove(2, 7, 2, 4))).toBeFalsy();
	});
});
