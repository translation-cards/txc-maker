angular.module('mock.BackendService', [])
  .factory('BackendService', function($q) {
    var service = {};

    service.get = function(url) {
      var response = {data: "stubbed text"};
      return $q.when(response);
    }

    return service;
  });
