var BOARD_SIZE = 256;
var PIECE_TYPES = [
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	K, A, A, E, E, R, R, C, C, H, H, P, P, P, P, P,
	K, A, A, E, E, R, R, C, C, H, H, P, P, P, P, P];

var KING_MOVE_TAB	= [-0x10, -0x01, +0x01, +0x10];
var ADVISOR_MOVE_TAB	= [-0x11, -0x0f, +0x0f, +0x11];
var ELEPHANT_MOVE_TAB	= [-0x22, -0x1e, +0x1e, +0x22];
var HORSE_MOVE_TAB = [-0x21, -0x1f, -0x12, -0x0e, +0x0e, +0x12, +0x1f, +0x21];

var LEGAL_MOVE_TAB = [
                       0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0
];

var HORSE_LEG_TAB = [
	                            0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,-16,  0,-16,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0, 16,  0, 16,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0
];

var IN_BOARD, IN_PALACE, LEGAL_MOVE_TAB, HORSE_LEG_TAB;
var ROOK_ROW_CAP_MASK, ROOK_ROW_NO_CAP_MASK;
var ROOK_COL_CAP_MASK, ROOK_COL_NO_CAP_MASK;
var CANNON_ROW_CAP_MASK, CANNON_COL_CAP_MASK;
var ROOK_ROW_CAP_TAB, ROOK_ROW_NO_CAP_TAB, CANNON_ROW_CAP_TAB;
var ROOK_COL_CAP_TAB, ROOK_COL_NO_CAP_TAB, CANNON_COL_CAP_TAB;

var KING_MOVES, ADVISOR_MOVES, ELEPHANT_MOVES, HORSE_MOVES, PAWN_MOVES;
var ELEPHANT_EYES, HORSE_LEGS;

function init_arr() {
	var args = Array.prototype.slice.call(arguments);
	if (args.length === 0) {
		return 0;
	}
	var arr = [];
	var first = args.shift();
	for (var i = 0; i < first; i++) {
		var e = init_arr.apply(window, args);
		arr.push(e);
	}
	return arr;
}

function initInBoard() {
	IN_BOARD = init_arr(BOARD_SIZE);
	for (var i = 3; i < 3 + ROWS; i++)
		for (var j = 3; j < 3 + COLS; j++) IN_BOARD[i << 4 | j] = 1;
}

function initInPalace() {
	IN_PALACE = init_arr(BOARD_SIZE);
	for (var j = 6; j < 9; j++) {
		for (var i = 3; i < 6; i++) {
			IN_PALACE[i << 4 | j] = true;
		}
		for (var i = 10; i < 13; i++) {
			IN_PALACE[i << 4 | j] = true;
		}
	}
}

function initKingBishopMoves() {
	// King & Bishop

	KING_MOVES = init_arr(BOARD_SIZE, 5);
	ADVISOR_MOVES = init_arr(BOARD_SIZE, 5);

	var src, dst, cc, i;
	for (src = 0; src < BOARD_SIZE; src++) {
		if (IN_PALACE[src]) {
			cc = 0;
			for (i = 0; i < KING_MOVE_TAB.length; i++) {
				dst = src + KING_MOVE_TAB[i];
				if (IN_PALACE[dst]) KING_MOVES[src][cc++] = dst;
			}
			cc = 0;
			for (i = 0; i < ADVISOR_MOVE_TAB.length; i++) {
				dst = src + ADVISOR_MOVE_TAB[i];
				if (IN_PALACE[dst]) ADVISOR_MOVES[src][cc++] = dst;
			}
		}
	}
}

