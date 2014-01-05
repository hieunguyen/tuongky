'use strict';

describe('Position class', function() {

	var FEN = 'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR w - - - 1';

	var fenService, position;

	function fromFen(fen) {
		var pos = fenService.fen2pos(fen);
		var position = new Position();
		position.init(pos.board, pos.turn);
		return position;
	}

	beforeEach(module('tkApp.services'));

	beforeEach(inject(function(_fenService_) {
	  fenService = _fenService_;
	  position = fromFen(FEN);
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

	it('should test isChecked correctly.', function() {
		var pos = fromFen('4k4/9/9/9/9/4R4/9/9/9/4K4 b - - - 1');
		expect(pos.isChecked()).toBeTruthy();

		pos = fromFen('4k4/4a4/9/9/9/4R4/9/9/9/4K4 b - - - 1');
		expect(pos.isChecked()).toBeFalsy();

		pos = fromFen('rheakaehr/9/1c5c1/p1p1p1p1p/9/4C4/P1P1P1P1P/1C7/9/RHEAKAEHR b - - - 1');
		expect(pos.isChecked()).toBeTruthy();

		pos = fromFen('rheakaehr/6H2/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAE1R b - - - 1');
		expect(pos.isChecked()).toBeTruthy();
  	expect(pos.isChecked(RED)).toBeFalsy();

		pos = fromFen('1heakaehr/6H2/1c5c1/p1p1p1p1p/9/4P4/P1P1r1P1P/1C5C1/9/RHEAKAE1R b - - - 1');
		expect(pos.isChecked()).toBeTruthy();
		expect(pos.isChecked(RED)).toBeTruthy();

		pos = fromFen('1hea1aehr/3Pk1H2/1c5c1/p1p1p1p1p/9/4P4/P3r1P1P/1C5C1/9/RHEAKAE1R b - - - 1');
		expect(pos.isChecked()).toBeTruthy();

		pos = fromFen('1hea1aehr/3Pk1H2/1c5c1/p1p1p1p1p/9/4P4/P3r1P1P/1C5C1/9/RHEAKAE1R b - - - 1');
		expect(pos.isChecked()).toBeTruthy();

		pos = fromFen('rheakaehr/5cH2/9/p1p1p1p1p/4c4/3C5/P1P1P1P1P/4C1H2/9/R1EAKAE1R b - - - 1');
		expect(pos.isChecked()).toBeFalsy();
		expect(pos.isChecked(RED)).toBeFalsy();
	});

	it('should be able to recognize CheckMate positions.', function() {
		var pos = fromFen('rheakaehr/9/1c5c1/p1p1C1p1p/9/4C4/P1P1P1P1P/9/9/RHEAKAEHR b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('rhea1aehr/4k1HC1/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C7/9/RHEAKAE1R b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('rheakaehr/9/7c1/p1p1C1p1p/1c7/4C4/P1P1P1P1P/9/9/RHEAKAEHR b - - - 1');
		expect(pos.isCheckmated()).toBeFalsy();

		pos = fromFen('r1eakaehr/9/7c1/p1p1C1p1p/1c1h5/4C4/P1P1P1P1P/9/9/RHEAKAEHR b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('3aka3/4h4/9/9/9/9/9/9/9/4K4 b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('3aka3/4h4/9/9/9/9/9/9/9/4K4 b - - - 1');
		expect(pos.isCheckmated(RED)).toBeFalsy();

		pos = fromFen('3aka3/4h4/9/2p6/9/9/9/9/9/4K4 b - - - 1');
		expect(pos.isCheckmated()).toBeFalsy();

		pos = fromFen('rhea1ae1r/4k1HC1/1c1h3c1/p1p1p1p1p/9/9/P1P1P1P1P/1C7/9/RHEAKAE1R b - - - 1');
		expect(pos.isCheckmated()).toBeFalsy();

		pos = fromFen('rh1a1ae1r/4k1HC1/1c1he2c1/p1p1p1p1p/9/9/P1P1P1P1P/1C7/9/RHEAKAE1R b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('rh1a1ae1r/4k1HC1/3he2c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5c1/9/RHEAKAE1R b - - - 1');
		expect(pos.isCheckmated()).toBeFalsy();

		pos = fromFen('r2a1ae1r/4k1HC1/3he2c1/p1p1p1p1p/9/7h1/P1P1P1P1P/1C5c1/9/RHEAKAE1R b - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('4k4/9/9/9/9/9/9/5h3/9/3K5 w - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('4k4/9/9/9/9/9/9/9/2p6/3K5 w - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('4k4/9/9/9/9/9/9/9/2p6/5K3 w - - - 1');
		expect(pos.isCheckmated()).toBeFalsy();

		pos = fromFen('4k4/5c3/5a3/9/9/9/9/9/9/5K3 w - - - 1');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('9/9/3k1H3/9/9/9/9/9/9/4K4 b - - - 39');
		expect(pos.isCheckmated()).toBeTruthy();

		pos = fromFen('9/9/3k1H3/9/9/9/9/9/9/4K4 b - - - 39');
		expect(pos.isCheckmated(RED)).toBeFalsy();
	});

	it('should be able to generate all valid moves.', function() {
		var pos = fromFen('4k4/9/9/9/9/9/9/9/9/3K5 w - - - 1');
		expect(pos.genMoves().length).toBe(2);

		pos = fromFen('4k4/9/9/9/9/9/9/9/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(3);

		pos = fromFen('4k4/2h6/9/9/9/9/6H2/9/9/3K5 w - - - 1');
		expect(pos.genMoves().length).toBe(10);

		pos = fromFen('4k4/2h6/9/9/9/9/6H2/9/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(8);

		pos = fromFen('4k4/2h6/9/6p2/9/9/6H2/6C2/9/3K5 w - - - 1');
		expect(pos.genMoves().length).toBe(19);

		pos = fromFen('4k4/2h6/9/6p2/9/9/6H2/6C2/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(9);

		pos = fromFen('9/2h1k4/9/6p2/9/9/6H2/6C2/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(11);

		pos = fromFen('9/2h1k4/9/6p2/1r7/9/6H2/6C2/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(28);

		pos = fromFen('9/2h1k4/9/6p2/1r7/9/6H1r/6C2/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(39);

		pos = fromFen('9/2h1k4/9/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(35);

		pos = fromFen('6e2/2h1k4/9/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(37);

		pos = fromFen('2e3e2/2h1k4/9/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(39);

		pos = fromFen('6e2/2h1k4/4e4/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(37);

		pos = fromFen('3a1ae2/2h1k4/4e4/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(37);

		pos = fromFen('3akae2/2h6/4e4/6p2/1r7/8h/6H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(36);

		pos = fromFen('3akae2/2h6/4e4/6p2/1r7/8h/2p3H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(39);

		pos = fromFen('3akae2/2h6/4e1c2/6p2/1r7/8h/2p3H1r/6C1p/9/3K5 b - - - 1');
		expect(pos.genMoves().length).toBe(44);

		pos = fromFen('4k4/9/9/9/9/9/9/9/9/4K4 w - - - 1');
		expect(pos.genMoves().length).toBe(3);

		pos = fromFen('4k4/9/9/9/9/9/9/9/4K4/9 w - - - 1');
		expect(pos.genMoves().length).toBe(4);

		pos = fromFen('2P1k4/9/9/9/6P2/9/8P/9/4K4/9 w - - - 1');
		expect(pos.genMoves().length).toBe(10);

		pos = fromFen('2P1k4/9/9/9/6P2/9/8P/8H/4K3H/9 w - - - 1');
		expect(pos.genMoves().length).toBe(14);

		pos = fromFen('2P1k4/4P4/9/9/6P2/9/8P/8H/4K3H/9 w - - - 1');
		expect(pos.genMoves().length).toBe(17);

		pos = fromFen('2P1k4/4P4/9/9/6P2/9/8P/8H/4K3H/9 b - - - 1');
		expect(pos.genMoves().length).toBe(3);

		pos = fromFen('2P1k4/4P4/9/9/6P2/9/8P/8H/4K3H/4R4 w - - - 1');
		expect(pos.genMoves().length).toBe(24);
  	expect(pos.genMoves(BLACK).length).toBe(3);
	});
});
