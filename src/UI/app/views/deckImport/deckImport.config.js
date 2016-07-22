'use strict';

angular.module('txcmaker.deckImport', ['ngRoute'])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/deckImport/deckImport.html',
    controller: 'DeckImportCtrl'
  });
}]);