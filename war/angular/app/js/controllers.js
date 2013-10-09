'use strict';

/* Controllers */

var tkControllers = angular.module('tkApp.controllers', []);

tkControllers.controller('CreateGameCtrl', function(
    $scope, $routeParams, gameService, treeService, fenService) {

  var fen = fenService.getStartingFen();
  if ($routeParams.encodedFen) {
    fen = fenService.url2fen($routeParams.encodedFen);
  }
  var pos = fenService.fen2pos(fen);
  if (!pos) {
    console.log('Invalid FEN: ' + fen);
    pos = fenService.getStartingPosition();
  }

  var PIECE_IMAGE_NAME_MAP = {};
  PIECE_IMAGE_NAME_MAP[K] = 'k';
  PIECE_IMAGE_NAME_MAP[A] = 'a';
  PIECE_IMAGE_NAME_MAP[E] = 'e';
  PIECE_IMAGE_NAME_MAP[R] = 'r';
  PIECE_IMAGE_NAME_MAP[C] = 'c';
  PIECE_IMAGE_NAME_MAP[H] = 'h';
  PIECE_IMAGE_NAME_MAP[P] = 'p';

  var line;

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

  $scope.restart = function(board, turn) {
    gameService.init(board, turn);
    $scope.board = gameService.getBoard();
    $scope.turn = gameService.getTurn();
    treeService.init();
    line = treeService.getLine();
    $scope.currentLineIndex = 0;
    $scope.moveTable = computeMoveTable(line);
    $scope.variations = computeVariations($scope.currentLineIndex);
  };
  $scope.restart(pos.board, pos.turn);

  $scope.undo = function() {
    gameService.unMakeMove();
    $scope.turn = gameService.getTurn();
  };

  $scope.getImageName = function(piece) {
    if (!piece) return 'dot';
    return (piece > 0 ? 'r' : 'b') + PIECE_IMAGE_NAME_MAP[Math.abs(piece)];
  };

  $scope.dropIt = function(dragId, row, col) {
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


tkControllers.controller('SandboxCtrl', function($scope, dbService) {

  var USERNAME = 'nguoituyet';
  var CATEGORIES = ['', 'Ván đấu', 'Khai cuộc', 'Trung cuộc', 'Tàn cuộc', 'Cờ thế'];

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

  $scope.create = function() {
    dbService.createGame($scope.game, USERNAME);
  };

  function searchSuccessCallback(searchResults) {
    _.each(searchResults, function(searchResult) {
      searchResult.categoryText = CATEGORIES[Number(searchResult.category)];
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


tkControllers.controller('SearchBarCtrl', function($scope, $location) {

  $scope.search = function() {
    var params = new Params();
    params.set('q', $scope.queryString);
    $location.path('/search/' + params.encode());
  };
});


tkControllers.controller('SearchCtrl', function(
    $scope, $routeParams, $location, dbService) {

  $scope.ITEMS_PER_PAGE = 10;

  var CATEGORIES = ['', 'Ván đấu', 'Khai cuộc', 'Trung cuộc', 'Tàn cuộc', 'Cờ thế'];

  function searchSuccessCallback(response) {
    _.each(response.games, function(game) {
      game.categoryText = CATEGORIES[Number(game.categoryIndex)];
    });
    $scope.searchResults = response.games;
    $scope.totalItems = Number(response.numberFound);
  }

  var params = new Params($routeParams.params);

  $scope.start = Number(params.get('start') || '0');

  dbService.searchGames(params.get('q'), params.get('start')).then(searchSuccessCallback);

  $scope.currentPage = Math.floor(params.get('start') / $scope.ITEMS_PER_PAGE) + 1;

  $scope.selectPage = function(page) {
    params.set('start', (page - 1) * $scope.ITEMS_PER_PAGE);
    $location.path('/search/' + params.encode());
  };
});


tkControllers.controller('ShowGameCtrl', function(
    $scope, $routeParams, dbService) {
  $scope.game = dbService.getGame($routeParams.gameId);
});
