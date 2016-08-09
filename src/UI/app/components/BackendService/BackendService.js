angular.module('txcmaker')

.factory('BackendService', function($http) {
  var service = {};

  service.get = function (url) {
    return $http({
      url: url,
      method: "GET",
      headers: {
        'Content-Type': 'application/json; charset=utf-8'
      }
    });
  }

  service.postForm = function(url, formData) {
    return $http({
      url: url,
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'application/json'
      }
    })
  }
  return service;
});
