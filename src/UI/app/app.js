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

  var customBlue = $mdThemingProvider.extendPalette('blue', {
    '800': '#01579B',
    '900': '#00447A'
  });
  $mdThemingProvider.definePalette('customBlue', customBlue);

  var customAccent = $mdThemingProvider.extendPalette('amber', {
    'A100': '#ffffff'
  });
  $mdThemingProvider.definePalette('customAccent', customAccent);

  $mdThemingProvider.theme('default')
    .primaryPalette('customBlue', {
      'default': '800',
      'hue-1': '900'
    })
    .accentPalette('customAccent');
}]);