function initElephanKnightPawnMoves() {
	// Elephant, Knight & Pawn

	ELEPHANT_MOVES = init_arr(BOARD_SIZE, 5);
	ELEPHANT_EYES = init_arr(BOARD_SIZE, 5);
	HORSE_MOVES = init_arr(BOARD_SIZE, 10);
	HORSE_LEGS = init_arr(BOARD_SIZE, 10);
	PAWN_MOVES = init_arr(BOARD_SIZE, 2, 5);

	var src, dst, cc, i, j;
	for (src = 0; src < BOARD_SIZE; src++) {
		if (IN_BOARD[src]) {
			cc = 0;
			for (i = 0; i < ELEPHANT_MOVE_TAB.length; i++) {
				dst = src + ELEPHANT_MOVE_TAB[i];
				if (IN_BOARD[dst] && ((src^dst) & 0x80) === 0) {
					ELEPHANT_MOVES[src][cc] = dst;
					ELEPHANT_EYES[src][cc] = (src + dst) >> 1;
					cc++;
				}
			}

			cc = 0;
			for (i = 0; i < HORSE_MOVE_TAB.length; i++) {
				dst = src + HORSE_MOVE_TAB[i];
				if (IN_BOARD[dst]) {
					HORSE_MOVES[src][cc] = dst;
					HORSE_LEGS[src][cc] = src + HORSE_LEG_TAB[dst - src + BOARD_SIZE];
					cc++;
				}
			}

			for (i = 0; i < 2; i++) {
				cc = 0;
				if (i === 0) dst = src - 16; else dst = src + 16;
				if (IN_BOARD[dst]) PAWN_MOVES[src][i][cc++] = dst;

				if (i===0 ? (src & 0x80) === 0 : (src & 0x80) !== 0) {
					for (j = -1; j <= 1; j += 2) {
						dst = src + j;
						if (IN_BOARD[dst]) PAWN_MOVES[src][i][cc++] = dst;
					}
				}
			}
		}
	}
}

function initRookCannonMoves() {
	// Rook & Cannon

	ROOK_ROW_NO_CAP_TAB = init_arr(COLS, 1 << COLS, 2);
	ROOK_ROW_CAP_TAB = init_arr(COLS, 1 << COLS, 2);
	CANNON_ROW_CAP_TAB = init_arr(COLS, 1 << COLS, 2);

	ROOK_COL_NO_CAP_TAB = init_arr(ROWS, 1 << ROWS, 2);
	ROOK_COL_CAP_TAB	= init_arr(ROWS, 1 << ROWS, 2);
	CANNON_COL_CAP_TAB	= init_arr(ROWS, 1 << ROWS, 2);

	ROOK_ROW_NO_CAP_MASK	= init_arr(COLS, 1 << COLS);
	ROOK_ROW_CAP_MASK = init_arr(COLS, 1 << COLS);
	CANNON_ROW_CAP_MASK = init_arr(COLS, 1 << COLS);

	ROOK_COL_NO_CAP_MASK = init_arr(ROWS, 1 << ROWS);
	ROOK_COL_CAP_MASK = init_arr(ROWS, 1 << ROWS);
	CANNON_COL_CAP_MASK = init_arr(ROWS, 1 << ROWS);

	var i, j, k;

	// ROWS
	for (i = 0; i < COLS; i++)
	for (j = 0; j < 1 << COLS; j++) {
		ROOK_ROW_NO_CAP_TAB[i][j][0] = ROOK_ROW_NO_CAP_TAB[i][j][1] = i + 3;
		ROOK_ROW_CAP_TAB[i][j][0] = ROOK_ROW_CAP_TAB[i][j][1] = i + 3;
		CANNON_ROW_CAP_TAB[i][j][0] = CANNON_ROW_CAP_TAB[i][j][1] = i + 3;
		ROOK_ROW_NO_CAP_MASK[i][j] = ROOK_ROW_CAP_MASK[i][j] = CANNON_ROW_CAP_MASK[i][j] = 0;

		for (k = i + 1; k < COLS; k++) {
			if (j >> k & 1) {
				ROOK_ROW_CAP_TAB[i][j][0] = k + 3;
				ROOK_ROW_CAP_MASK[i][j] |= 1 << (k + 3);
				break;
			}
			ROOK_ROW_NO_CAP_TAB[i][j][0] = k + 3;
			ROOK_ROW_NO_CAP_MASK[i][j] |= 1 << (k + 3);
		}

		for (k++; k < COLS; k++)
		if (j >> k & 1) {
			CANNON_ROW_CAP_TAB[i][j][0] = k + 3;
			CANNON_ROW_CAP_MASK[i][j] |= 1 << (k + 3);
			break;
		}

		for (k = i - 1; k >= 0; k--) {
			if (j >> k & 1) {
				ROOK_ROW_CAP_TAB[i][j][1] = k + 3;
				ROOK_ROW_CAP_MASK[i][j] |= 1 << (k + 3);
				break;
			}
			ROOK_ROW_NO_CAP_TAB[i][j][1] = k + 3;
			ROOK_ROW_NO_CAP_MASK[i][j] |= 1 << (k + 3);
		}

		for (k--; k >= 0; k--)
		if (j >> k & 1) {
			CANNON_ROW_CAP_TAB[i][j][1] = k + 3;
			CANNON_ROW_CAP_MASK[i][j] |= 1 << (k + 3);
			break;
		}

	}

	// COLS
	for (i = 0; i < ROWS; i++)
	for (j = 0; j < 1 << ROWS; j++) {
		ROOK_COL_NO_CAP_TAB[i][j][0] = ROOK_COL_NO_CAP_TAB[i][j][1] = i + 3;
		ROOK_COL_CAP_TAB[i][j][0] = ROOK_COL_CAP_TAB[i][j][1] = i + 3;
		CANNON_COL_CAP_TAB[i][j][0] = CANNON_COL_CAP_TAB[i][j][1] = i + 3;
		ROOK_COL_NO_CAP_MASK[i][j] = ROOK_COL_CAP_MASK[i][j] = CANNON_COL_CAP_MASK[i][j] = 0;

		for (k = i + 1; k < ROWS; k++) {
			if (j >> k & 1) {
				ROOK_COL_CAP_TAB[i][j][0] = k + 3;
				ROOK_COL_CAP_MASK[i][j] |= 1 << (k + 3);
				break;
			}
			ROOK_COL_NO_CAP_TAB[i][j][0] = k + 3;
			ROOK_COL_NO_CAP_MASK[i][j] |= 1 << (k + 3);
		}

		for (k++; k < ROWS; k++)
		if (j >> k & 1) {
			CANNON_COL_CAP_TAB[i][j][0] = k + 3;
			CANNON_COL_CAP_MASK[i][j] |= 1 << (k + 3);
			break;
		}

		for (k = i - 1; k >= 0; k--) {
			if (j >> k & 1) {
				ROOK_COL_CAP_TAB[i][j][1] = k + 3;
				ROOK_COL_CAP_MASK[i][j] |= 1 << (k + 3);
				break;
			}
			ROOK_COL_NO_CAP_TAB[i][j][1] = k + 3;
			ROOK_COL_NO_CAP_MASK[i][j] |= 1 << (k + 3);
		}

		for (k--; k >= 0; k--)
		if (j >> k & 1) {
			CANNON_COL_CAP_TAB[i][j][1] = k + 3;
			CANNON_COL_CAP_MASK[i][j] |= 1 << (k + 3);
			break;
		}

	}
}

