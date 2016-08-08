'use strict';

describe('DeckPreviewController', function() {

  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  beforeEach(inject(function(_$controller_, _$timeout_, $rootScope,
      _BackendService_, _timeoutDuration_, _maxFetches_) {
    this.$timeout = _$timeout_;
    this.backendService = _BackendService_;
    this.$scope = $rootScope;
    this.$controller = _$controller_;
    this.timeoutDuration = _timeoutDuration_;
    this.maxFetches = _maxFetches_;

    this.deckPreviewController = this.$controller('DeckPreviewCtrl', {
      $scope: $rootScope,
      BackendService: this.backendService,
      $timeout: this.$timeout,
      $routeParams: {id: 10}
    });
  }));

  it('should get a deck', function() {
    this.$scope.$apply();
    expect(this.$scope.deck).toBeDefined();
    expect(this.$scope.deck.deck_label).toBe("stubbed label");
  });

  it('should retry to get a deck when it isn\'t found', function(done) {
    this.backendService.response = {
      status: 404,
      data: {
        deck: {
          deck_label: "error label"
        }
      }
    };
    this.backendService.requestCount = 0;

    this.deckPreviewController = this.$controller('DeckPreviewCtrl', {
      $scope: this.$scope,
      BackendService: this.backendService,
      $timeout: this.$timeout,
      $routeParams: {id: 10}
    });

    for(var i = 0; i < this.maxFetches; i++) {
      this.$timeout.flush();
      expect(this.backendService.requestCount).toBe(i+1);
    }

    expect(this.$scope.errorFetchingDeck).toBe(true);

    done();
  });

});
