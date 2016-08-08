'use strict';

function DeckImportController ($scope, BackendService, $location){

  $scope.formData = {};
  $scope.errors = [];

  $scope.submitForm = function() {
    BackendService.postForm('/api/decks/', $scope.formData).then(
      function(response) {
        $location.path('/DeckPreview/' + response.data.id);
      },
      function(response) {
        $scope.errors = response.data.errors;
      });
  };
}

angular.module('txcmaker')

.controller('DeckImportCtrl', ['$scope', 'BackendService', '$location', DeckImportController]);
