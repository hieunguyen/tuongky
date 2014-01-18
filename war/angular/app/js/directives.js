'use strict';

/* Directives */

var tkDirectives = angular.module('tkApp.directives',
    ['ui.bootstrap.pagination',
     'ui.bootstrap.alert',
     'ui.bootstrap.typeahead']);

tkDirectives.directive('draggable', function() {
  return function(scope, element) {
    var el = element[0];

    el.draggable = true;

    el.addEventListener('dragstart', function(e) {
      e.dataTransfer.effectAllowed = 'move';
      e.dataTransfer.clearData('Text');
      e.dataTransfer.setData('Text', this.id);
      this.classList.add('drag');
      return false;
    }, false);

    el.addEventListener('dragend', function(e) {
      this.classList.remove('drag');
      return false;
    }, false);
  }
});

tkDirectives.directive('droppable', function($parse) {
  return {
    link : function(scope, element, attrs) {
      var el = element[0];
      el.addEventListener('dragover', function(e) {
        e.dataTransfer.dropEffect = 'move';
        if (e.preventDefault)
          e.preventDefault(); // allows us to drop
        this.classList.add('over');
        return false;
      }, false);

      el.addEventListener('dragenter', function(e) {
        this.classList.add('over');
        return false;
      }, false);

      el.addEventListener('dragleave', function(e) {
        this.classList.remove('over');
        return false;
      }, false);

      el.addEventListener('drop', function(e) {
        if (e.preventDefault)
          e.preventDefault(); // allows us to drop
        if (e.stopPropagation)
          e.stopPropagation(); // Stops some browsers from redirecting.
        this.classList.remove('over');

        if (attrs.drop) {
          var dropFunc = $parse(attrs.drop);
          dropFunc(scope, {dragId: e.dataTransfer.getData('Text')});
          scope.$apply();
        }
        return false;
      }, false);
    }
  }
});


tkDirectives.directive('pressEnter', function() {
  return function(scope, element, attrs) {
    element.bind("keydown keypress", function(event) {
      if(event.which === 13) {
        scope.$apply(function() {
          scope.$eval(attrs.pressEnter);
        });
        event.preventDefault();
      }
    });
  };
});


tkDirectives.directive('tkDoc', function() {
  return {
    restrict: 'AE',
    scope: {
      fen: '=',
      editMode: '=',
      hideMoveList: '=',
      hideVariations: '=',
      hideComment: '=',
      hideMoveText: '=',
      hideStudyBtn: '='
    },
    replace: true,
    templateUrl: 'partials/tk_doc.html',
    controller: 'DocCtrl'
  };
});


tkDirectives.directive('tkFocus', function($parse) {
  var ownFocusEvent = false;
  return function(scope, element, attrs) {
    scope.safeApply = function(fn) {
      var phase = this.$root.$$phase;
      if (phase == '$apply' || phase == '$digest') {
        if (fn && (typeof(fn) === 'function')) {
          fn();
        }
      } else {
        this.$apply(fn);
      }
    };

    var model = $parse(attrs['tkFocus']);
    element.bind('focus', function() {
      if (!ownFocusEvent) {
        scope.safeApply(function() {
          model.assign(scope, true);
        });
      }
      ownFocusEvent = false;
    });
    element.bind('blur', function() {
      scope.safeApply(function() {
        model.assign(scope, false);
      });
    });
    scope.$watch(model, function(newValue, oldValue) {
      if (newValue && !oldValue) {
        ownFocusEvent = true;
        $(element).focus();
      }
    });
  };
});


tkDirectives.directive('autoScrollTo', function($timeout) {
  return {
    link: function(scope, element, attrs) {
      scope.$watch(attrs.autoScrollTo, function(to) {
        var scrollTo = $('tr', element).eq(to);
        if (scrollTo) {
          var container = $(element);
          container.scrollTop(
            scrollTo.offset().top - container.offset().top +
                container.scrollTop()
          );
        }
      });
    }
  };
});


tkDirectives.directive('shortcut', function() {
  return {
    restrict: 'E',
    scope: {
      handler: '&'
    },
    link: function(scope, element, attrs) {
      var keyCode = attrs['keycode'];
      var eventName = 'keydown.' + keyCode + '.' +
          Math.floor(Math.random() * 1000000);
      $(document).on(eventName, function(e) {
        scope.$apply(function() {
          if (e.which === Number(keyCode)) {
            if (!$(e.target).attr('stop-propagation')) {
              scope.handler();
            }
          }
        });
      });
      scope.$on('$destroy', function() {
        $(document).off(eventName);
      });
    }
  };
});


tkDirectives.directive('moveInput', function($parse, $timeout) {
  return {
    scope: {
      onSpace: '&',
      onEnter: '&'
    },
    link: function(scope, element, attrs) {
      var modelFn = $parse(attrs['ngModel']);
      var eventName = 'keydown.13_32';
      $(element).on(eventName, function(e) {
        if (e.which === 13) {
          scope.onEnter({value: modelFn(scope)});
        }
        if (e.which === 32) {
          $timeout(function() {
            scope.onSpace({value: modelFn(scope)});
          });
        }
      });
      scope.$on('$destroy', function() {
        $(element).off(eventName);
      });
    }
  };
});


tkDirectives.directive('tkBoard', function($q) {

  var PIECE_IMAGE_NAME_MAP = {};
  PIECE_IMAGE_NAME_MAP[K] = 'k';
  PIECE_IMAGE_NAME_MAP[A] = 'a';
  PIECE_IMAGE_NAME_MAP[E] = 'e';
  PIECE_IMAGE_NAME_MAP[R] = 'r';
  PIECE_IMAGE_NAME_MAP[C] = 'c';
  PIECE_IMAGE_NAME_MAP[H] = 'h';
  PIECE_IMAGE_NAME_MAP[P] = 'p';

  return {
    replace: true,
    scope: {
      board: '=data',
      api: '=',
      dropOn: '&'
    },
    templateUrl: 'partials/tk_board.html',
    controller: function($scope) {
      $scope.getImageName = function(piece) {
        if (!piece) return 'dummy.png';
        return (piece > 0 ? 'r' : 'b') +
            PIECE_IMAGE_NAME_MAP[Math.abs(piece)] + '.gif';
      };
      $scope.dropIt = function(dragId, row, col) {
        $scope.dropOn({dragId: dragId, row: row, col: col});
      };
    },
    link: function(scope, elem, attrs) {
      scope.api.go = function(x, y, u, v) {
        var deferred = $q.defer();
        var imgs = $('.tk-board-cell img', elem);
        var img = imgs.eq(x * 9 + y);
        var w = img.height();
        var h = img.width();
        $(img).animate({left: (v - y) * w, top: (u - x) * h}, 200, function() {
          deferred.resolve(true);
        });
        return deferred.promise;
      };
    }
  };
});
