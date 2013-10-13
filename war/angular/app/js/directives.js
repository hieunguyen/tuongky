'use strict';

/* Directives */

var tkDirectives = angular.module('tkApp.directives', ['ui.bootstrap.pagination']);

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


tkDirectives.directive('board', function() {
  return {
    restrict: 'AE',
    scope: {
      fen: '='
    },
    replace: true,
    templateUrl: 'partials/board.html',
    controller: 'BoardCtrl',
    link: function(scope, element, attrs) {
    }
  };
});