function initThings() {
	initInBoard();
	initInPalace();
	initKingBishopMoves();
	initElephanKnightPawnMoves();
	initRookCannonMoves();
}
initThings();

var Position = function() {};

Position.prototype.init = function(board, turn) {
	this.board = init_arr(BOARD_SIZE);
	this.pieces = init_arr(48);
	this.bitRows = init_arr(16);
	this.bitCols = init_arr(16);
	this.moves = [];
	this.turn = turn;
	var startTag = [-1, 0, 1, 3, 5, 7, 9, 11];
	var count = init_arr(3, 8);
	for (var i = 0; i < ROWS; i++) {
		for (var j = 0; j < COLS; j++) {
			if (board[i][j] !== EMPTY) {
				var pType, pieceTag, side, row, col, square;
				pType = Math.abs(board[i][j]);
				side = board[i][j] > 0 ? RED : BLACK;
				pieceTag = side << 4;
				square = (i + 3) << 4 | (j + 3);
				this.addPiece(square, pieceTag + startTag[pType] + count[side][pType]);
				count[side][pType]++;
			}
		}
	}
};

Position.prototype.addPiece = function(square, piece) {
	this.board[square] = piece;
	this.pieces[piece] = square;
	this.bitRows[square >> 4] ^= 1 << (square & 0xf);
	this.bitCols[square & 0xf] ^= 1 << (square >> 4);
};

Position.prototype.removePiece = function(square, piece) {
	this.board[piece] = 0;
	this.pieces[piece] = 0;
	this.bitRows[square >> 4] ^= 1 << (square & 0xf);
	this.bitCols[square & 0xf] ^= 1 << (square >> 4);
};

Position.prototype.movePiece = function(move) {
	var src, dst, movedPiece, capturedPiece;
	src = move >> 8 & 0xff;
	dst = move & 0xff;
	movedPiece = this.board[src];
	capturedPiece = this.board[dst];
	if (!capturedPiece) {
		this.bitRows[dst >> 4] ^= 1 << (dst & 0xf);
		this.bitCols[dst & 0xf] ^= 1 << (dst >> 4);
	} else {
		this.pieces[capturedPiece] = 0;
	}
	this.board[src] = 0;
	this.board[dst] = movedPiece;
	this.pieces[movedPiece] = dst;
	this.bitRows[src >> 4] ^= 1 << (src & 0xf);
	this.bitCols[src & 0xf] ^= 1 << (src >> 4);
	return capturedPiece;
};

