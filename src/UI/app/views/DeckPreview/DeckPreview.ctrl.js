
function DeckPreviewController($scope, $routeParams, BackendService) {
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
  ['$scope', '$routeParams', 'BackendService', DeckPreviewController]);
