//jshint strict: false
module.exports = function(config) {
  config.set({

    basePath: './app',

    files: [
      'bower_components/angular/angular.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-mocks/angular-mocks.js',
      'bower_components/angular-animate/angular-animate.min.js',
      'bower_components/angular-aria/angular-aria.min.js',
      'bower_components/angular-messages/angular-messages.min.js',
      'bower_components/angular-material/angular-material.min.js',

      'app.mock.js',

      // First, load the module
      'views/DeckImport/DeckImport.ctrl.js',
      'views/DeckImport/DeckImport.spec.js',
      'views/DeckPreview/DeckPreview.ctrl.js',
      'views/DeckPreview/DeckPreview.spec.js',
      'views/DeckPublish/DeckPublish.ctrl.js',
      'views/DeckPublish/DeckPublish.spec.js',
      // Then, load dependencies of the module
      'components/BackendService/BackendService.js',
      'components/BackendService/BackendService.mock.js',
    ],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: [
      'karma-chrome-launcher',
      'karma-firefox-launcher',
      'karma-phantomjs-launcher',
      'karma-jasmine',
      'karma-junit-reporter'
    ],

    junitReporter: {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    }

  });
};
