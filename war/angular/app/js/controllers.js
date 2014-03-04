'use strict';

/* Controllers */

var tkControllers = angular.module('tkApp.controllers', []);


tkControllers.controller('AppCtrl', function(
    $scope, $location, $route, $facebook,
    notificationService, authService, userService, cookieService) {

  $scope.isAdmin = function() {
    return authService.isAdmin();
  };

  $scope.mainNav = {tab: ''};

  $scope.CATEGORIES =
      ['', 'Ván đấu', 'Khai cuộc', 'Trung cuộc', 'Tàn cuộc', 'Cờ thế'];
  $scope.CATEGORY_KEYWORDS =
      ['', 'vandau', 'khaicuoc', 'trungcuoc', 'tancuoc', 'cothe'];

  $scope.alert = notificationService.getAlert();
  $scope.user = authService.getUser();
  $scope.data = {loading: false};
  $scope.searchData = {
    queryString: '',
    searchBoxFocused: false
  };

  $scope.$watch(function() {
    return $location.search()['embed'];
  }, function(embed) {
    $scope.embed = embed;
  });

  $scope.$on('$routeChangeStart', function(event, next) {

    if (next.accessLevel > Roles.ANONYMOUS && !authService.isAuthenticated()) {
      $location.path('/fb_signin');
      return;
    }
    if (next.accessLevel === Roles.ADMIN && !authService.isAdmin()) {
      $location.path('/accessdenied');
    }
  });

  $scope.data.loading = 1;
  userService.getStatus().then(function(status) {
    $scope.data.loading--;
    if (status.fbId) {
      authService.signIn(status.fbId, status.fbName, status.roleId);
    } else {
      cookieService.delete(SESSION_ID);
    }
  });

  $scope.$on('facebook.auth.authResponseChange', function(e, response) {
    if (authService.isAuthenticated()) return;
    if (!response.authResponse) {
      $route.reload();
      return;
    }
    $scope.data.loading = 1;
    userService.getStatus().then(function(status) {
      $scope.data.loading--;
      if (status.fbId) {
        authService.signIn(status.fbId, status.fbName, status.roleId);
        $route.reload();
      }
    });
  });
});


tkControllers.controller('AuthController', function(
    $scope, $location, $facebook, userService, authService) {

  $scope.signInWithFb = function() {
    $facebook.login();
  };

  $scope.signUp = function() {
    $location.path('/signup');
  };

  $scope.signOut = function() {
    authService.signOut();
    $facebook.logout();
    $location.path('/');
  };

  $scope.invite = function() {
    $location.path('/invite');
  };
});


tkControllers.controller('SandboxCtrl', function() {
});


tkControllers.controller('StudyCtrl', function(
    $scope, $timeout, $routeParams,
    gameService, treeService, fenService, engineService) {

  $scope.fen = fenService.getStartingFen();
  if ($routeParams.encodedFen) {
    $scope.fen = fenService.url2fen($routeParams.encodedFen);
  }

  $scope.editMode = true;

  var pos = fenService.fen2pos($scope.fen);

  if (pos) {
    gameService.init(pos.board, pos.turn);
  } else {
    alert('FEN không hợp lệ.');
    gameService.init();
  }
  treeService.init();

  $scope.playerTypes = [0, HUMAN, COMPUTER];

  function computerPlay() {
    var fen = $scope.fen;
    var moves = _.map(gameService.getMoves(), function(move) {
      function where(x, y) {
        return String.fromCharCode(97 + y) + (ROWS - 1 - x);
      }
      return where(move.x, move.y) + where(move.u, move.v);
    }).join(' ');
    return engineService.think(fen, moves);
  }

  function maybeComputerPlay(inTurn) {
    if (Number(inTurn) === COMPUTER) {
      if (gameService.isCheckmated()) {
        return;
      }
      $timeout(function() {
        computerPlay().then(function(move) {
          $scope.$broadcast('new_move', move);
        });
      });
    }
  }

  $scope.$watch(function() {
    return gameService.getTurn();
  }, function(turn) {
    maybeComputerPlay($scope.playerTypes[turn]);
  });

  $scope.$watch(function() {
    return $scope.playerTypes[gameService.getTurn()];
  }, maybeComputerPlay);
});


