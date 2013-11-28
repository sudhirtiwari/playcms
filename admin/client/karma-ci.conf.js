var sharedConfig = require('./karma-shared.conf.js');

module.exports = function(config) {
    sharedConfig(config);

    config.set({

        plugins: [
            'karma-mocha',
            'karma-expect',
            'karma-phantomjs-launcher',
            'karma-html2js-preprocessor',
            'karma-requirejs',
            'karma-junit-reporter'
        ],

        // test results reporter to use
        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['junit'],

        junitReporter: {
            outputFile: 'test_reports/test-results.xml',
            suite: ''
        }
    });
};