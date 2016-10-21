function DeckPublishController($scope, BackendService) {
  var messageHandler = function(response) {
    var data = angular.fromJson(response.data);
    $scope.downloadUrl = data.downloadUrl;
    $scope.deck = data.deck;
    $scope.sourceLanguage = $scope.deck.language_label;

    $scope.destinationLanguages = $scope.deck.languages.map(function(language) {
      return language.languageLabel;
    });

    $scope.$apply();
    BackendService.closeChannel();
  };

  (function fetchDeck() {
    BackendService.updateHandler(messageHandler);
  })();
}

angular
  .module('txcmaker')
  .controller('DeckPublishCtrl', ['$scope', 'BackendService', DeckPublishController]);
