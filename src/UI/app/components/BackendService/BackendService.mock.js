angular.module('mock.BackendService', [])
  .factory('BackendService', function($q) {
    var service = {};

    service.response = {
      status: 200,
      data: {
        deck: {
          deck_label: "stubbed label"
        }
      }
    }

    service.get = function(url) {
      return $q.when(service.response);
    }

    return service;
  });
