'use strict';

describe('DeckPreviewController', function() {

  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  beforeEach(inject(function(_$controller_, _$timeout_, $rootScope, _BackendService_) {
    this.$timeout = _$timeout_;
    this.backendService = _BackendService_;
    this.$scope = $rootScope;
    this.$controller = _$controller_;

    this.deckPreviewController = this.$controller('DeckPreviewCtrl', {
      $scope: $rootScope,
      BackendService: this.backendService,
      $timeout: this.$timeout,
      $routeParams: {id: 10}
    });
  }));

  it('should open a channel with the server', function() {
    expect(this.backendService.openChannelCalled).toBe(true);
  })

  it('should get a deck from the channel', function() {
    expect(this.$scope.deck).toBeDefined();
    expect(this.$scope.deck.deck_label).toBe("stubbed label");
  });
});
