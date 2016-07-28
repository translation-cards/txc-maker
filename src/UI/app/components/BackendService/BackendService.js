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
    return service;
  });
