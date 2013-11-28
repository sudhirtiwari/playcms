var allTestFiles = [],
    thisFile = '/base/test/main.js',
    TEST_REGEXP = /test.*\.js$/;

Object.keys(window.__karma__.files).forEach(function(file) {
    if (TEST_REGEXP.test(file) && file != thisFile) {
        allTestFiles.push(file);
    }
});

require.config({
    // Karma serves files under /base, which is the basePath from your config file
    baseUrl: '/base',

    shim: {

    },

    // dynamically load all test files
    deps: allTestFiles,

    // we have to kick of mocha, as it is asynchronous
    callback: window.__karma__.start
});