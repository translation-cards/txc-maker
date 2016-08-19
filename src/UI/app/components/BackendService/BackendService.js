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
    service.socket = channel.open();
    service.socket.onmessage = messageHandler;
  };

  service.updateHandler = function(messageHandler) {
    service.socket.onmessage = messageHandler;
  };

  service.buildDeck = function(deckId) {
    return $http({
      url: '/api/decks/' + deckId,
      method: 'POST'
    });
  };

  return service;
});
