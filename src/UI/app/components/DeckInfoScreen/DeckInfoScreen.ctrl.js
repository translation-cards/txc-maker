
function DeckInfoScreenController($scope, BackendService) {
  BackendService.get('/api/decks/' + this.deckId).then(function(response) {
    $scope.deck = response.data.deck;
    console.log($scope.deck);

    $scope.sourceLanguage = [$scope.deck.iso_code];

    $scope.destinationLanguages = [];
    $scope.deck.languages.forEach(function(language){
      $scope.destinationLanguages.push(language.iso_code);
    });
  });
}

angular.module('txcmaker').component('deckInfoScreen', {
  transclude:true,
  templateUrl: 'components/DeckInfoScreen/DeckInfoScreen.html',
  controller: DeckInfoScreenController,
  bindings: {
    deckId: '<'
  }
});
