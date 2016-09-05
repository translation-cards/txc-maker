
function DeckPreviewController($scope, $routeParams, BackendService) {
  $scope.deckId = $routeParams.id;

  var messageHandler = function(data) {
    $scope.deck = angular.fromJson(data.data);
    $scope.$apply();
    if($scope.deck.numberOfErrors > 0) {
      BackendService.closeChannel();
    }
  };

  (function fetchDeck() {
    BackendService.openChannel(messageHandler);
  })();
}

angular.module('txcmaker')

.controller('DeckPreviewCtrl',
  ['$scope', '$routeParams', 'BackendService', DeckPreviewController]);
