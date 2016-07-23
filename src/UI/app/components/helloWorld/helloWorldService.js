angular.module('txcmaker.deckImport')
.service('helloWorldService', function($http, API) {
    this.getText = function () {
        return $http({
        url: 'http://' + API.location + '/helloWorld',
        method: "GET",
        headers: {
          'Content-Type': 'application/json; charset=utf-8'
        }
      });
    }
});
