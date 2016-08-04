'use strict';

describe('DeckPreviewController', function() {

  var backendService, deckPreviewController, $timeout, $scope;

  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  beforeEach(inject(function($controller, _$timeout_, $rootScope, _BackendService_) {
    $timeout = _$timeout_;
    backendService = _BackendService_;
    $scope = $rootScope;

    backendService.response = {
      status: 404,
      data: {
        deck: {
          deck_label: "error label"
        }
      }
    };


    deckPreviewController = $controller('DeckPreviewCtrl', {
      $scope: $rootScope,
      BackendService: backendService,
      $timeout: $timeout
    })
  }));

  beforeEach(function(done) {
    // $scope.$apply();
    done();
  });

  it('should get a deck', function() {
    expect($scope.deck).toBeDefined();
  });

  it('should retry to get a deck when it isn\'t found', function(done) {


    $scope.promise.then(function(response){
      expect($scope.deck).toBeUndefined();
      expect(response.status).toBe(404);
      done(); //$scope.apply()?
    });

    $timeout.flush();



  })




});
