
function TranslationsScreenController($scope) {
  this.$onChanges = function (changesObj) {
    if(changesObj.language && changesObj.language.currentValue) {
      $scope.language = changesObj.language.currentValue;
    }
  }
}

angular.module('txcmaker').component('translationsScreen', {
  transclude:true,
  templateUrl: 'components/TranslationsScreen/TranslationsScreen.html',
  controller: TranslationsScreenController,
  bindings: {
    language: '<'
  }
});
