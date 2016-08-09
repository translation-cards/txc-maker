

function LoadingScreenController() {

}

angular.module('txcmaker').component('loadingScreen', {
  transclude:true,
  templateUrl: 'components/LoadingScreen/LoadingScreen.html',
  controller: LoadingScreenController,
  bindings: {
    headline: '<',
    subhead: '<'
  }
});