Position.prototype.unMovePiece = function(move, capturedPiece) {
	var src, dst, movedPiece;
	src = (move >> 8) & 0xff;
	dst = move & 0xff;
	movedPiece = this.board[dst];
	this.board[src] = movedPiece;
	this.board[dst] = capturedPiece;
	this.pieces[movedPiece] = src;
	this.bitRows[src >> 4] ^= 1 << (src & 0xf);
	this.bitCols[src & 0xf] ^= 1 << (src >> 4);
	if (!capturedPiece) {
		this.bitRows[dst >> 4] ^= 1 << (dst & 0xf);
		this.bitCols[dst & 0xf] ^= 1 << (dst >> 4);
	} else {
		this.pieces[capturedPiece] = dst;
	}
};

Position.prototype.makeMove = function(move) {
	var capturedPiece = this.movePiece(move);
	move |= capturedPiece << 16;
	this.moves.push(move);
	this.turn = this.turn ^ 3;
};

Position.prototype.unMakeMove = function() {
	var move = this.moves.pop();
	this.unMovePiece(move, move >> 16 & 0xff);
	this.turn = this.turn ^ 3;
};

Position.prototype.isValidMove = function(move) {
	var src, dst, row, col;
	var pieceTag, movedPiece, capturedPiece;

	src = (move >> 8) & 0xff;
	dst = move & 0xff;
	pieceTag = this.turn << 4;
	movedPiece = this.board[src];
	capturedPiece = this.board[dst];

  // move must be made by the in-turn player.
	if (!(movedPiece & pieceTag)) return false;

	// the color of the captured piece must be different.
	if (capturedPiece & pieceTag) return false;

	switch (PIECE_TYPES[movedPiece]) {
		case K:
			return IN_PALACE[dst] && LEGAL_MOVE_TAB[dst - src + 256] === 1;
		case A:
			return IN_PALACE[dst] && LEGAL_MOVE_TAB[dst - src + 256] === 2;
		case E:
			return !((src ^ dst) & 0x80) &&
			    !this.board[(src + dst) >> 1] &&
			    LEGAL_MOVE_TAB[dst - src + 256] === 3;
		case R:
			row = src >> 4;
			col = src & 0xf;
			if (col === (dst & 0xf)) {
				if (!capturedPiece) {
					return ROOK_COL_NO_CAP_MASK[row - 3][this.bitCols[col] >> 3] & (1 << (dst >> 4));
				} else {
					return ROOK_COL_CAP_MASK[row - 3][this.bitCols[col] >> 3] & (1 << (dst >> 4));
				}
			} else
			if (row === (dst >> 4)) {
				if (!capturedPiece) {
					return ROOK_ROW_NO_CAP_MASK[col - 3][this.bitRows[row] >> 3] & (1 << (dst & 0xf));
				} else {
					return ROOK_ROW_CAP_MASK[col - 3][this.bitRows[row] >> 3] & (1 << (dst & 0xf));
				}
			}
			return false;
		case C:
			row = src >> 4;
			col = src & 0xf;
			if (col === (dst & 0xf)) {
				if (!capturedPiece) {
					return ROOK_COL_NO_CAP_MASK[row - 3][this.bitCols[col] >> 3] & (1 << (dst >> 4));
				} else {
					return CANNON_COL_CAP_MASK[row - 3][this.bitCols[col] >> 3] & (1 << (dst >> 4));
				}
			} else
			if (row === (dst >> 4)) {
				if (!capturedPiece) {
					return ROOK_ROW_NO_CAP_MASK[col - 3][this.bitRows[row] >> 3] & (1 << (dst & 0xf));
				} else {
					return CANNON_ROW_CAP_MASK[col - 3][this.bitRows[row] >> 3] & (1 << (dst & 0xf));
				}
			}
			return false;
		case H:
			var hleg = HORSE_LEG_TAB[dst - src + 256];
			return hleg && !this.board[src + hleg];
		default: // PAWN
			if (pieceTag === 16)
				return dst === src - 16 || (!(dst & 0x80) && Math.abs(dst - src) === 1);
			else
				return dst === src + 16 || ((dst & 0x80) && Math.abs(dst - src) === 1);
	};
};

