
function DeckInfoScreenController($scope, BackendService) {
  this.$onChanges = function (changesObj) {
    if(changesObj.deck && changesObj.deck.currentValue) {
      $scope.deck = changesObj.deck.currentValue;

      $scope.sourceLanguage = $scope.deck.language_label;

      $scope.destinationLanguages = [];
      $scope.deck.languages.forEach(function(language){
        $scope.destinationLanguages.push(language.language_label);
      });
    }

    if(changesObj.deckId && changesObj.deckId.currentValue) {
      $scope.deckId = changesObj.deckId.currentValue;
    }
  }

  $scope.publish = function() {
    console.log('hi');
    console.log($scope.deck);
    console.log($scope.deckId);
    BackendService.buildDeck($scope.deckId).then(
        function(response) {
          console.log(response);
        }
    );
  }
}

angular.module('txcmaker').component('deckInfoScreen', {
  templateUrl: 'components/DeckInfoScreen/DeckInfoScreen.html',
  controller: DeckInfoScreenController,
  bindings: {
    deckId: '<',
    deck: '<'
  }
});
