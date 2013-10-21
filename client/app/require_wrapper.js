requirejs.config({
    baseUrl: 'cms/assets/javascripts',
    paths: {
        text: '../components/requirejs-text/text',
        bootstrap: '../components/bootstrap/dist/js/bootstrap',
        jquery: '../components/jquery/jquery',
        underscore: '../components/underscore/underscore',
        knockout: '../components/knockout.js/knockout',
        "knockout.validation": '../components/knockout.validation/index',
        durandal: '../components/durandal/js',
        plugins: '../components/durandal/js/plugins',
        transitions: '..components/durandal/js/transitions'
    },
    shim: {
        underscore: {
            exports: '_'
        },
        jquery: {
            exports: '$'
        },
        bootstrap: {
            deps: ['jquery']
        },
        knockout: {
            exports: 'ko'
        },
        "knockout.validation": {
            deps: ['knockout'],
            exports: 'knockout.validation'
        }
    }
});