

function DeckPreviewController($scope, $timeout, $routeParams, BackendService, timeoutDuration, maxFetches) {

  var fetchCount = 1;
  $scope.errorFetchingDeck = false;
  $scope.deckId = $routeParams.id;

  $scope.messageHandler = function(data) {
    console.log(data.data);
    $scope.deck = data.data;
    $scope.$apply();
  };

  (function fetchDeck() {
    BackendService.openChannel($scope.messageHandler);
  })();

}

angular.module('txcmaker')

.controller('DeckPreviewCtrl',
  ['$scope', '$timeout', '$routeParams', 'BackendService', 'timeoutDuration',
  'maxFetches', DeckPreviewController])

.value('timeoutDuration', 1000)
.value('maxFetches', 120);