tkControllers.controller('CreateGameCtrl', function(
    $scope, $routeParams, $location, $timeout,
    authService, gameService, treeService, fenService, dbService, game,
    vnService, bookService) {

  $scope.isAdmin = function() {
    return authService.isAdmin();
  };

  var fen = fenService.getStartingFen();

  if (game) {
    $scope.game = game;
    $scope.game.category = game.categoryIndex;
    try {
      var dataObj = JSON.parse(game.data);
      treeService.init(dataObj.moveTree);
      fen = dataObj.fen;
    } catch (error) {
      alert('Tài liệu lỗi.');
      treeService.init();
    }
  } else {
    $scope.game = {
      category: 1,
      title: '',
      book: ''
    };
    if ($routeParams.encodedFen) {
      fen = fenService.url2fen($routeParams.encodedFen);
    }
    treeService.init();
    $timeout(function() {
      $scope.titleFocused = true;
    });
  }

  var pos = fenService.fen2pos(fen);

  if (pos) {
    gameService.init(pos.board, pos.turn);
  } else {
    alert('FEN không hợp lệ.');
    gameService.init();
  }

  $scope.fen = fen;
  $scope.editMode = !game;

  $scope.books = [];
  bookService.getBooksForUser($scope.user.username).then(function(books) {
    $scope.books = books;
    _.each($scope.books, function(book) {
      book.normalizedName = vnService.removeAnnotation(book.name).toLowerCase();
    });
  });

  function addBook(name) {
    var book = {
      name: name,
      normalizedName: vnService.removeAnnotation(name)
    };
    bookService.addBook($scope.user.username, book);
    $scope.books = bookService.getBooks($scope.user.username);
  }

  $scope.saveGame = function() {
    $scope.game.data = JSON.stringify({
      moveTree: treeService.toObject(),
      fen: fen
    });
    $scope.game.oldBook = bookService.isOldBook(
        $scope.user.username, $scope.game.book) ? 1 : 0;
    if (game) {
      dbService.saveGame($scope.game, $scope.user.username).then(function() {
        if (!$scope.game.oldBook) {
          addBook($scope.game.book);
        }
        $scope.editMode = false;
      });
    } else {
      dbService.createGame($scope.game, $scope.user.username).then(
          function(gameId) {
            if (!$scope.game.oldBook) {
              addBook($scope.game.book);
            }
            $scope.editMode = false;
            $location.path('/game/id/' + gameId);
          });
    }
  };

  $scope.editGame = function() {
    $scope.editMode = true;
    $timeout(function() {
      $scope.titleFocused = true;
    });
  };

  $scope.deleteGame = function() {
    if (!confirm('Bạn có thực sự muốn xóa tài liệu này không?')) {
      return;
    }
    dbService.deleteGame($scope.game.id, $scope.user.username).then(function() {
      $location.path('/');
    });
  };

  $scope.cancelChanges = function() {
    $scope.editMode = false;
  };
});


