
function DeckInfoScreenController($scope, $location, BackendService) {
  this.$onChanges = function (changesObj) {
    if(changesObj.deck && changesObj.deck.currentValue) {
      $scope.deck = changesObj.deck.currentValue;
      $scope.numberOfErrors = $scope.deck.errors.length;
    }
  };

  $scope.publish = function() {
    BackendService.buildDeck($scope.deck.id).then(
        function(response) {
          $location.path('/DeckPublish')
        }
    );
  }
}

angular.module('txcmaker').component('deckInfoScreen', {
  templateUrl: 'components/DeckInfoScreen/DeckInfoScreen.html',
  controller: DeckInfoScreenController,
  bindings: {
    deck: '<'
  }
});
