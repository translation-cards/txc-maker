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
  })
  .when('/FAQ', {
    templateUrl: 'views/FAQ/FAQ.html',
    controller: 'FAQCtrl'
  });

  $routeProvider.otherwise({redirectTo: '/DeckImport'});

  $mdThemingProvider.theme('default')
    .primaryPalette('blue', {
      'default': '800',
      'hue-1': '900'
    })
    .accentPalette('amber');
}]);
