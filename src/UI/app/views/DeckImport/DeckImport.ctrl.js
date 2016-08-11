'use strict';

function DeckImportController ($scope, BackendService, $location){

  $scope.formData = {};
  $scope.errors = [];
  $scope.licenseOption = 'cc';
  $scope.licenseOtherUrl = '';

  $scope.submitForm = function() {
    $scope.formData.licenseUrl = getLicenseUrl($scope);

    BackendService.postForm('/api/decks/', $scope.formData).then(
      function(response) {
        $location.path('/DeckPreview/' + response.data.id);
      },
      function(response) {
        $scope.errors = response.data.errors;
      });
  };

  var getLicenseUrl = function(scope) {
    if ($scope.licenseOption === 'cc') {
        return  'http://creativecommons.org/licenses/by-nc/4.0/';
    } else {
        return $scope.licenseOtherUrl;
    }
  }
}

angular.module('txcmaker')

.controller('DeckImportCtrl', ['$scope', 'BackendService', '$location', DeckImportController]);
