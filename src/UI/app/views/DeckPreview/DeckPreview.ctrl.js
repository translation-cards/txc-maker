

function DeckPreviewController($scope, BackendService) {

  $scope.deckId = 10;

  BackendService.get('/api/decks/' + $scope.deckId).then(function(response) {
    $scope.deck = response.data.deck;
  });
}

angular.module('txcmaker')

.controller('DeckPreviewCtrl', ['$scope', 'BackendService', DeckPreviewController]);
