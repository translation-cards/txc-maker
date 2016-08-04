

function DeckPreviewController($scope) {

  $scope.deckId = 10;

}

angular.module('txcmaker')

.controller('DeckPreviewCtrl', ['$scope', DeckPreviewController]);
