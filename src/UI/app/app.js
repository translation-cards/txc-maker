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
    controller: 'DeckImportCtrl',
    css: 'views/DeckImport/DeckImport.css'
  })
  .when('/DeckPreview/:id', {
    templateUrl: 'views/DeckPreview/DeckPreview.html',
    controller: 'DeckPreviewCtrl',
    css: 'views/DeckPreview/DeckPreview.css'
  })
  .when('/DeckPublish', {
    templateUrl: 'views/DeckPublish/DeckPublish.html',
    controller: 'DeckPublishCtrl',
    css: 'views/DeckPublish/DeckPublish.css'
  })
  .when('/FAQ', {
    templateUrl: 'views/FAQ/FAQ.html',
    controller: 'FAQCtrl'
  });

  $routeProvider.otherwise({redirectTo: '/DeckImport'});

  var altBlue = $mdThemingProvider.extendPalette('blue', {
    '800': '#01579B',
    '900': '#00447A'
  });
  // Register the new color palette map with the name <code>neonRed</code>
  $mdThemingProvider.definePalette('altBlue', altBlue);

  $mdThemingProvider.theme('default')
    .primaryPalette('altBlue', {
      'default': '800',
      'hue-1': '900'
    })
    .accentPalette('amber');
}]);
