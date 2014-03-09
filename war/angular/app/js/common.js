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

var FB_SCOPES = 'email';

// should contain exact 13 elements for levels 0 to 12.
var LEVEL_DESCRIPTIONS = [
  'Chưa sạch nước cản',
  'Bập bõm',
  'Mới biết chơi',
  'Giỏi nhất nhà',
  'Nổi danh khắp xóm',
  'Vô địch phường',
  'Vô địch quận',
  'Vô địch huyện',
  'Vô địch quốc gia',
  'Vô địch thế giới',
  'Đánh đâu thắng đó',
  'Độc Cô Cầu Bại',
  'Vô Đối'
];