tkControllers.controller('CreateFenCtrl', function(
    $scope, $location, $routeParams, fenService) {

  var pos;
  if ($routeParams.encodedFen) {
    pos = fenService.url2pos($routeParams.encodedFen);
  }

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

  var INITIAL_BOARD = createInitialBoard();
  var INITIAL_RED_BOX = [R, R, C, C, H, H, P, P, P, P, P, K, A, A, E, E];

  function createEmptyBoard(rows, cols) {
    var board = [];
    for (var i = 0; i < rows; i++) {
      var aRow = [];
      for (var j = 0; j < cols; j++) aRow.push(EMPTY);
      board.push(aRow);
    }
    return board;
  }

  var PIECE_IMAGE_NAME_MAP = {};
  PIECE_IMAGE_NAME_MAP[K] = 'k';
  PIECE_IMAGE_NAME_MAP[A] = 'a';
  PIECE_IMAGE_NAME_MAP[E] = 'e';
  PIECE_IMAGE_NAME_MAP[R] = 'r';
  PIECE_IMAGE_NAME_MAP[C] = 'c';
  PIECE_IMAGE_NAME_MAP[H] = 'h';
  PIECE_IMAGE_NAME_MAP[P] = 'p';

  $scope.getImageName = function(piece) {
    if (!piece) return 'dot';
    return (piece > 0 ? 'r' : 'b') + PIECE_IMAGE_NAME_MAP[Math.abs(piece)];
  };

  function dropWithinBoard(dragId, row, col) {
    var x = Number(dragId.split('_')[1]);
    var y = Number(dragId.split('_')[2]);
    if (x === row && y === col) return;
    putPieceIntoBox(row, col);
    $scope.board[row][col] = $scope.board[x][y];
    $scope.board[x][y] = 0;
  }

  function dropBoxToBoard(dragId, row, col) {
    var index = Number(dragId.split('_')[1]);
    var piece = index < 16 ? $scope.blackBox[index] : $scope.redBox[index - 16];
    putOnBoardAt(index < 16 ? index : index - 16, piece, row, col);
  }

  $scope.dropIt = function(dragId, row, col) {
    if (dragId.indexOf('sq') === 0) {
      dropWithinBoard(dragId, row, col);
    } else {
      dropBoxToBoard(dragId, row, col);
    }
    updateFen();
  };

  function updateFen() {
    $scope.fen = fenService.pos2fen({
      board: $scope.board,
      turn: Number($scope.turn),
      halfMoveClock: -1,
      fullMoveNumber: $scope.fullMoveNumber
    });
  }

  function putPieceIntoBox(row, col) {
    var piece = $scope.board[row][col];
    if (piece === EMPTY) {
      return;
    }
    var box = piece > 0 ? $scope.redBox : $scope.blackBox;
    for (var i = 0; i < box.length; i++) {
      if (box[i] === EMPTY && Math.abs(piece) === INITIAL_RED_BOX[i]) {
        box[i] = piece;
        $scope.board[row][col] = EMPTY;
        return;
      }
    }
  };

  $scope.putIntoBox = function(row, col) {
    putPieceIntoBox(row, col);
    updateFen();
  };

  $scope.toCreateGame = function() {
    var nextToken = '/game/create/fen/' + fenService.fen2url($scope.fen);
    $location.path(nextToken);
  };

  function isAtOriginalCell(piece, row, col) {
    return INITIAL_BOARD[row][col] === piece;
  }

  function computeScore(piece, row, col) {
    if (isAtOriginalCell(piece, row, col)) {
      return 10000;
    }
    return 100 - Math.abs(ROWS - row * 2) - Math.abs(COLS - col * 2);
  }

  function findBestEmptyCell(piece) {
    var bestScore = -1, row = -1, col = -1;
    for (var i = 0; i < ROWS; i++)
      for (var j = 0; j < COLS; j++)
        if ($scope.board[i][j] === EMPTY) {
        var score = computeScore(piece, i, j);
        if (score > bestScore) {
          bestScore = score;
          row = i;
          col = j;
        }
      }
    return bestScore !== -1 ? {row: row, col: col} : undefined;
  };

  $scope.selectPieceInBox = function(index, piece) {
    putOnBoard(index, piece);
    updateFen();
  };

  function putOnBoard(index, piece) {
    var cell = findBestEmptyCell(piece);
    if (!cell) {
      return;
    }
    putOnBoardAt(index, piece, cell.row, cell.col);
  }

  function putOnBoardAt(index, piece, row, col) {
    if ($scope.board[row][col] !== EMPTY) {
      return;
    }
    $scope.board[row][col] = piece;
    if (piece > 0) {
      $scope.redBox[index] = 0;
    } else {
      $scope.blackBox[index] = 0;
    }
  }

  $scope.putAllIntoBoxes = function() {
    for (var i = 0; i < ROWS; i++)
      for (var j = 0; j < COLS; j++)
        if ($scope.board[i][j]) {
          putPieceIntoBox(i, j);
          $scope.board[i][j] = EMPTY;
        }
    putOnBoard(11, K);
    putOnBoard(11, -K);
    updateFen();
  };

  $scope.putAllOnBoard = function() {
    _.each($scope.redBox, function(piece, index) {
      if (piece !== EMPTY) {
        putOnBoard(index, piece);
      }
    });
    _.each($scope.blackBox, function(piece, index) {
      if (piece !== EMPTY) {
        putOnBoard(index, piece);
      }
    });
    updateFen();
  };

  $scope.redBox = angular.copy(INITIAL_RED_BOX);
  $scope.blackBox = _.map($scope.redBox, function(piece) {
    return piece * -1;
  });

  function findPieceInBox(box, piece) {
    return _.indexOf(box, piece);
  }

  function initializeFromPos(pos) {
    $scope.turn = pos.turn;
    $scope.fullMoveNumber = pos.fullMoveNumber;
    $scope.halfMoveClock = pos.halfMoveClock;
    for (var i = 0; i < ROWS; i++)
      for (var j = 0; j < COLS; j++)
        if (pos.board[i][j] !== EMPTY) {
          var piece = pos.board[i][j];
          var index = piece < 0 ?
              findPieceInBox($scope.blackBox, piece) :
              findPieceInBox($scope.redBox, piece);
          if (index !== -1) {
            putOnBoardAt(index, piece, i, j);
          }
        }
    updateFen();
  }

  function init(pos) {
    $scope.board = createEmptyBoard(ROWS, COLS);
    if (pos) {
      initializeFromPos(pos);
      return;
    }
    $scope.turn = RED;
    $scope.fullMoveNumber = 1;
    putOnBoard(11, K);
    putOnBoard(11, -K);
    updateFen();
  }

  init(pos);

  $scope.$watch('turn', function() {
    updateFen();
  });

  $scope.$watch('fullMoveNumber', function() {
    updateFen();
  });
});


tkControllers.controller('SearchBarCtrl', function(
    $scope, $location, $timeout, fenService) {

  $scope.search = function(e) {
    var query = $scope.searchData.queryString;
    if (!fenService.fen2pos(query)) {
      var params = new Params();
      params.set('q', query);
      $location.path('/search/' + params.encode());
      return;
    }
    $scope.searchData.queryString = '';
    if (e) e.stopPropagation();
    var nextToken = '/fen/create/fen/' + fenService.fen2url(query);
    $location.path(nextToken);
  };

  $scope.createGame = function(e) {
    var fen = $scope.searchData.queryString || '';
    if (!fenService.fen2pos(fen)) {
      return;
    }
    $scope.searchData.queryString = '';
    e.stopPropagation();
    var nextToken = '/game/create/fen/' + fenService.fen2url(fen);
    $location.path(nextToken);
  };
});


