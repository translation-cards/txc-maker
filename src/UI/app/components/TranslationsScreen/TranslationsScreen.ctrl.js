
function TranslationsScreenController($scope, $sce) {
  this.$onChanges = function (changesObj) {
    if(changesObj.data && changesObj.data.currentValue) {
      var data = changesObj.data.currentValue;
      $scope.language = data.language;
      $scope.deckId = data.deckId;
    }
  }

  $scope.playAudio = function(path) {
    var audio = new Audio('/api/decks/' + $scope.deckId + '/' + path);
    audio.play();
  }
}

angular.module('txcmaker').component('translationsScreen', {
  transclude:true,
  templateUrl: 'components/TranslationsScreen/TranslationsScreen.html',
  controller: TranslationsScreenController,
  bindings: {
    data: '<'
  }
});
