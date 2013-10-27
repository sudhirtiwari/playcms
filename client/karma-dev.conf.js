var sharedConfig = require('./karma-shared.conf');

module.exports = function(config) {
    sharedConfig(config);

    config.set({

        plugins: [
            'karma-jasmine',
            'karma-phantomjs-launcher',
            'karma-html2js-preprocessor',
            'karma-requirejs',
            'karma-growl-reporter'
        ],

        // test results reporter to use
        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['dots', 'growl'],

        background: true
    });
};