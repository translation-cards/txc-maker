'use strict';

// Declare app level module which depends on views, and components
angular.module('txcmaker', [
  'ngRoute',
  'txcmaker.deckImport'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  //$locationProvider.hashPrefix('!');

  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/deckImport/deckImport.html',
    controller: 'DeckImportCtrl'
  });

  $routeProvider.otherwise({redirectTo: '/DeckImport'});

  $locationProvider.html5Mode(true);
}])

.run(function () {
    var mdlUpgradeDom = false;
    setInterval(function() {
      if (mdlUpgradeDom) {
        componentHandler.upgradeDom();
        mdlUpgradeDom = false;
      }
    }, 200);

    var observer = new MutationObserver(function () {
      mdlUpgradeDom = true;
    });
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });
    /* support <= IE 10
    angular.element(document).bind('DOMNodeInserted', function(e) {
        mdlUpgradeDom = true;
    });
    */
});
