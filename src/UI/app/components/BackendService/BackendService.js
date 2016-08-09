angular.module('txcmaker')

.factory('BackendService', function($http, $httpParamSerializerJQLike) {
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
      data: $httpParamSerializerJQLike(formData),
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }
  return service;
});
