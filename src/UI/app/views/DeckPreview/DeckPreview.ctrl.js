
function DeckPreviewController($scope, $timeout, $routeParams, BackendService) {
  $scope.deckId = $routeParams.id;

  var messageHandler = function(data) {
    $scope.deck = angular.fromJson(data.data);
    $scope.$apply();
  };

  (function fetchDeck() {
    BackendService.openChannel(messageHandler);
  })();
}

angular.module('txcmaker')

.controller('DeckPreviewCtrl',
  ['$scope', '$timeout', '$routeParams', 'BackendService', DeckPreviewController])

.value('timeoutDuration', 1000)
.value('maxFetches', 120);