tkControllers.controller('SearchCtrl', function(
    $scope, $routeParams, $location, $timeout, $sce,
    dbService, annotateService) {

  $scope.mainNav.tab = 'document';

  $scope.ITEMS_PER_PAGE = 10;

  function searchSuccessCallback(response) {
    var idsToViews = {};
    _.each(response.gameMetadatas, function(gameMetadata) {
      idsToViews[gameMetadata.id] = gameMetadata.views;
    });
    _.each(response.games, function(game) {
      game.categoryText = $scope.CATEGORIES[Number(game.categoryIndex)];
      game.categoryKeyword =
          $scope.CATEGORY_KEYWORDS[Number(game.categoryIndex)];
      game.annotated_title = $sce.trustAsHtml(
          annotateService.annotate(game.title, $scope.searchData.queryString));
      game.annotated_book = $sce.trustAsHtml(
          annotateService.annotate(game.book, $scope.searchData.queryString));
      game.encoded_book = encodeURIComponent(encodeURIComponent(game.book));
      game.annotated_username = $sce.trustAsHtml(
          annotateService.annotate(
              game.username, $scope.searchData.queryString));
      game.views = idsToViews[game.id] || 0;
    });
    $scope.searchResults = response.games;
    $scope.totalItems = Number(response.numberFound);
    $scope.data.loading--;
  }

  var params = new Params($routeParams.params);

  $scope.start = Number(params.get('start') || '0');

  $scope.searchData.queryString = params.get('q');

  $scope.searchResults = [];
  $scope.totalItems = 0;
  $scope.data.loading++;
  dbService.searchGames(
      params.get('q'), params.get('start')).then(searchSuccessCallback);

  $scope.currentPage =
      Math.floor(params.get('start') / $scope.ITEMS_PER_PAGE) + 1;

  $scope.selectPage = function(page) {
    params.set('start', (page - 1) * $scope.ITEMS_PER_PAGE);
    $location.path('/search/' + params.encode());
  };

  $timeout(function() {
    $scope.searchData.searchBoxFocused = true;
  });

  $scope.$on('$routeChangeStart', function() {
    $scope.searchData.queryString = '';
  });
});


tkControllers.controller('ShowGameCtrl', function(
    $scope, $routeParams, dbService) {
  $scope.game = dbService.getGame($routeParams.gameId);
});


