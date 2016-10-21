function extractCardsForLanguage(translations, language) {
  return translations.map(function(translation) {
    return translation.cards.find(function(card) {
      return card.destinationLanguage.language_label === language;
    });
  }).filter(function removeUndefinedCards(card) {
    return !(card === undefined);
  });
}

function TranslationsScreenController($scope, $sce) {
  $scope.audioFiles = [];

  this.$onChanges = function (changesObj) {
    if(changesObj.data && changesObj.data.currentValue) {
      var data = changesObj.data.currentValue;
      $scope.language = data.language;
      $scope.translations = data.deck.translations;
      $scope.cards_for_language = extractCardsForLanguage(data.deck.translations, data.language);
      $scope.deckId = data.deckId;
    }
  };

  $scope.playAudio = function(fileName) {
    var found = false;
    $scope.audioFiles.some(function(audioFile) {
      if(audioFile.fileName === fileName) {
        audioFile.audio.play();
        found = true;
        return true;
      }
    });

    if(!found) {
      var audio = new Audio('/api/decks/' + $scope.deckId + '/' + fileName);
      $scope.audioFiles.push({
        fileName: fileName,
        audio: audio
      });
      audio.play();
    }
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
