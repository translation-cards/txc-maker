function HeaderController($scope, $location) {
  $scope.go = function(path) {
    $location.path(path);
  }
}

angular.module('txcmaker').component('deckMakerHeader', {
  transclude:true,
  templateUrl: 'components/DeckMakerHeader/deckMakerHeader.html',
  controller: HeaderController,
  bindings: {
    subtitle: '<'
  }
});