tkControllers.controller('DocCtrl', function(
    $scope, $location, $timeout,
    gameService, treeService, fenService, problemService) {

  $scope.boardApi = {};

  $scope.highlights = [{}, {}];

  var line;
  var pos;
  var selectedRow, selectedCol;

  $scope.autoMode = false;
  $scope.commentFull = false;

  function start() {
    $scope.board = gameService.getBoard();
    $scope.turn = gameService.getTurn();
    line = treeService.getLine();
    $scope.currentLineIndex = 0;
    $scope.moveTable = computeMoveTable(line);
    $scope.variations = computeVariations($scope.currentLineIndex);
    $scope.line = line;
  };

  $scope.$watch('fen', function(fen) {
    pos = fenService.fen2pos($scope.fen);
    if (!pos) {
      console.log('Invalid FEN: ' + fen);
      pos = fenService.getStartingPosition();
    }
    start();
  });

  $scope.$watch('editMode', function() {
    forgetSelectedPiece();
  });

  $scope.extractMoveQualityFromComment = function(index, comment) {
    if (index === 0 || !comment) return '';
    var QUALITIES = ['!!', '!?', '??', '?!', '!', '?'];
    return _.find(QUALITIES, function(quality) {
      return comment.indexOf(quality) === 0;
    }) || '';
  };

  function computeMoveTable(line) {
    var moveTable = [];
    _.each(line, function(node, index) {
      var first, second;
      if (node.isRoot()) {
        first = '-->';
        second = 'Bắt đầu';
        if (pos.fullMoveNumber > 1 || pos.turn === BLACK) {
          second += ' (...)';
        }
      } else {
        var moveIndex = Math.floor((index + 1) / 2) + pos.fullMoveNumber;
        if (pos.turn === RED) {
          moveIndex--;
        }
        first = ((index + pos.turn) % 2 === 0) ? (moveIndex + '.') : '';
        second = node.nodeData.humanMove;
        var variationCount = node.getSiblingCount();
        if (variationCount > 1) {
          second += ' (' + variationCount + ')';
        }
      }
      moveTable.push({first: first, second: second});
    });
    return moveTable;
  };

  function computeVariations(index) {
    var node = line[index];
    var variations = [];
    if (!node.isRoot()) {
      _.each(node.parent.children, function(sibling) {
        variations.push(sibling);
      });
    }
    return variations;
  }

  $scope.dropIt = function(dragId, row, col) {
    if (!$scope.editMode) {
      return;
    }
    var x = Number(dragId.split('_')[1]);
    var y = Number(dragId.split('_')[2]);
    if (x === row && y === col) return;
    maybeMakeMove(x, y, row, col);
  };

  function updateVariations() {
    $scope.variations = computeVariations($scope.currentLineIndex);
    $scope.currentVariationIndex = _.indexOf($scope.variations, getCurrentNode());
  }

  $scope.$on('new_move', function(e, move) {

    function row(c) {
      return c.charCodeAt() - '0'.charCodeAt();
    }

    function col(c) {
      return c.charCodeAt() - 'a'.charCodeAt();
    }

    var x = ROWS - 1 - row(move[1]);
    var y = col(move[0]);
    var u = ROWS - 1 - row(move[3]);
    var v = col(move[2]);

    maybeMakeMove(x, y, u, v);
  });

  function maybeMakeMove(x, y, u, v) {
    if (gameService.isValidMove(x, y, u, v)) {
      makeMove(x, y, u, v);
      return true;
    }
    return false;
  }

  function makeMove(x, y, u, v) {
    var humanMove = gameService.produceHumanMove(x, y, u, v);
    gameService.makeMove(x, y, u, v);
    $scope.turn = gameService.getTurn();
    treeService.addVariation(getCurrentNode(),
        {
          humanMove: humanMove,
          move: {
            x: x,
            y: y,
            u: u,
            v: v
          }
        }
    );
    line = treeService.getLine();
    $scope.moveTable = computeMoveTable(line);
    $scope.currentLineIndex++;
    updateVariations();
    $scope.line = line;
    forgetSelectedPiece();

    $timeout(function() {
      if (gameService.isCheckmated()) {
        alert($scope.turn === BLACK ? 'Đỏ thắng.' : 'Đen thắng.');
      }
    });
  }

  function forgetSelectedPiece() {
    selectedRow = undefined;
    selectedCol = undefined;
    updateSelectedPieces();
  }

  function updateSelectedPieces() {
    $scope.highlights[0].x = selectedRow;
    $scope.highlights[0].y = selectedCol;
  }

  function pieceToTurn(p) {
    if (!p) {
      return EMPTY;
    }
    return p > 0 ? RED : BLACK;
  }

  $scope.selectPiece = function(row, col) {
    if (!$scope.editMode) return;

    if ($scope.turn !== pieceToTurn($scope.board[row][col])) {
      if (selectedRow !== undefined) {
        $scope.selectBoardCell(row, col);
      }
      return;
    }

    if (selectedRow === row && selectedCol === col) {
      forgetSelectedPiece();
    } else {
      selectedRow = row;
      selectedCol = col;
      updateSelectedPieces();
    }
  };

  $scope.selectBoardCell = function(row, col) {
    if (selectedRow === undefined || selectedCol === undefined) return;
    if (selectedRow === row && selectedCol === col) return;
    maybeMakeMove(selectedRow, selectedCol, row, col);
  };

  $scope.isHighlighted = function(row, col) {
    if (!$scope.editMode ||
        selectedRow === undefined || selectedCol === undefined) return false;
    return row === selectedRow && col === selectedCol;
  };

  $scope.selectMove = function(index) {
    if (index === $scope.currentLineIndex) {
      return;
    }
    if (index < $scope.currentLineIndex) {
      for (var i = 0; i < $scope.currentLineIndex - index; i++) {
        gameService.unMakeMove();
      }
    } else {
      for (var i = $scope.currentLineIndex + 1; i <= index; i++) {
        var move = line[i].nodeData.move;
        gameService.makeMove(move.x, move.y, move.u, move.v);
      }
    }
    $scope.turn = gameService.getTurn();
    $scope.currentLineIndex = index;
    updateVariations();
    forgetSelectedPiece();
  };

  function getCurrentNode() {
    return line[$scope.currentLineIndex];
  }

  $scope.selectVariation = function(vIndex) {
    if ($scope.currentVariationIndex === vIndex) {
      return;
    }
    treeService.selectVariation(getCurrentNode(), vIndex);
    line = treeService.getLine();
    $scope.moveTable = computeMoveTable(line);
    updateVariations();
    gameService.unMakeMove();
    var move = getCurrentNode().nodeData.move;
    gameService.makeMove(move.x, move.y, move.u, move.v);
    $scope.turn = gameService.getTurn();
    $scope.line = line;
    forgetSelectedPiece();
  };

  $scope.moveVariationUp = function() {
    if (!$scope.variations.length || !$scope.currentVariationIndex) {
      return;
    }
    treeService.swapNodes(
        getCurrentNode().parent, $scope.currentVariationIndex, $scope.currentVariationIndex - 1);
    updateVariations();
  };

  $scope.moveVariationDown = function() {
    if (!$scope.variations.length || $scope.currentVariationIndex >= $scope.variations.length - 1) {
      return;
    }
    treeService.swapNodes(
        getCurrentNode().parent, $scope.currentVariationIndex, $scope.currentVariationIndex + 1);
    updateVariations();
  };

  $scope.deleteVariation = function() {
    if (!$scope.variations.length) {
      return;
    }
    if (!confirm('Bạn có thực sự muốn xóa nước đi này không?')) {
      return;
    }
    treeService.removeChild(getCurrentNode().parent, $scope.currentVariationIndex);
    line = treeService.getLine();
    $scope.currentLineIndex--;
    updateVariations();
    $scope.moveTable = computeMoveTable(line);
    gameService.unMakeMove();
    $scope.turn = gameService.getTurn();
    $scope.line = line;
  };

  $scope.goFirst = function() {
    $scope.selectMove(0);
  };

  $scope.goPrevious = function() {
    if ($scope.currentLineIndex - 1 >= 0) {
      $scope.selectMove($scope.currentLineIndex - 1);
    }
  };

  $scope.goNext = function() {
    if ($scope.currentLineIndex + 1 < line.length) {
      $scope.selectMove($scope.currentLineIndex + 1);
    }
  };

  $scope.goLast = function() {
    $scope.selectMove(line.length - 1);
  };

  function produceFen() {
    function indexToFullMove(index) {
      return Math.floor((index + 1)/2);
    }

    function posToIndex(pos) {
      var index = pos.fullMoveNumber * 2;
      if (pos.turn === RED) index--;
      return index;
    }

    function getFullMoveNumber(initialPos, currentLineIndex) {
      var index = posToIndex(initialPos) + currentLineIndex;
      return indexToFullMove(index);
    }

    var fen = fenService.pos2fen({
      board: $scope.board,
      turn: $scope.turn,
      fullMoveNumber: getFullMoveNumber(pos, $scope.currentLineIndex)
    });

    return fen;
  }

  function autoPlayNextMove() {
    if ($scope.currentLineIndex === line.length - 1) {
      $scope.autoMode = false;
      return;
    }
    var index = $scope.currentLineIndex + 1;
    var move = line[index].nodeData.move;
    gameService.makeMove(move.x, move.y, move.u, move.v);
    $scope.turn = gameService.getTurn();
    $scope.currentLineIndex = index;
    updateVariations();
    forgetSelectedPiece();
  }

  var autoPlayInterval = setInterval(function() {
    if ($scope.autoMode) {
      $scope.$apply(autoPlayNextMove);
    }
  }, 1000);

  $scope.$on('$destroy', function() {
    clearInterval(autoPlayInterval);
    $scope.autoMode = false;
  });

  $scope.autoPlay = function() {
    $scope.autoMode = true;
  };

  $scope.manualPlay = function() {
    $scope.autoMode = false;
  };

  $scope.showFen = function() {
    alert(produceFen());
  };

  $scope.studyPosition = function() {
    var nextToken = '/study/fen/' + fenService.fen2url(produceFen());
    $location.path(nextToken);
  };

  $scope.createProblem = function() {
    problemService.createProblem(produceFen(), '', '' ,'');
  };

  $scope.pressEnter = function(value) {
    processHumanMoves(value);
  };

  $scope.pressSpace = function(value) {
    // processHumanMoves(value);
  };

  function processHumanMoves(value) {
    var moveTokens = value.split(' ');
    while (moveTokens.length > 0) {
      var next = moveTokens[0];
      if (!next.length) {
        moveTokens.shift();
      } else {
        if (processHumanMove(next)) {
          moveTokens.shift();
        } else {
          break;
        }
      }
    }
    $scope.moveString = moveTokens.join(' ');
    $scope.invalidMove = moveTokens.length ? moveTokens[0] : '';
  }

  function processHumanMove(moveString) {
    var move = gameService.parseHumanMove(moveString);
    if (move) {
      return maybeMakeMove(move.x, move.y, move.u, move. v);
    }
    return false;
  }

  $scope.toggleFullCommentMode = function() {
    $scope.commentFull = !$scope.commentFull;
  };
});


