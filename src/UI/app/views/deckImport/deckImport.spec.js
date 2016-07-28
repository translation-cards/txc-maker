'use strict';

describe('txcmaker.deckImport', function() {

  describe('DeckImportCtrl', function(){

    var controller, scope, service;

    // Load the needed modules
    beforeEach(module('txcmaker.deckImport'));
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

    it('should get some text', function() {
      expect(scope.myWelcome).toBe("stubbed text");
    })

  });
});
