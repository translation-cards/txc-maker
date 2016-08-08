

function DeckPreviewController($scope, $timeout, BackendService, timeoutDuration, maxFetches) {

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
        $scope.errorFetchingDeck = true;
      } else if(response.status === 404) {
        fetchCount++;
        $timeout(fetchDeck, timeoutDuration);
      }
    });
  })();

}

angular.module('txcmaker')

.controller('DeckPreviewCtrl',
  ['$scope', '$timeout', 'BackendService', 'timeoutDuration', 'maxFetches', DeckPreviewController])

.value('timeoutDuration', 1000)
.value('maxFetches', 120);