tkControllers.controller('SigninCtrl', function(
    $scope, $timeout, $location, cookieService, authService, userService) {

  if (authService.isAuthenticated()) {
    $location.path('/');
    return;
  }

  $timeout(function() {
    $scope.usernameFocused = true;
  });

  $scope.signIn = function() {
    userService.signIn($scope.username, $scope.password)
        .then(function(response) {
          cookieService.set(SESSION_ID, response.sid);
          authService.signIn($scope.username);
          $location.path('/');
        });
  };
});


tkControllers.controller('SignupCtrl', function(
    $scope, $timeout, $location, cookieService, $routeParams,
    userService, authService) {

  if (authService.isAuthenticated()) {
    $location.path('/');
    return;
  }

  var inviteCode = $routeParams.inviteCode || '';

  $scope.showPassword = false;

  $timeout(function() {
    $scope.emailFocused = true;
  });

  $scope.signUp = function() {
    userService.signUp(
        $scope.email, $scope.username, $scope.password, inviteCode)
        .then(function(response) {
          cookieService.set(SESSION_ID, response.sid);
          authService.signIn($scope.username);
          $location.path('/');
        });
  };
});


tkControllers.controller('AccessDeniedCtrl', function() {});


tkControllers.controller('InviteCtrl', function($scope, inviteService) {

  function createInviteUrl(inviteId) {
    return window.location.origin + '/#/signup/' + inviteId;
  }

  $scope.generateInvite = function() {
    inviteService.createInvite().then(function(response) {
      $scope.inviteUrl = createInviteUrl(response.inviteId);
    });
  };
});


