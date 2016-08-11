'use strict';

describe('deckImport', function() {

  // Load the needed modules
  beforeEach(module('txcmaker'));
  beforeEach(module('mock.BackendService'));

  // Initialize the controller with its dependencies
  beforeEach(inject(function($controller, $rootScope, $location, _BackendService_) {
    this.$scope = $rootScope.$new();
    this.$location = $location;
    this.backendService = _BackendService_;

    this.deckImportController = $controller('DeckImportCtrl', {
      $scope: this.$scope,
      BackendService: _BackendService_,
      $location: this.$location
    });
  }));

  it('should be defined', function() {
    expect(this.deckImportController).toBeDefined();
  });

  it('should load the next view when the form is successfully submitted', function() {
    spyOn(this.$location, 'path');

    this.$scope.submitForm();
    this.$scope.$apply();

    var expectedId = this.backendService.responseToPostForm.data.id;
    expect(this.$location.path).toHaveBeenCalledWith('/DeckPreview/' + expectedId);
  });

  it('should display errors when the form is not successfully submitted', function() {
    var response = {
      status: 400,
      data: {
        errors: ['error1', 'error2'],
        warnings: [],
        id: -1
      }
    };
    this.backendService.responseToPostForm = response;

    this.$scope.submitForm();
    this.$scope.$apply();

    expect(this.$scope.errors).toBe(response.data.errors);
  });

  it('should populate the licenseUrl with CC URL when selected', function() {
    this.$scope.licenseOption = 'cc';

    this.$scope.submitForm();

    expect(this.$scope.formData.licenseUrl).toEqual('http://creativecommons.org/licenses/by-nc/4.0/');
  });

  it('should populate the licenseUrl with Other URL when selected', function() {
    var expectedUrl = 'http://someOtherLicenseUrl.com';
    this.$scope.licenseOption = 'other';
    this.$scope.licenseOtherUrl = expectedUrl;

    this.$scope.submitForm();

    expect(this.$scope.formData.licenseUrl).toEqual(expectedUrl);
  });

});
