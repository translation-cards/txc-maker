function HeaderController($location) {
  this.go=function(path){
      $location.path(path);
  }
}

angular.module('txcmaker').component('deckMakerHeader', {
  transclude:true,
  templateUrl: 'components/DeckMakerHeader/deckMakerHeader.html',
  controller: HeaderController,
  bindings: {
    deckImportSelected: '<'
  }
});