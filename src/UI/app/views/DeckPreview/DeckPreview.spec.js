'use strict';

describe('DeckPreviewController', function() {

  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  var initializeController = function($controller, $scope, backendService) {
    return $controller('DeckPreviewCtrl', {
      $scope: $scope,
      BackendService: backendService,
      $routeParams: {id: 10}
    });
  }

  beforeEach(inject(function(_$controller_, $rootScope, _BackendService_) {
    this.backendService = _BackendService_;
    this.$scope = $rootScope;
    this.$controller = _$controller_;

    this.deckPreviewController = initializeController(
      this.$controller,
      this.$scope,
      this.backendService
    );
  }));

  it('should open a channel with the server', function() {
    expect(this.backendService.openChannelCalled).toBe(true);
  });

  it('should leave the channel open if there are no errors', function() {
    expect(this.backendService.closeChannelCalled).toBe(false);
  });

  it('should close the channel if there are errors that prevent publishing', function() {
    this.backendService.deckStub = {
      deck_label: 'deck with errors',
      numberOfErrors: 2
    };

    this.deckPreviewController = initializeController(
      this.$controller,
      this.$scope,
      this.backendService
    );

    expect(this.backendService.closeChannelCalled).toBe(true);
  });

  it('should get a deck from the channel', function() {
    expect(this.$scope.deck).toBeDefined();
    expect(this.$scope.deck.deck_label).toBe("stubbed label");
  });
});
