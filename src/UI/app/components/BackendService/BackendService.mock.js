angular.module('mock.BackendService', [])
  .factory('BackendService', function($q) {
    var service = {
      get: function(url) {
        var deferred = $q.defer();
        this.requestCount++;

        if (this.responseToGet.status === 200) {
          deferred.resolve(this.responseToGet);
        } else {
          deferred.reject(this.responseToGet);
        }
        return deferred.promise;
      },
      responseToGet: {
        status: 200,
        data: {
          deck: {
            deck_label: "stubbed label"
          }
        }
      },
      requestCount: 0,

      postForm: function(url, formData) {
        var deferred = $q.defer();
        this.requestCount++;

        if(this.responseToPostForm.status === 200) {
          deferred.resolve(this.responseToPostForm)
        } else {
          deferred.reject(this.responseToPostForm)
        }

        return deferred.promise;
      },
      responseToPostForm: {
        status: 200,
        data: {
          errors: [],
          id: 10,
          warnings: []
        }
      },

      openChannel: function(messageHandler) {
        this.openChannelCalled = true;
        messageHandler({
          data: {
            deck_label: "stubbed label"
          }
        });
      },
      openChannelCalled: false
    };



    return service;
  });
