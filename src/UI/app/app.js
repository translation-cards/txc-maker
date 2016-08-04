'use strict';

// Declare app level module which depends on views, and components
angular.module('txcmaker', [
  'ngRoute',
  'ngMaterial',
  'angularCSS'
]).
config(['$locationProvider', '$routeProvider', '$mdThemingProvider', function($locationProvider, $routeProvider, $mdThemingProvider) {

  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/DeckImport/DeckImport.html',
    controller: 'DeckImportCtrl'
  })
  .when('/DeckPreview', {
    templateUrl: 'views/DeckPreview/DeckPreview.html',
    controller: 'DeckPreviewCtrl',
    css: 'views/DeckPreview/DeckPreview.css'
  });

  $routeProvider.otherwise({redirectTo: '/DeckImport'});

  $mdThemingProvider.theme('default')
    .primaryPalette('indigo')
    .accentPalette('amber');
}]);
