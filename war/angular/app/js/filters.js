'use strict';

/* Filters */

var tkFilters = angular.module('tkApp.filters', ['tkApp.services']);


tkFilters.filter('bookMatch', function(vnService) {
  return function(books, value, opt_limit) {
    var limit = opt_limit || 8;
    var output = [];
    var normalizedValue = vnService.removeAnnotation(value).toLowerCase();
    for (var i = 0; i < books.length; i++) {
      if (books[i].normalizedName.indexOf(normalizedValue) >= 0) {
        output.push(books[i]);
        if (output.length >= limit) {
          break;
        }
      }
    }
    return output;
  };
});
