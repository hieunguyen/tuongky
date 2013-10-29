'use strict';

/* Services */

var tkServices = angular.module('tkApp.services', []);

function opp(turn) {
  return turn ^ 3;
}

tkServices.factory('gameService', function() {

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

  service.produceHumanMove = function(x, y, u, v) {
    var PIECES = 'TSVXPMC';
    var DIRS = '.-/';
    var piece = board[x][y];
    var pieceChar, pieceId, dir, dst;
    pieceChar = PIECES.charAt(Math.abs(piece) - 1);

    function getCol(c) {
      return turn === RED ? COLS - c : c + 1;
    }

    // dir = 1, 2, 3 = ., -, /
    if (x === u) {
      dir = 2;
    } else if (u < x) {
      dir = turn === RED ? 1 : 3;
    } else {
      dir = turn === RED ? 3 : 1;
    }

    // TODO: process s/g/t.

    switch (Math.abs(piece)) {
      case K:
      case P:
        pieceId = getCol(y);
        if (dir === 2) {
          dst = getCol(v);
        } else {
          dst = Math.abs(x - u);
        }
        break;
      case A:
      case E:
      case H:
        pieceId = getCol(y);
        dst = getCol(v);
        break;
      case R:
      case C:
        pieceId = getCol(y);
        if (dir === 2) {
          dst = getCol(v);
        } else {
          dst = Math.abs(x - u);
        }
        break;
    };
    return pieceChar + pieceId + DIRS.charAt(dir - 1) + dst;
  };

  service.isValidMove = function(x, y, u, v) {
    return position.isValidMove(toMove(x, y, u, v));
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
          var pieceType = RED_PIECES.indexOf(c.toUpperCase());
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

    var halfMoveClockInFen = pos.halfMoveClock < 0 ? '-' : pos.halfMoveClock.toString();
    var fullMoveNumberInFen = pos.fullMoveNumber < 0 ? '-' : pos.fullMoveNumber.toString();

    return rows.join('/') + ' ' + turnInFen + ' - - ' + halfMoveClockInFen + ' ' +
        fullMoveNumberInFen;
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
    notificationService.show('Đang tạo game...');
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
      notificationService.show('Đã tạo game thành công.');
      defer.resolve(response.gameId);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa tạo được game.');
      defer.reject();
    });
  return defer.promise;
  };

  service.saveGame = function(game, username) {
    var defer = $q.defer();
    notificationService.show('Đang lưu game...');
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
      notificationService.show('Đã lưu game thành công.');
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa lưu được game.');
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
    $http.get('/game/show?gameId=' + gameId)
    .success(function(response) {
      defer.resolve(response.game);
    }).error(function() {
      defer.reject();
    });
    return defer.promise;
  };

  service.deleteGame = function(gameId, username) {
    var defer = $q.defer();
    notificationService.show('Đang xóa game...');
    $http.post('/game/delete', {
      id: gameId,
      username: username
    }).success(function(response) {
      notificationService.show('Đã xóa game thành công.');
      defer.resolve(response);
    }).error(function() {
      notificationService.showError('Gặp lỗi, chưa xóa được game.');
      defer.reject();
    });
    return defer.promise;
  };

  return service;
});


tkServices.factory('notificationService', function($timeout) {
  var SHOW_TIME = 3000; // 3s.

  var service = {};

  var alert = {
    type: '',
    content: '',
    visible: false
  };

  var showingCount = 0;

  service.getAlert = function() {
    return alert;
  };

  function maybeHide() {
    showingCount--;
    if (!showingCount) {
      service.hide();
    }
  };

  service.show = function(text, showTime) {
    showingCount++;
    alert.type = 'success';
    alert.content = text;
    alert.visible = true;
    $timeout(maybeHide, showTime || SHOW_TIME);
  };

  service.showError = function(text, showTime) {
    showingCount++;
    alert.type = 'danger';
    alert.content = text;
    alert.visible = true;
    $timeout(maybeHide, showTime || SHOW_TIME);
  };

  service.hide = function() {
    alert.visible = false;
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
      var books = _.uniq(response.books);
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
    if (!service.isOldBook(book.name)) {
      booksForUsers[username].push(book);
    }
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
    sy: 'si'
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
