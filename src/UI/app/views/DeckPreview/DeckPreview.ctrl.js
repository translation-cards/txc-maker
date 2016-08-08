

function DeckPreviewController($scope, $timeout, BackendService) {

  var timeoutDuration = 2000;
  var maxFetches = 30;
  var fetchCount = 1;
  $scope.errorFetchingDeck = false;
  $scope.deckId = 10;

  (function fetchDeck() {
    $scope.promise = BackendService.get('/api/decks/' + $scope.deckId);
    $scope.promise.then(function(response) {
        $scope.deck = response.data.deck;
    },
    function(response) {
      if(fetchCount === maxFetches) {
        errorFetchingDeck = true;
      } else if(response.status === 404) {
        fetchCount++;
        $timeout(fetchDeck, timeoutDuration);
      }
    });
  })();

}

angular.module('txcmaker')

.controller('DeckPreviewCtrl', ['$scope', '$timeout', 'BackendService', DeckPreviewController]);
