
function DeckInfoScreenController($scope) {
  this.$onChanges = function (changesObj) {
    if(changesObj.deck) {
      $scope.deck = changesObj.deck.currentValue;

      $scope.sourceLanguage = [$scope.deck.iso_code];

      $scope.destinationLanguages = [];
      $scope.deck.languages.forEach(function(language){
        $scope.destinationLanguages.push(language.iso_code);
      });
    }
  }
}

angular.module('txcmaker').component('deckInfoScreen', {
  transclude:true,
  templateUrl: 'components/DeckInfoScreen/DeckInfoScreen.html',
  controller: DeckInfoScreenController,
  bindings: {
    deck: '<'
  }
});
