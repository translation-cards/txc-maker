angular.module('txcmaker.deckImport')
.service('helloWorldService', function($http) {
    this.getText = function () {
        return $http({
        url: 'http://127.0.0.1:8080/helloWorld',
        method: "GET",
        headers: {
                    'Content-Type': 'application/json; charset=utf-8'
        }
    });
    }
});