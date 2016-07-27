angular.module('txcmaker.deckImport')
.service('helloWorldService', function($http) {
    this.getText = function () {
        return $http({
        url: '/helloWorld',
        method: "GET",
        headers: {
          'Content-Type': 'application/json; charset=utf-8'
        }
      });
    }
});
