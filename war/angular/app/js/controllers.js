'use strict';

/* Controllers */

var tkControllers = angular.module('tkApp.controllers', []);

tkControllers.controller('AppCtrl', function(
    $scope, $location, notificationService, authService, userService) {

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

  $scope.$on('$routeChangeStart', function(event, next) {
    if (next.accessLevel > Roles.ANONYMOUS && !authService.isAuthenticated()) {
      $location.path('/signin');
      return;
    }
    if (next.accessLevel === Roles.ADMIN && !authService.isAdmin()) {
      $location.path('/accessdenied');
    }
  });

  $scope.data.loading = true;
  userService.getStatus().then(function(username) {
    $scope.data.loading = false;
    if (username) {
      authService.signIn(username);
    }
  });
});


tkControllers.controller('AuthController', function(
    $scope, $location, authService) {

  $scope.signIn = function() {
    $location.path('/signin');
  };

  $scope.signUp = function() {
    $location.path('/signup');
  };

  $scope.signOut = function() {
    authService.signOut();
  };

  $scope.invite = function() {
    $location.path('/invite');
  };
});


tkControllers.controller('CreateGameCtrl', function(
    $scope, $routeParams, $location, $timeout,
    gameService, treeService, fenService, dbService, game,
    vnService, bookService) {

  var fen = fenService.getStartingFen();

  if (game) {
    $scope.game = game;
    $scope.game.category = game.categoryIndex;
    try {
      var dataObj = JSON.parse(game.data);
      treeService.init(dataObj.moveTree);
      fen = dataObj.fen;
    } catch (error) {
      alert('Game error.');
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
  gameService.init(pos.board, pos.turn);
  $scope.fen = fen;
  $scope.editMode = !game;

  $scope.books = [];
  bookService.getBooksForUser($scope.user.username).then(function(books) {
    console.log(books);
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
    bookService.addBook(book);
    $scope.books = bookService.getBooks();
  }

  $scope.saveGame = function() {
    $scope.game.data = JSON.stringify({
      moveTree: treeService.toObject(),
      fen: fen
    });
    if (game) {
      dbService.saveGame($scope.game, $scope.user.username).then(function() {
        addBook($scope.game.book);
        $scope.editMode = false;
      });
    } else {
      dbService.createGame($scope.game, $scope.user.username).then(
          function(gameId) {
            addBook($scope.game.book);
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
    if (!confirm('Bạn có thực sự muốn xóa game này không?')) {
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


tkControllers.controller('CreateFenCtrl', function($scope, $location, fenService) {
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
    if (1 + 1 === 2) {
    }
  };

  $scope.dropIt = function(dragId, row, col) {
    var x = parseInt(dragId.split('_')[1]);
    var y = parseInt(dragId.split('_')[2]);
    if (x === row && y === col) return;
    putPieceIntoBox(row, col);
    $scope.board[row][col] = $scope.board[x][y];
    $scope.board[x][y] = 0;
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

  $scope.gogo = function() {
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
    $scope.board[cell.row][cell.col] = piece;
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

  $scope.board = createEmptyBoard(ROWS, COLS);
  $scope.turn = RED;
  $scope.fullMoveNumber = 1;
  putOnBoard(11, K);
  putOnBoard(11, -K);
  updateFen();

  $scope.$watch('turn', function() {
    updateFen();
  });

  $scope.$watch('fullMoveNumber', function() {
    updateFen();
  });
});


tkControllers.controller('SandboxCtrl', function(
    $scope, $timeout, dbService, gameService, treeService, fenService) {

  $scope.clear = function() {
    $scope.game = {
      category: '1',
      title: '',
      list: ''
    };
    $scope.searchResults = undefined;
    $scope.queryString = '';
  };

  $scope.clear();

  $timeout(function() {
    $scope.searchData.searchBoxFocused = true;
  });

  $scope.create = function() {
    dbService.createGame($scope.game, $scope.user.username);
  };

  function searchSuccessCallback(searchResults) {
    _.each(searchResults, function(searchResult) {
      searchResult.categoryText =
          $scope.CATEGORIES[Number(searchResult.category)];
    });
    $scope.searchResults = searchResults;
  }

  $scope.search = function() {
    dbService.searchGames($scope.queryString).then(searchSuccessCallback);
  };

  $scope.advancedSearch = function() {
    dbService.advancedSearchGames($scope.game).then(searchSuccessCallback);
  };

  $scope.init = function() {
    dbService.initGames().then(function() {
      alert('Init done.');
    });
  };
});


tkControllers.controller('SearchBarCtrl', function(
    $scope, $location, $timeout) {

  $scope.search = function() {
    var params = new Params();
    params.set('q', $scope.searchData.queryString);
    $location.path('/search/' + params.encode());
  };

  $timeout(function() {
    $scope.searchData.searchBoxFocused = true;
  });
});


tkControllers.controller('SearchCtrl', function(
    $scope, $routeParams, $location, $timeout, dbService, annotateService) {

  $scope.ITEMS_PER_PAGE = 10;

  function searchSuccessCallback(response) {
    _.each(response.games, function(game) {
      game.categoryText = $scope.CATEGORIES[Number(game.categoryIndex)];
      game.categoryKeyword =
          $scope.CATEGORY_KEYWORDS[Number(game.categoryIndex)];
      game.annotated_title = annotateService.annotate(
          game.title, $scope.searchData.queryString);
      game.annotated_book = annotateService.annotate(
          game.book, $scope.searchData.queryString);
      game.annotated_username = annotateService.annotate(
          game.username, $scope.searchData.queryString);
    });
    $scope.searchResults = response.games;
    $scope.totalItems = Number(response.numberFound);
    $scope.data.loading = false;
  }

  var params = new Params($routeParams.params);

  $scope.start = Number(params.get('start') || '0');

  $scope.searchData.queryString = params.get('q');

  $scope.searchResults = [];
  $scope.totalItems = 0;
  $scope.data.loading = true;
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


tkControllers.controller('BoardCtrl', function(
    $scope, gameService, treeService, fenService) {

  var PIECE_IMAGE_NAME_MAP = {};
  PIECE_IMAGE_NAME_MAP[K] = 'k';
  PIECE_IMAGE_NAME_MAP[A] = 'a';
  PIECE_IMAGE_NAME_MAP[E] = 'e';
  PIECE_IMAGE_NAME_MAP[R] = 'r';
  PIECE_IMAGE_NAME_MAP[C] = 'c';
  PIECE_IMAGE_NAME_MAP[H] = 'h';
  PIECE_IMAGE_NAME_MAP[P] = 'p';

  var line;
  var pos;

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

  $scope.getImageName = function(piece) {
    if (!piece) return 'dot';
    return (piece > 0 ? 'r' : 'b') + PIECE_IMAGE_NAME_MAP[Math.abs(piece)];
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
    var x = parseInt(dragId.split('_')[1]);
    var y = parseInt(dragId.split('_')[2]);
    if (x === row && y === col) return;
    if (gameService.isValidMove(x, y, row, col)) {
      makeMove(x, y, row, col);
    }
  };

  function updateVariations() {
    $scope.variations = computeVariations($scope.currentLineIndex);
    $scope.currentVariationIndex = _.indexOf($scope.variations, getCurrentNode());
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
  }

  $scope.selectPiece = function(row, col) {
    console.log('select a piece at ' + row + ' ' + col);
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
          cookieService.set('sid', response.sid);
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
          cookieService.set('sid', response.sid);
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
