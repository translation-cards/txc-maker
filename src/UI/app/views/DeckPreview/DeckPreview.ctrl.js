

function DeckPreviewController($scope, BackendService) {

  BackendService.get('/api/decks/10').then(function(response) {
    $scope.deck = response.data.deck;

    $scope.sourceLanguage = [$scope.deck.iso_code];
    
    $scope.destinationLanguages = [];
    $scope.deck.languages.forEach(function(language){
      $scope.destinationLanguages.push(language.iso_code);
    });
  });

}

angular.module('txcmaker')

.controller('DeckPreviewCtrl', ['$scope', 'BackendService', DeckPreviewController]);
