angular.module('mock.helloWorldService', [])
  .factory('helloWorldService', function($q) {
    var service = {};

    service.getText = function() {
      var response = {data: "stubbed text"};
      return $q.when(response);
    }

    return service;
  });
