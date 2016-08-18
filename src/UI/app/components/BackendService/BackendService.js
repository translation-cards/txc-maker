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
  };

  service.postForm = function(url, formData) {
    return $http({
      url: url,
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'application/json'
      }
    })
  };

  service.channelToken = "";

  service.openChannel = function(messageHandler) {
    var channel = new goog.appengine.Channel(service.channelToken);
    var socket = channel.open();
    socket.onmessage = messageHandler;
  };

  service.buildDeck = function(deckId) {
    return $http({
      url: 'tasks/txc-build',
      method: 'POST',
      data: deckId,
      headers: {
        'Content-Type': 'text/plain'
      }
    });
  }

  return service;
});
