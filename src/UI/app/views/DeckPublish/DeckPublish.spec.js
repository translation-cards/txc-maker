describe('DeckPublishController', function() {

  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  beforeEach(inject(function(_$controller_, _$timeout_, $rootScope, _BackendService_) {
    this.$timeout = _$timeout_;
    this.backendService = _BackendService_;
    this.$scope = $rootScope;
    this.$controller = _$controller_;

    this.deckPreviewController = this.$controller('DeckPublishCtrl', {
      $scope: $rootScope,
      BackendService: this.backendService,
      $timeout: this.$timeout
    });
  }));

  it('should close the channel after publishing', function() {
    expect(this.backendService.closeChannelCalled).toBe(true);
  });

});