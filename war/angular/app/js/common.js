var ROWS = 10;
var COLS = 9;

var EMPTY = 0;
var RED = 1;
var BLACK = 2;

var WIN = 1;
var LOSS = 2;
var DRAW = 3;

var HUMAN = 1;
var COMPUTER = 2;

var K = 1; // King
var A = 2; // Assistant
var E = 3; // Elephant
var R = 4; // Rook
var C = 5; // Cannon
var H = 6; // Horse
var P = 7; // Pawn

var Roles = {
  ANONYMOUS: 0,
  USER: 1,
  MODERATOR: 2,
  ADMIN: 3
};

var ADMIN_USERS = ['admin', 'hieu']; // TODO(): remove this.

var SESSION_ID = 'sid';

var FB_APP_ID = '1447546642129595';

// should contain exact 13 elements for levels 0 to 12.
var LEVEL_DESCRIPTIONS = [
  'Lính Mới',
  'Bập Bõm',
  'Biết Chơi',
  'Giỏi Nhất Nhà',
  'Cao Thủ Xóm',
  'Cao Thủ Phường',
  'Cao Thủ Quận',
  'Cao Thủ Huyện',
  'Cao Thủ Quốc Gia',
  'Cao Thủ Quốc Tế',
  'Đánh Đâu Thắng Đó',
  'Độc Cô Cầu Bại',
  'Vô Địch Thiên Hạ'
];
