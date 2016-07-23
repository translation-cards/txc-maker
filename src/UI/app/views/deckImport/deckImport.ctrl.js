'use strict';
function DeckImportController (helloWorldService){
  helloWorldService.getText().then(function (response) {
        console.log(response);
        $scope.myWelcome = response.data;
    });
}
angular.module('txcmaker.deckImport', ['ngRoute'])

.controller('DeckImportCtrl', ["helloWorldService",DeckImportController])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/deckImport/deckImport.html',
    controller: 'DeckImportCtrl'
  });
}]);;
