'use strict';

// Declare app level module which depends on views, and components
angular.module('txcmaker', [
  'ngRoute',
  'ngMaterial'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  //$locationProvider.hashPrefix('!');

  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/DeckImport/DeckImport.html',
    controller: 'DeckImportCtrl'
  })
  .when('/DeckPreview', {
    templateUrl: 'views/DeckPreview/DeckPreview.html',
    controller: 'DeckPreviewCtrl'
  });

  $routeProvider.otherwise({redirectTo: '/DeckImport'});
}]);

