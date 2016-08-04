'use strict';

describe('deckImport', function() {
  var controller, scope, service;

  // Load the needed modules
  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  // Initialize the controller with its dependencies
  beforeEach(inject(function($controller, $rootScope, _BackendService_) {
    scope = $rootScope.$new();
    service = _BackendService_;

    controller = $controller('DeckImportCtrl', {
      $scope: scope,
      BackendService: _BackendService_
    });
  }));

  // Resolve mocked promises
  beforeEach(function() {
    scope.$apply();
  })

  it('should be defined', function() {
    expect(controller).toBeDefined();
  });
});
