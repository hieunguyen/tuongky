var BOARD_SIZE = 256;
var PIECE_TYPES = [
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	K, A, A, E, E, R, R, C, C, H, H, P, P, P, P, P,
	K, A, A, E, E, R, R, C, C, H, H, P, P, P, P, P];

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

		for (k = i + 1; k < ROWS; k++) {
			if (j >> k & 1) {
				ROOK_ROW_CAP_TAB[i][j][0] = k + 3;
				ROOK_ROW_CAP_MASK[i][j] |= 1 << (k + 3);
				break;
			}
			ROOK_ROW_NO_CAP_TAB[i][j][0] = k + 3;
			ROOK_ROW_NO_CAP_MASK[i][j] |= 1 << (k + 3);
		}

		for (k++; k < ROWS; k++)
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
