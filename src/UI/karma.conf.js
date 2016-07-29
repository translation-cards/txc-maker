//jshint strict: false
module.exports = function(config) {
  config.set({

    basePath: './app',

    files: [
      'bower_components/angular/angular.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-mocks/angular-mocks.js',

      'app.js',

      // First, load the module
      'views/DeckImport/DeckImport.ctrl.js',
      'views/DeckImport/DeckImport.spec.js',
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