Position.prototype.isChecked = function(opt_player) {
	var player = opt_player || this.turn;
	var src, dst, rank, file, pieceTag, y;

	pieceTag = 48 - (player << 4);
	src = this.pieces[48 - pieceTag]; // KING

	rank = src >> 4;
	file = src & 0xf;

	// KING kills KING
	dst = this.pieces[pieceTag];
	if (dst) {
		y = dst & 0xf;
		if (y === file && ROOK_COL_CAP_MASK[rank - 3][this.bitCols[file] >> 3] & (1 << (dst >> 4))) return true;
	}

	// ROOK kills KING
	for (var i = 5; i <= 6; i++) {
		dst = this.pieces[pieceTag + i];
		if (dst) {
			if ((dst >> 4) === rank) {
				if (ROOK_ROW_CAP_MASK[file - 3][this.bitRows[rank] >> 3] & (1 << (dst & 0xf))) return true;
			} else
			if ((dst & 0xf) === file) {
				if (ROOK_COL_CAP_MASK[rank - 3][this.bitCols[file] >> 3] & (1 << (dst >> 4))) return true;
			}
		}
	}

	// CANNON kills KING
	for (var i = 7; i <= 8; i++) {
		dst = this.pieces[pieceTag + i];
		if (dst) {
			if ((dst >> 4) === rank) {
				if (CANNON_ROW_CAP_MASK[file - 3][this.bitRows[rank] >> 3] & (1 << (dst & 0xf))) return true;
			} else
			if ((dst & 0xf) === file) {
				if (CANNON_COL_CAP_MASK[rank - 3][this.bitCols[file] >> 3] & (1 << (dst >> 4))) return true;
			}
		}
	}

	// HORSE kills KING
	for (var i = 9; i <= 10; i++) {
		dst = this.pieces[pieceTag + i];
		if (dst) {
			var hleg = HORSE_LEG_TAB[src - dst + 256];
			if (hleg && !this.board[dst + hleg]) return true;
		}
	}

	// PAWN kills KING
	var ptmp;

	ptmp = this.board[src - 1];
	if ((ptmp & pieceTag) && PIECE_TYPES[ptmp] === P) return true;

	ptmp = this.board[src + 1];
	if ((ptmp & pieceTag) && PIECE_TYPES[ptmp] === P) return true;

	ptmp = this.board[src - 16 + ((player - 1) << 5)];
	if ((ptmp & pieceTag) && PIECE_TYPES[ptmp] === P) return true;

	return false;
};

Position.prototype.isCheckmated = function(opt_player) {
	var player = opt_player || this.turn;
	var moveList = this.genMoves(player);
	if (!moveList.length) { // no valid moves.
		return true;
	}
	var validMoveFound = false;
	for (var i = 0; i < moveList.length; i++) {
		this.makeMove(moveList[i]);
		if (!this.isChecked(player)) {
			validMoveFound = true;
		}
		this.unMakeMove();
		if (validMoveFound) {
			return false;
		}
	}
	return true;
};

