module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ng-constant');

    grunt.initConfig({
      ngconstant: {
        // Options for all targets
        options: {
          space: '  ',
          wrap: '"use strict";\n\n {\%= __ngModule %}',
          name: 'apiConfig',
        },
        // Environment targets
        development: {
          options: {
            dest: 'app/apiConfig.js'
          },
          constants: {
            API: {
              name: 'development',
              location: process.env.API_LOCATION || 'localhost:8080'
            }
          }
        }
      }
    });
};
