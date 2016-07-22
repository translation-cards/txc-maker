'use strict';

// Declare app level module which depends on views, and components
angular.module('txcmaker', [
  'ngRoute',
  'txcmaker.deckImport',
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  //$locationProvider.hashPrefix('!');

  $routeProvider.otherwise({redirectTo: '/DeckImport'});
}]);