tkControllers.controller('ProblemSetCtrl', function(
    $scope, $location, problemService) {
  $scope.mainNav.tab = 'practice';

  $scope.ITEMS_PER_PAGE = 10;

  var params = $location.search();

  var start = params.start || 0;

  $scope.currentPage = Math.floor(start / $scope.ITEMS_PER_PAGE) + 1;

  problemService.getProblems(
      $scope.currentPage - 1, $scope.ITEMS_PER_PAGE, 'id').then(function(response) {
    $scope.totalItems = response.total;
    $scope.problems = _.map(response.problem_search, function(result) {
      var problem = result.problem;
      problem.solved = result.isSolved;
      return problem;
    });
  });

  $scope.selectPage = function(page) {
    var start = (page - 1) * $scope.ITEMS_PER_PAGE;
    $location.search({start: start});
  };
});


tkControllers.controller('ProblemCtrl', function(
  $scope, $timeout, $routeParams, $sce, $facebook,
  authService, problemService, gameService, fenService, engineService) {

  $scope.attempting = false;
  $scope.authenticated = authService.isAuthenticated();

  $scope.getShareUrl = function(problemId) {
    var url = 'http://www.facebook.com/plugins/like.php?href=' +
        'http%3A%2F%2Ftuongky.ngrok.com%2Fproblem%2F' + problemId +
        '&width&layout=button_count&action=like&show_faces=true&share=true&appId=' + FB_APP_ID;
    return $sce.trustAsResourceUrl(url);
  };

  $scope.mainNav.tab = 'practice';

  $scope.boardApi = {};
  $scope.highlights = [{}, {}];

  $scope.playerTypes = [0, HUMAN, COMPUTER];

  var selectedRow, selectedCol;
  var fen;

  function init(problem) {
    var pos = fenService.fen2pos(problem.fen);
    if (pos) {
      gameService.init(pos.board, pos.turn);
    } else {
      alert('FEN không hợp lệ.');
      gameService.init();
    }
    $scope.board = gameService.getBoard();
    $scope.turn = gameService.getTurn();
  }

  var problemId = $routeParams.problemId;
  problemService.getProblem(problemId).then(function(response) {
    var problem = response.problem;
    problem.solved = response.solved;
    problem.attempts = response.attempt_count;
    $scope.problem = problem;
    fen = problem.fen;
    init(problem);
  }, function() {
    alert('Bài không tồn tại.');
  });

  $scope.dropIt = function(dragId, row, col) {
    if (!$scope.attempting) {
      return;
    }
    var x = Number(dragId.split('_')[1]);
    var y = Number(dragId.split('_')[2]);
    if (x === row && y === col) return;
    maybeMakeMove(x, y, row, col);
  };

  function maybeMakeMove(x, y, u, v) {
    if (gameService.isValidMove(x, y, u, v)) {
      makeMove(x, y, u, v);
      return true;
    }
    return false;
  }

  function solveIt() {
    problemService.solve($scope.attemptId).then(function(response) {
      $scope.problem.solved = true;
      $scope.attempting = false;
    });
  };

  function makeMove(x, y, u, v) {
    gameService.makeMove(x, y, u, v);
    $scope.turn = gameService.getTurn();
    $timeout(function() {
      if (gameService.isCheckmated()) {
        if ($scope.turn === BLACK) {
          alert('Đỏ thắng. Xin chức mừng, bạn đã vượt qua thử thách này!');
          solveIt();
        } else {
          alert('Đen thắng.');
          init($scope.problem);
          $scope.attempting = false;
        }
      }
    });
  }

  function maybeMakeAnimatedMove(x, y, u, v) {
    if (!gameService.isValidMove(x, y, u, v)) {
      return;
    }
    forgetSelectedPiece();
    $scope.boardApi.animateMove(x, y, u, v).then(function() {
      makeMove(x, y, u, v);
    });
  }

  function forgetSelectedPiece() {
    selectedRow = undefined;
    selectedCol = undefined;
    updateSelectedPieces();
  }

  function updateSelectedPieces() {
    $scope.highlights[0].x = selectedRow;
    $scope.highlights[0].y = selectedCol;
  }

  function pieceToTurn(p) {
    if (!p) {
      return EMPTY;
    }
    return p > 0 ? RED : BLACK;
  }

  $scope.hasNotMoved = function() {
    return !gameService.getMoves() || !gameService.getMoves().length;
  };

  $scope.selectPiece = function(row, col) {
    if (!$scope.attempting) {
      return;
    }
    if ($scope.turn !== pieceToTurn($scope.board[row][col])) {
      if (selectedRow !== undefined) {
        $scope.selectBoardCell(row, col);
      }
      return;
    }

    if (selectedRow === row && selectedCol === col) {
      forgetSelectedPiece();
    } else {
      selectedRow = row;
      selectedCol = col;
      updateSelectedPieces();
    }
  };

  $scope.selectBoardCell = function(row, col) {
    if (selectedRow === undefined || selectedCol === undefined) return;
    if (selectedRow === row && selectedCol === col) return;
    maybeMakeAnimatedMove(selectedRow, selectedCol, row, col);
  };

  function computerPlay() {
    var moves = _.map(gameService.getMoves(), function(move) {
      function where(x, y) {
        return String.fromCharCode(97 + y) + (ROWS - 1 - x);
      }
      return where(move.x, move.y) + where(move.u, move.v);
    }).join(' ');
    return engineService.think(fen, moves);
  }

  function handleComputerMove(move) {
    function row(c) {
      return c.charCodeAt() - '0'.charCodeAt();
    }

    function col(c) {
      return c.charCodeAt() - 'a'.charCodeAt();
    }

    var x = ROWS - 1 - row(move[1]);
    var y = col(move[0]);
    var u = ROWS - 1 - row(move[3]);
    var v = col(move[2]);

    maybeMakeAnimatedMove(x, y, u, v);
  }

  function maybeComputerPlay(inTurn) {
    if (Number(inTurn) === COMPUTER) {
      if (gameService.isCheckmated()) {
        return;
      }
      computerPlay().then(handleComputerMove);
    }
  }

  $scope.$watch(function() {
    return $scope.turn;
  }, function(turn) {
    maybeComputerPlay($scope.playerTypes[turn]);
  });

  $scope.$watch(function() {
    return $scope.playerTypes[gameService.getTurn()];
  }, maybeComputerPlay);

  $scope.attempt = function() {
    if ($scope.problem.solved) {
      if (!confirm('Bạn đã vượt qua thử thách này rồi. Bạn có thực sự muốn thử lại không?')) {
        return;
      }
    }

    problemService.attempt($scope.problem.id).then(function(response) {
      $scope.attempting = true;
      $scope.attemptId = response.attemptId;
      $scope.problem.attempts++;
      init($scope.problem);
    });
  };

  $scope.retry = function() {
    problemService.attempt($scope.problem.id).then(function(response) {
      $scope.attempting = true;
      $scope.attemptId = response.attemptId;
      $scope.problem.attempts++;
      init($scope.problem);
    });
  };

  $scope.signInWithFacebook = function() {
    $facebook.login();
  };
});


