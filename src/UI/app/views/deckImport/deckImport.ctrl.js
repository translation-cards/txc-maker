'use strict';

function DeckImportController ($scope, helloWorldService){
  var text = helloWorldService.getText();
  text.then(function (response) {
        console.log(response);
        $scope.myWelcome = response.data;
    });
}
angular.module('txcmaker.deckImport', ['ngRoute'])

.controller('DeckImportCtrl', ["$scope", "helloWorldService",DeckImportController])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/DeckImport', {
    templateUrl: 'views/deckImport/deckImport.html',
    controller: 'DeckImportCtrl'
  });
}]);;
