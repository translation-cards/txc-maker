angular.module('mock.BackendService', [])
  .factory('BackendService', function($q) {
    var service = {
      get: function(url) {
        var deferred = $q.defer();
        this.requestCount++;

        if (this.response.status === 200) {
          deferred.resolve(this.response);
        } else {
          deferred.reject({
            reason:'status is not 200',
            status: this.response.status
          });
        }
        return deferred.promise;
      },
      response: {
        status: 200,
        data: {
          deck: {
            deck_label: "stubbed label"
          }
        }
      },
      requestCount: 0
    };

    return service;
  });