tkControllers.controller('SolvedByCtrl', function(
    $scope, $routeParams, $location, solutionService) {
  $scope.mainNav.tab = 'practice';

  var problemId = $routeParams.problemId;

  $scope.ITEMS_PER_PAGE = 10;

  var params = $location.search();

  var start = params.start || 0;

  $scope.currentPage = Math.floor(start / $scope.ITEMS_PER_PAGE) + 1;

  solutionService.getSolutionsForProblem(
    problemId, $scope.currentPage - 1, $scope.ITEMS_PER_PAGE)
    .then(function(response) {
      $scope.problem = response.problem;
      $scope.totalItems = $scope.problem.solvers;
      $scope.items = response.items;
    });


  $scope.selectPage = function(page) {
    var start = (page - 1) * $scope.ITEMS_PER_PAGE;
    $location.search({start: start});
  };
});


tkControllers.controller('RankCtrl', function(
    $scope, $location, rankService, levelService) {
  $scope.mainNav.tab = 'rank';

  $scope.ITEMS_PER_PAGE = 10;

  var params = $location.search();

  var start = params.start || 0;

  $scope.currentPage = Math.floor(start / $scope.ITEMS_PER_PAGE) + 1;

  rankService.getRanks(
      $scope.currentPage - 1, $scope.ITEMS_PER_PAGE).then(function(response) {
    $scope.totalItems = response.total;
    $scope.ranks = response.usersRank;
    _.each($scope.ranks, function(rank) {
      rank.levelDesc = levelService.getLevelDesc(
          rank.userMetadata.solves, response.problemCount);
    });
  });

  $scope.selectPage = function(page) {
    var start = (page - 1) * $scope.ITEMS_PER_PAGE;
    $location.search({start: start});
  };
});


tkControllers.controller('ProfileCtrl', function(
    $scope, $routeParams, $location, authService, userService, levelService) {
  $scope.mainNav.tab = 'profile';

  var fbId = $routeParams.fbId;

  if (!fbId) {
    if (!authService.isAuthenticated()) {
      $location.path('/accessdenied');
      return;
    }
    fbId = authService.getUser().fbId;
  }

  userService.getProfile(fbId).then(function(response) {
    $scope.profile = response;
    $scope.profile.levelDesc = levelService.getLevelDesc(
        $scope.profile.metadata.solves, response.problemCount);
  });
});


tkControllers.controller('FbSigninCtrl', function($scope, $facebook) {

  $scope.signInWithFb = function() {
    $facebook.login();
  };
});