Position.prototype.genMoves = function(opt_player) {
	var player = opt_player || this.turn;

	var moveList = [];
	var moves;

	var src, dst, num;
	var pieceTag, ind, rank, file, x, y, i;

	pieceTag = player << 4;

	// KING
	src = this.pieces[pieceTag];
	moves = KING_MOVES[src];
	if (src) {
		ind = 0;
		dst = moves[ind];
		while (dst) {
			if (!(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}
			ind++;
			dst = moves[ind];
		}
	}

	// ADVISOR
	for (i = 1; i <= 2; i++) {
		src = this.pieces[pieceTag + i];
		moves = ADVISOR_MOVES[src];
		if (src) {
			ind = 0;
			dst = moves[ind];
			while (dst) {
				if (!(this.board[dst] & pieceTag)) {
					moveList.push(src << 8 | dst);
				}
				ind++;
				dst = moves[ind];
			}
		}
	}

	// ELEPHANT
	for (i = 3; i <= 4; i++) {
		src = this.pieces[pieceTag + i];
		if (src) {
			ind = 0;
			moves = ELEPHANT_MOVES[src];
			dst = moves[ind];
			while (dst) {
				var ee = ELEPHANT_EYES[src][ind];
				if (!this.board[ee] && !(this.board[dst] & pieceTag)) {
					moveList.push(src << 8 | dst);
				}
				ind++;
				dst = moves[ind];
			}
		}
	}

	// ROOK
	for (i = 5; i <= 6; i++) {
		src = this.pieces[pieceTag + i];
		if (src) {

			rank = src >> 4;
			file = src & 0xf;

			var bitRank, bitFile;
			bitRank = this.bitRows[rank] >> 3;
			bitFile = this.bitCols[file] >> 3;

			// HORIZONTAL

			y = ROOK_ROW_NO_CAP_TAB[file - 3][bitRank][0];
			while (y > file) {
				moveList.push(src << 8 | rank << 4 | y);
				y--;
			}

			y = ROOK_ROW_NO_CAP_TAB[file - 3][bitRank][1];
			while (y < file) {
				moveList.push(src << 8 | rank << 4 | y);
				y++;
			}

			y = ROOK_ROW_CAP_TAB[file - 3][bitRank][0];
			dst = rank << 4 | y;
			if (y !== file && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			y = ROOK_ROW_CAP_TAB[file - 3][bitRank][1];
			dst = rank << 4 | y;
			if (y !== file && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			// VERTICAL

			x = ROOK_COL_NO_CAP_TAB[rank - 3][bitFile][0];
			while (x > rank) {
				moveList.push(src << 8 | x << 4 | file);
				x--;
			}

			x = ROOK_COL_NO_CAP_TAB[rank - 3][bitFile][1];
			while (x < rank) {
				moveList.push(src << 8 | x << 4 | file);
				x++;
			}

			x = ROOK_COL_CAP_TAB[rank - 3][bitFile][0];
			dst = x << 4 | file;
			if (x !== rank && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			x = ROOK_COL_CAP_TAB[rank - 3][bitFile][1];
			dst = x << 4 | file;
			if (x !== rank && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

		}
	}

	// CANNON
	for (i = 7; i <= 8; i++) {
		src = this.pieces[pieceTag + i];
		if (src) {

			rank = src >> 4;
			file = src & 0xf;

			var bitRank, bitFile;
			bitRank = this.bitRows[rank] >> 3;
			bitFile = this.bitCols[file] >> 3;

			// HORIZONTAL

			y = ROOK_ROW_NO_CAP_TAB[file - 3][bitRank][0];
			while (y > file) {
				moveList.push(src << 8 | rank << 4 | y);
				y--;
			}

			y = ROOK_ROW_NO_CAP_TAB[file - 3][bitRank][1];
			while (y < file) {
				moveList.push(src << 8 | rank << 4 | y);
				y++;
			}

			y = CANNON_ROW_CAP_TAB[file - 3][bitRank][0];
			dst = rank << 4 | y;
			if (y !== file && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			y = CANNON_ROW_CAP_TAB[file - 3][bitRank][1];
			dst = rank << 4 | y;
			if (y !== file && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			// VERTICAL

			x = ROOK_COL_NO_CAP_TAB[rank - 3][bitFile][0];
			while (x > rank) {
				moveList.push(src << 8 | x << 4 | file);
				x--;
			}

			x = ROOK_COL_NO_CAP_TAB[rank - 3][bitFile][1];
			while (x < rank) {
				moveList.push(src << 8 | x << 4 | file);
				x++;
			}

			x = CANNON_COL_CAP_TAB[rank - 3][bitFile][0];
			dst = x << 4 | file;
			if (x !== rank && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}

			x = CANNON_COL_CAP_TAB[rank - 3][bitFile][1];
			dst = x << 4 | file;
			if (x !== rank && !(this.board[dst] & pieceTag)) {
				moveList.push(src << 8 | dst);
			}
		}
	}

	// KNIGHT
	for (i = 9; i <= 10; i++) {
		src = this.pieces[pieceTag + i];
		if (src) {
			ind = 0;
			moves = HORSE_MOVES[src];
			dst = moves[ind];
			while (dst) {
				var hleg = HORSE_LEGS[src][ind];
				if (!this.board[hleg] && !(this.board[dst] & pieceTag)) {
					moveList.push(src << 8 | dst);
				}
				ind++;
				dst = moves[ind];
			}
		}
	}

	// PAWN
	for (var i = 11; i <= 15; i++) {
		src = this.pieces[pieceTag + i];
		if (src) {
			ind = 0;
			moves = PAWN_MOVES[src][player - 1];
			dst = moves[ind];
			while (dst) {
				if (!(this.board[dst] & pieceTag)) {
					moveList.push(src << 8 | dst);
				}
				ind++;
				dst = moves[ind];
			}
		}
	}

	return moveList;
};
