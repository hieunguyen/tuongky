'use strict';

/* Services */

var tkServices = angular.module('tkApp.services', []);

function opp(turn) {
  return turn ^ 3;
}

tkServices.factory('gameService', function() {

  var PIECES = 'TSVXPMC';
  var DIRS = '.-/';

  var Move = function(x, y, u, v, cap) {
    this.x = x;
    this.y = y;
    this.u = u;
    this.v = v;
    this.cap = cap;
  };

  var service = {};

  var board;
  var turn;
  var moves;
  var position = new Position();

  function createInitialBoard() {
    return [
      [-R, -H, -E, -A, -K, -A, -E, -H, -R],
      [0, 0, 0, 0, 0, 0, 0, 0, 0],
      [0, -C, 0, 0, 0, 0, 0, -C, 0],
      [-P, 0, -P, 0, -P, 0, -P, 0, -P],
      [0, 0, 0, 0, 0, 0, 0, 0, 0],
      [0, 0, 0, 0, 0, 0, 0, 0, 0],
      [P, 0, P, 0, P, 0, P, 0, P],
      [0, C, 0, 0, 0, 0, 0, C, 0],
      [0, 0, 0, 0, 0, 0, 0, 0, 0],
      [R, H, E, A, K, A, E, H, R]
    ];
  }

  function createEmptyBoard(rows, cols) {
    var board = [];
    for (var i = 0; i < rows; i++) {
      var aRow = [];
      for (var j = 0; j < cols; j++) aRow.push(EMPTY);
      board.push(aRow);
    }
    return board;
  }

  function toMove(x, y, u, v) {
    var src = (x + 3) << 4 | (y + 3);
    var dst = (u + 3) << 4 | (v + 3);
    return src << 8 | dst;
  }

  service.init = function(opt_board, opt_turn) {
    board = opt_board ? angular.copy(opt_board) : createInitialBoard();
    turn = opt_turn || RED;
    moves = [];
    position.init(board, turn);
  };

  service.getBoard = function() {
    return board;
  };

  service.getTurn = function() {
    return turn;
  };

  service.getMoves = function() {
    return moves;
  };

  function getCol(c) {
    return turn === RED ? COLS - c : c + 1;
  }

  function isDiagonalPiece(piece) {
    var p = Math.abs(piece);
    return p === A || p === E || p === H;
  }

  service.produceHumanPosition = function(x, y) {
    var piece = board[x][y];
    var pieceChar, pieceId;
    pieceChar = PIECES.charAt(Math.abs(piece) - 1);

    function getOrder(n, k) {
      if (n < 2) {
        return '';
      }
      if (n === 2) {
        return k === 0 ? 't' : 's';
      }
      if (n === 3) {
        return k === 0 ? 't' : (k === 1 ? 'g' : 's');
      }
      return k + 1;
    }

    function getPieceId(c, totalSameType, totalSameTypeAhead, isPawn) {
      var pieceId = getCol(c);
      if (totalSameType === 1) {
        return pieceId;
      }
      if (isPawn) {
        pieceId += '' + getOrder(totalSameType, totalSameTypeAhead);
      } else {
        pieceId = '' + getOrder(totalSameType, totalSameTypeAhead);
      }
      return pieceId;
    }

    function countSameType(piece, col, opt_row) {
      var count = 0;
      var row, i;
      if (turn === RED) {
        row = opt_row === undefined ? ROWS : opt_row;
        for (i = 0; i < row; i++) {
          if (board[i][col] === piece) {
            count++;
          }
        }
      } else {
        var row = opt_row === undefined ? -1 : opt_row;
        for (i = ROWS - 1; i > row; i--) {
          if (board[i][col] === piece) {
            count++;
          }
        }
      }
      return count;
    }

    var totalSameType = countSameType(piece, y);
    var totalSameTypeAhead = countSameType(piece, y, x);

    pieceId = getPieceId(
        y, totalSameType, totalSameTypeAhead, Math.abs(piece) === P);
    return pieceChar + pieceId;
  };

  service.produceHumanMove = function(x, y, u, v) {
    var dir, dst;

    // dir = 1, 2, 3 = ., -, /
    if (x === u) {
      dir = 2;
    } else if (u < x) {
      dir = turn === RED ? 1 : 3;
    } else {
      dir = turn === RED ? 3 : 1;
    }

    if (isDiagonalPiece(board[x][y])) {
      dst = getCol(v);
    } else {
      if (dir === 2) {
        dst = getCol(v);
      } else {
        dst = Math.abs(x - u);
      }
    }
    return service.produceHumanPosition(x, y) + DIRS.charAt(dir - 1) + dst;
  };

  service.parseHumanMove = function(humanMove) {

    function trim(str) {
        return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    }

    function findPosition(piece, humanPosition) {
      if (turn === BLACK) {
        piece = -piece;
      }
      for (var i = 0; i < ROWS; i++)
        for (var j = 0; j < COLS; j++)
          if (board[i][j] === piece &&
              service.produceHumanPosition(i, j).toLowerCase() ===
                  humanPosition) {
            return [i, j];
          }
      return undefined;
    }

    var hm = trim(humanMove).toLowerCase();
    if (hm.length < 4 || hm.length > 5) {
      return undefined;
    }
    var x, y, u, v, cap;
    var piece, col;
    var piece = PIECES.toLowerCase().indexOf(hm[0]);
    if (piece < 0) {
      return undefined;
    }
    piece++;

    var ss = hm.split(/[-\.//]/);

    if (ss.length !== 2 ||
        ss[0].length < 2 || ss[0].length > 3 ||
        ss[1].length !== 1) {
      return undefined;
    }

    var dir = DIRS.indexOf(hm[ss[0].length]);
    if (dir < 0) return undefined;
    dir++;

    var dst = Number(ss[1]);
    if (isNaN(dst)) return undefined;

    var pos = findPosition(piece, ss[0]);

    if (!pos) return undefined;

    x = pos[0];
    y = pos[1];

    var dstCol = turn === RED ? COLS - dst : dst - 1;

    var dx, dy;

    switch (piece) {
      case K:
      case R:
      case C:
      case P:
        if (dir === 2) {
          dx = 0;
          dy = dstCol - y;
        } else if (dir === 1) {
          dx = -dst;
          dy = 0;
        } else {
          dx = dst;
          dy = 0;
        }
        break;
      case A:
        if (dir === 2) {
          return undefined;
        }
        dy = dstCol - y;
        if (dir === 1) {
          dx = -1;
        } else {
          dx = +1;
        }
        break;
      case E:
        if (dir === 2) {
          return undefined;
        }
        dy = dstCol - y;
        if (dir === 1) {
          dx = -2;
        } else {
          dx = +2;
        }
        break;
      case H:
        if (dir === 2) {
          return undefined;
        }
        dy = dstCol - y;
        if (dir === 1) {
          dx = -(3 - Math.abs(dy));
        } else {
          dx = 3 - Math.abs(dy);
        }
        break;
    }

    if (turn === BLACK) {
      dx = -dx;
    }

    u = x + dx;
    v = y + dy;
    if (u < 0 || v < 0 || u >= ROWS || v >= COLS) return undefined;

    cap = board[u][v];

    if (service.produceHumanMove(x, y, u, v).toLowerCase() !== hm) {
      return undefined;
    }

    return new Move(x, y, u, v, cap);
  }

  service.isValidMove = function(x, y, u, v) {
    var move = toMove(x, y, u, v);
    if (!position.isValidMove(move)) return false;
    position.makeMove(move);
    var checked = position.isChecked(turn);
    position.unMakeMove();
    return !checked;
  };

  service.isChecked = function(opt_player) {
    return position.isChecked(opt_player);
  };

  service.isCheckmated = function(opt_player) {
    return position.isCheckmated(opt_player);
  };

  service.makeMove = function(x, y, u, v) {
    moves.push(new Move(x, y, u, v, board[u][v]));
    board[u][v] = board[x][y];
    board[x][y] = EMPTY;
    turn = opp(turn);
    position.makeMove(toMove(x, y, u, v));
  };

  service.unMakeMove = function() {
    if (!moves.length) {
      console.log('Oops, no moves left to undo.');
      return;
    }
    var lm = moves.pop();
    board[lm.x][lm.y] = board[lm.u][lm.v];
    board[lm.u][lm.v] = lm.cap;
    turn = opp(turn);
    position.unMakeMove();
  };

  return service;
});


tkServices.factory('treeService', function() {

  var Node = function(opt_nodeData) {
    this.parent = null;
    this.children = [];
    this.nodeData = opt_nodeData || {};
  };

  Node.prototype.setParent = function(parent) {
    this.parent = parent;
  };

  Node.prototype.addChild = function(node) {
    this.children.push(node);
    node.setParent(this);
  };

  Node.prototype.isRoot = function() {
    return !this.parent;
  };

  Node.prototype.hasChildren = function() {
    return this.children.length > 0;
  };

  Node.prototype.firstChild = function() {
    return this.children[0];
  };

  Node.prototype.getSiblingCount = function() {
    return this.parent ? this.parent.children.length : 1;
  };

  var service = {};

  var root, line;

  service.init = function(opt_obj) {
    if (opt_obj) {
      root = fromObject(opt_obj);
    } else {
      root = new Node();
    }
    line = extractLine(root);
  };

  function fromObject(obj) { // deserialize.
    var node = new Node(obj.nodeData);
    _.each(obj.children, function(childObj) {
      node.addChild(fromObject(childObj));
    });
    return node;
  }

  function toObjectFrom(node) {
    var subTree = {
      nodeData: node.nodeData
    };
    if (node.hasChildren()) {
      subTree.children = [];
      _.each(node.children, function(child) {
        subTree.children.push(toObjectFrom(child));
      });
    }
    return subTree;
  }

  service.toObject = function() { // serialize.
    return toObjectFrom(root);
  };

  service.addVariation = function(parent, nodeData) {
    var vIndex = -1;
    _.each(parent.children, function(node, index) {
      if (angular.equals(node.nodeData.move, nodeData.move)) {
        vIndex = index;
      }
    });
    if (vIndex >= 0) {
      service.selectVariation(parent.children[vIndex], vIndex);
    } else {
      var node = new Node(nodeData);
      parent.addChild(node);
      line = extractLine(node);
    }
  };

  service.selectVariation = function(node, vIndex) {
    line = extractLine(node.parent.children[vIndex]);
  };

  service.swapNodes = function(parent, xIndex, yIndex) {
    var tmp = parent.children[xIndex];
    parent.children[xIndex] = parent.children[yIndex];
    parent.children[yIndex] = tmp;
  };

  service.removeChild = function(parent, cIndex) {
    parent.children[cIndex].parent = undefined;
    parent.children.splice(cIndex, 1);
    line = extractLine(parent);
  };

  service.getLine = function() {
    return line;
  }

  function extractLine(node) {
    var line = [];
    var x = node;
    while (x) {
      line.unshift(x);
      x = x.parent;
    }
    x = node;
    while (x.hasChildren()) {
      x = x.firstChild();
      line.push(x);
    }
    return line;
  };

  return service;
});


tkServices.factory('fenService', function() {

  var RED_PIECES    = "KAERCHP";
  var STARTING_FEN = 'rheakaehr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RHEAKAEHR w - - - 1';

  var service = {};

  service.url2fen = function(url) {
   return url.replace(/\./g, '/').replace(/_/g, ' ');
  };

  service.fen2url = function(fen) {
    return fen.replace(/\//g, '.').replace(/\s/g, '_');
  };

  function getPieceType(c) {
    if (c.toLowerCase() === 'b') {
      c = 'e';
    }
    if (c.toLowerCase() === 'n') {
      c = 'h';
    }
    return RED_PIECES.indexOf(c.toUpperCase());
  }

  service.url2pos = function(url) {
    return service.fen2pos(service.url2fen(url));
  };

  service.fen2pos = function(fen) {
    var board, turn, halfMoveClock = -1, fullMoveNumber = 1;
    var as = fen.split(' ');
    if (as.length < 2) {
      return null;
    }
    var rows = as[0].split('/');
    if (rows.length < ROWS) {
      return null;
    }

    board = [];
    for (var i = 0; i < ROWS; i++) {
      var boardRow = [];
      for (var j = 0; j < COLS; j++) boardRow.push(0);
      var col = 0;
      for (j = 0; j < rows[i].length; j++) {
        var c = rows[i].charAt(j);
        if ('0' <= c && c <= '9') {
          col += Number(c);
        } else {
          var pieceType = getPieceType(c);
          if (pieceType < 0) {
            return null;
          }
          if (col >= COLS) {
            return null;
          }
          boardRow[col] = pieceType + 1;
          if (c === c.toLowerCase()) {
            boardRow[col] *= -1;
          }
          col++;
        }
      }
      if (col !== COLS) {
        return null;
      }
      board.push(boardRow);
    }

    turn = as[1].toLowerCase() === 'w' ? RED : BLACK;

    if (as.length >= 5) {
      halfMoveClock = Number(as[4]);
      if (isNaN(halfMoveClock)) {
        halfMoveClock = -1;
      }
    }

    if (as.length >= 6) {
      fullMoveNumber = Number(as[5]);
      if (isNaN(fullMoveNumber)) {
        fullMoveNumber = -1;
      }
    }

    return {
      board: board,
      turn: turn,
      halfMoveClock: halfMoveClock,
      fullMoveNumber: fullMoveNumber
    };
  };

  service.pos2fen = function(pos) {
    var board = pos.board;
    var rows = [];
    for (var i = 0; i < ROWS; i++) {
      var row = '';
      var empty = 0;
      for (var j = 0; j < COLS; j++) {
        var piece = board[i][j];
        if (piece) {
          if (empty > 0) {
            row += empty.toString();
          }
          var pieceChar = RED_PIECES.charAt(Math.abs(piece) - 1);
          row += piece > 0 ? pieceChar : pieceChar.toLowerCase();
          empty = 0;
        } else empty++;
      }
      if (empty > 0) {
        row += empty.toString();
      }
      rows.push(row);
    }
    var turnInFen = pos.turn === RED ? 'w' : 'b';

    var halfMoveClockInFen =
        pos.halfMoveClock === undefined || pos.halfMoveClock < 0 ?
        '-' : pos.halfMoveClock.toString();

    var fullMoveNumberInFen =
        pos.fullMoveNumber === undefined || pos.fullMoveNumber < 0 ?
        '-' : pos.fullMoveNumber.toString();

    return rows.join('/') + ' ' + turnInFen + ' - - ' + halfMoveClockInFen +
        ' ' + fullMoveNumberInFen;
  };

  service.getStartingFen = function() {
    return STARTING_FEN;
  };

  service.getStartingPosition = function() {
    return service.fen2pos(STARTING_FEN);
  };
  return service;
});


tkServices.factory('dbService', function(
    $http, $q, notificationService, vnService) {

  var service = {};

  service.createGame = function(game, username) {
    var defer = $q.defer();
    notificationService.show('Đang tạo tài liệu...');
    $http.post('/game/create', {
      username: username,
      category: game.category,
      title: game.title,
      n_title: vnService.normalize(game.title),
      book: game.book,
      n_book: vnService.normalize(game.book),
      old_book: game.oldBook,
      data: game.data
    }).success(function(response) {
      notificationService.show('Đã tạo tài liệu thành công.');
      defer.resolve(response.gameId);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa tạo được tài liệu.');
      defer.reject();
    });
  return defer.promise;
  };

  service.saveGame = function(game, username) {
    var defer = $q.defer();
    notificationService.show('Đang lưu tài liệu...');
    $http.post('/game/save', {
      id: game.id,
      username: username,
      category: game.category,
      title: game.title,
      n_title: vnService.normalize(game.title),
      book: game.book,
      n_book: vnService.normalize(game.book),
      old_book: game.oldBook,
      data: game.data
    }).success(function(response) {
      notificationService.show('Đã lưu tài liệu thành công.');
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa lưu được tài liệu.');
      defer.reject();
    });
    return defer.promise;
  };

  service.searchGames = function(queryString, opt_start) {
    var defer = $q.defer();
    $http.post('/game/search', {
      q: vnService.normalize(queryString),
      start: opt_start || 0
    }).success(function(response) {
      defer.resolve(response);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  service.advancedSearchGames = function(query) {
    var defer = $q.defer();
    $http.post('/game/search', {
      category: query.category,
      title: vnService.normalize(query.title),
      book: vnService.normalize(query.book)
    }).success(function(response) {
      defer.resolve(response);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  service.initGames = function() {
    var defer = $q.defer();
    $http.post('/game/init', {}).success(function(response) {
      defer.resolve(response);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  service.getGame = function(gameId) {
    var defer = $q.defer();
    notificationService.show('Đang đọc tài liệu...');
    $http.get('/game/show?gameId=' + gameId)
    .success(function(response) {
      notificationService.hide();
      defer.resolve(response.game);
    }).error(function() {
      notificationService.show('Gặp lỗi, không đọc được tài liệu.');
      defer.reject();
    });
    return defer.promise;
  };

  service.deleteGame = function(gameId, username) {
    var defer = $q.defer();
    notificationService.show('Đang xóa tài liệu...');
    $http.post('/game/delete', {
      id: gameId,
      username: username
    }).success(function(response) {
      notificationService.show('Đã xóa tài liệu thành công.');
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa xóa được tài liệu.');
      defer.reject();
    });
    return defer.promise;
  };

  return service;
});


tkServices.factory('notificationService', function($timeout) {
  var SHOW_TIME = 7000; // 7s.

  var service = {};

  var alert_ = {
    type: '',
    content: '',
    visible: false
  };

  var showingCount = 0;

  service.getAlert = function() {
    return alert_;
  };

  function maybeHide() {
    showingCount--;
    if (!showingCount) {
      service.hide();
    }
  };

  service.show = function(text, showTime) {
    showingCount++;
    alert_.type = 'success';
    alert_.content = text;
    alert_.visible = true;
    $timeout(maybeHide, showTime || SHOW_TIME);
  };

  service.showError = function(text, showTime) {
    showingCount++;
    alert_.type = 'danger';
    alert_.content = text;
    alert_.visible = true;
    $timeout(maybeHide, showTime || SHOW_TIME);
  };

  service.hide = function() {
    alert_.visible = false;
    showingCount = 0;
  };

  return service;
});


tkServices.factory('cookieService', function() {
  var service = {};

  service.set = function(name, value, options) {
    $.cookie(name, value, options || {expires : 365});
  };

  service.get = function(name) {
    return $.cookie(name);
  };

  service.delete = function(name) {
    $.removeCookie(name);
  };

  return service;
});


tkServices.factory('authService', function(cookieService) {
  var service = {};

  var user = {
    username: '',
    authenticated: false,
    isAdmin: false
  };

  service.getUser = function() {
    return user;
  };

  service.signIn = function(username) {
    user.username = username;
    user.authenticated = true;
    user.isAdmin = _.contains(ADMIN_USERS, username);
  };

  service.signOut = function() {
    user.username = '';
    user.authenticated = false;
    user.isAdmin = false;
    cookieService.delete('sid');
  };

  service.isAuthenticated = function() {
    return user.authenticated;
  };

  service.isAdmin = function() {
    return user.isAdmin;
  };

  return service;
});


tkServices.factory('userService', function($q, $http, notificationService) {
  var service = {};

  service.signIn = function(username, password) {
    var defer = $q.defer();
    notificationService.show('Đang đăng nhập...');
    $http.post('/signin', {
      username: username,
      password: password
    }).success(function(response) {
      if (!response.code) {
        notificationService.show('Đã đăng nhập thành công.');
        defer.resolve(response);
      } else if (response.code === 1) {
        notificationService.showError('Tài khoản của bạn chưa được đăng ký.');
        defer.reject();
      } else if (response.code === 2) {
        notificationService.showError('Sai mật khẩu.');
        defer.reject();
      }
    }).error(function() {
      notificationService.showError('Gặp lỗi, không đăng nhập được.');
      defer.reject();
    });
    return defer.promise;
  };

  service.signUp = function(email, username, password, inviteCode) {
    var defer = $q.defer();
    notificationService.show('Đang đăng ký tài khoản mới...');
    $http.post('/signup', {
      email: email,
      username: username,
      password: password,
      invite_code: inviteCode
    }).success(function(response) {
      if (!response.code) {
        notificationService.show('Đã đăng ký tài khoản thành công.');
        defer.resolve(response);
      } else if (response.code === 1) {
        notificationService.showError('Tài khoản này đã được đăng ký sử dụng.');
        defer.reject();
      } else if (response.code === 2) {
        notificationService.showError('Tài khoản hoặc mật khẩu không hợp lệ.');
        defer.reject();
      } else if (response.code === 3) {
        notificationService.showError('Mã mời tạo tài khoản không hợp lệ.');
        defer.reject();
      }
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa đăng ký được tài khoản.');
      defer.reject();
    });
    return defer.promise;
  };

  service.getStatus = function() {
    var defer = $q.defer();
    $http.get('/user_status')
    .success(function(response) {
      defer.resolve(response.username);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  return service;
});


tkServices.factory('bookService', function($q, $http) {
  var service = {};

  var booksForUsers = {};

  function unique(books) {
    var output = [];
    var found = {};
    _.each(books, function(book) {
      if (!found[book.name]) {
        found[book.name] = true;
        output.push(book);
      }
    });
    return output;
  }

  service.getBooksForUser = function(username, lastCreatedAt) {
    if (booksForUsers[username]) {
      return $q.when(booksForUsers[username]);
    }
    var defer = $q.defer();
    var url = '/books?username=' + username;
    if (lastCreatedAt) {
      url += '&last_created_at=' + lastCreatedAt;
    }
    $http.get(url)
    .success(function(response) {
      var books = unique(response.books);
      booksForUsers[username] = books;
      defer.resolve(books);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  service.isOldBook = function(username, name) {
    var books = booksForUsers[username];
    if (!books) {
      return false;
    }
    return _.contains(_.pluck(books, 'name'), name);
  };

  service.addBook = function(username, book) {
    if (!booksForUsers[username]) {
      booksForUsers[username] = [];
    }
    if (!service.isOldBook(username, book.name)) {
      booksForUsers[username].push(book);
    }
  };

  service.getBooks = function(username) {
    return booksForUsers[username];
  };

  return service;
});


tkServices.factory('inviteService', function($q, $http, notificationService) {
  var service = {};

  service.createInvite = function() {
    var defer = $q.defer();
    notificationService.show('Đang tạo invite link...');
    $http.post('/invite/create', {
    }).success(function(response) {
      notificationService.show('Đã tạo invite link thành công.');
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp lỗi, không tạo được invite link.');
      defer.reject();
    });
    return defer.promise;
  };

  return service;
});


tkServices.factory('vnService', function() {

  var ROOTS = {
    a: 'à á ả ã ạ ầ ấ ẩ ẫ ậ â ằ ắ ẳ ẵ ặ ă',
    e: 'è é ẻ ẽ ẹ ề ế ể ễ ệ ê',
    o: 'ò ó ỏ õ ọ ồ ố ổ ỗ ộ ô ờ ớ ở ỡ ợ ơ',
    u: 'ù ú ủ ũ ụ ừ ứ ử ữ ự ư',
    i: 'ì í ỉ ĩ ị',
    y: 'ỳ ý ỷ ỹ ỵ',
    d: 'đ'
  };

  var STEMMINGS = {
    xa: 'xe',
    binh: 'tot',
    chot: 'tot',
    voi: 'tuong',
    sy: 'si',
    ly: 'li',
    ti: 'ty',
    cuc: 'cuoc'
  };

  var service = {};

  function reduceChar(c) {
    if (!c || c === ' ') {
      return c;
    }
    c = c.toLowerCase();
    var res = c;
    _.each(ROOTS, function(value, root) {
      if (value.indexOf(c) >= 0) {
        res = root;
      }
    });
    return res;
  }

  service.stems = function(s) {
    var words = s.toLowerCase().split(/[^a-zA-z0-9]+/);
    var ws = _.map(words, function(word) {
      return STEMMINGS[word] || word;
    });
    return _.filter(ws, function(w) { return !!w; }).join(' ');
  };

  service.removeAnnotation = function(s) {
    var res = '';
    for (var i = 0; i < s.length; i++) {
      res += reduceChar(s.charAt(i));
    }
    return res;
  };

  service.normalize = function(s) {
    if (!s) {
      return s;
    }
    return service.stems(service.removeAnnotation(s));
  };

  return service;
});


tkServices.factory('annotateService', function(vnService) {
  var service = {};

  function annotateToken(token, tag) {
    return '<' + tag + '>' + token + '</' + tag + '>';
  }

  function inRange(c, l, r) {
    return l <= c && c <= r;
  }

  function isLetter(c) {
    return inRange(c, 'a', 'z') || inRange(c, 'A', 'Z');
  }

  function isDigit(c) {
    return inRange(c, '0', '9');
  }

  function isLetterOrDigit(c) {
    return isLetter(c) || isDigit(c);
  }

  service.annotate = function(s, query, opt_tag) {
    if (!query) {
      return s;
    }
    var queryTokens = vnService.normalize(query).split(/[^a-zA-z0-9]/);
    if (!queryTokens.length) {
      return s;
    }
    var tag, annotated, rs, originalWord, word, i, j;
    tag = opt_tag || 'em';
    annotated = '';
    rs = vnService.removeAnnotation(s);
    for (i = 0; i < rs.length; i++) {
      if (isLetterOrDigit(rs[i])) {
        j = i;
        while (j + 1 < rs.length && isLetterOrDigit(rs[j + 1])) {
          j++;
        }
        originalWord = s.substring(i, j + 1);
        word = rs.substring(i, j + 1);
        if (_.contains(queryTokens, vnService.normalize(word))) {
          annotated += annotateToken(originalWord, tag);
        } else {
          annotated += originalWord;
        }
        i = j;
      } else annotated += s[i];
    }
    return annotated;
  };

  return service;
});


tkServices.factory('engineService', function($q, $http, notificationService) {
  var service = {};

  service.think = function(fen, moves) {
    var defer = $q.defer();
    var params = {
      fen: fen,
      moves: moves
    };
    var url = 'http://localhost:1234/go?' + $.param(params);
    // var url = 'http://8.35.197.17:1234/go?' + $.param(params);
    notificationService.show('Đang suy nghĩ...');
    $http.get(url).success(function(response) {
      notificationService.hide();
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp vấn đề, chưa nghĩ ra.');
      defer.reject();
    });
    return defer.promise;
  };

  return service;
});
