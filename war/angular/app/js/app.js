'use strict';

var tkApp = angular.module('tkApp',
    ['tkApp.filters', 'tkApp.services', 'tkApp.directives', 'tkApp.controllers']);

tkApp.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/fen/create',
      {templateUrl: 'partials/create_fen.html', controller: 'CreateFenCtrl'});
  $routeProvider.when('/game/create',
      {templateUrl: 'partials/create_game.html', controller: 'CreateGameCtrl'});
  $routeProvider.when('/game/create/fen/:encodedFen',
      {templateUrl: 'partials/create_game.html', controller: 'CreateGameCtrl'});
  $routeProvider.when('/game/id/:gameId',
      {templateUrl: 'partials/show_game.html', controller: 'ShowGameCtrl'});
  $routeProvider.when('/search/:params',
      {templateUrl: 'partials/search.html', controller: 'SearchCtrl'});
  $routeProvider.when('/sandbox',
      {templateUrl: 'partials/sandbox.html', controller: 'SandboxCtrl'});
  $routeProvider.otherwise({redirectTo: '/sandbox'});
}]);

tkApp.config(['$httpProvider', function($httpProvider) {
  // Use x-www-form-urlencoded Content-Type
  $httpProvider.defaults.headers.post['Content-Type'] =
      'application/x-www-form-urlencoded;charset=utf-8';

  // Override $http service's default transformRequest
  $httpProvider.defaults.transformRequest = [function(data) {
    return angular.isObject(data) &&
        String(data) !== '[object File]' ? jQuery.param(data) : data;
  }];
}]);
