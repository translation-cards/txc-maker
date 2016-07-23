//jshint strict: false
module.exports = function(config) {
  config.set({

    basePath: './app',

    files: [
      'bower_components/angular/angular.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-mocks/angular-mocks.js',

      // First, load the module
      'views/deckImport/deckImport.ctrl.js',
      'views/deckImport/deckImport.spec.js',
      // Then, load dependencies of the module
      'components/helloWorld/helloWorldService.js',
      'components/helloWorld/helloWorldService.mock.js',

      // Lastly, load the app
      'app.js'
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
