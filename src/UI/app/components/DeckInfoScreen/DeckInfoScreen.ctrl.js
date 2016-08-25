
function DeckInfoScreenController($scope, $location, BackendService) {
  this.$onChanges = function (changesObj) {
    if(changesObj.deck && changesObj.deck.currentValue) {
      $scope.deck = changesObj.deck.currentValue;

      $scope.sourceLanguage = $scope.deck.language_label;

      $scope.destinationLanguages = [];
      $scope.deck.languages.forEach(function(language){
        $scope.destinationLanguages.push(language.language_label);
      });
    }
  }

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
