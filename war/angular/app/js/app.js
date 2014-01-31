'use strict';

var tkApp = angular.module('tkApp',
    ['ngRoute',
     'ngAnimate',
     'tkApp.filters',
     'tkApp.services',
     'tkApp.directives',
     'tkApp.controllers']);

tkApp.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/fen/create',
      {
        templateUrl: 'partials/create_fen.html',
        controller: 'CreateFenCtrl',
        accessLevel: Roles.USER
      })
  .when('/fen/create/fen/:encodedFen',
      {
        templateUrl: 'partials/create_fen.html',
        controller: 'CreateFenCtrl',
        accessLevel: Roles.USER
      })
  .when('/game/create',
      {
        templateUrl: 'partials/create_game.html',
        controller: 'CreateGameCtrl',
        resolve: UnsavedGameResolve,
        accessLevel: Roles.USER
      })
  .when('/game/create/fen/:encodedFen',
      {
        templateUrl: 'partials/create_game.html',
        controller: 'CreateGameCtrl',
        resolve: UnsavedGameResolve,
        accessLevel: Roles.USER
      })
  .when('/game/id/:gameId',
      {
        templateUrl: 'partials/create_game.html',
        controller: 'CreateGameCtrl',
        resolve: GameResolve
      })
  .when('/search/:params',
      {
        templateUrl: 'partials/search.html',
        controller: 'SearchCtrl'
      })
  .when('/signin',
      {
        templateUrl: 'partials/signin.html',
        controller: 'SigninCtrl'
      })
  .when('/signup',
      {
        templateUrl: 'partials/signup.html',
        controller: 'SignupCtrl'
      })
  .when('/signup/:inviteCode',
      {
        templateUrl: 'partials/signup.html',
        controller: 'SignupCtrl'
      })
  .when('/invite',
      {
        templateUrl: 'partials/invite.html',
        controller: 'InviteCtrl',
        accessLevel: Roles.ADMIN
      })
  .when('/accessdenied',
      {
        templateUrl: 'partials/accessdenied.html',
        controller: 'AccessDeniedCtrl'
      })
  .when('/study',
      {
        templateUrl: 'partials/study.html',
        controller: 'StudyCtrl'
      })
  .when('/study/fen/:encodedFen',
      {
        templateUrl: 'partials/study.html',
        controller: 'StudyCtrl'
      })
  .when('/practice',
      {
        templateUrl: 'partials/practice.html',
        controller: 'ProblemSetCtrl'
      })
  .when('/practice/:problemId',
      {
        templateUrl: 'partials/problem.html',
        controller: 'ProblemCtrl'
      })
  .when('/sandbox',
      {
        templateUrl: 'partials/sandbox.html',
        controller: 'SandboxCtrl'
      })
  .otherwise({redirectTo: '/search/q='});
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

  delete $httpProvider.defaults.headers.common["X-Requested-With"];
}]);

var UnsavedGameResolve = {
  game: function($q) {
    return $q.when(null);
  }
};

var GameResolve = {
  game: function($route, dbService) {
    var gameId = $route.current.params.gameId
    return dbService.getGame(gameId);
  }
};
