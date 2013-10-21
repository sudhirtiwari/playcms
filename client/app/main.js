requirejs.config({
    baseUrl: 'cms/assets/javascripts/app',
    paths: {
        text: 'text',
        bootstrap: 'bootstrap/dist/js/bootstrap',
        jquery: 'jquery/jquery',
        underscore: 'underscore/underscore',
        knockout: 'knockout.js/knockout',
        "knockout.validation": 'knockout.validation/index',
        durandal: 'durandal',
        plugins: 'durandal/plugins',
        transitions: 'durandal/transitions'
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

define(['durandal/system', 'durandal/app', 'durandal/viewLocator', 'bootstrap'], function(system, app, viewLocator, bootstrap) {
    system.debug(true);

    app.title = 'Play! CMS';

    app.configurePlugins({
        router: true,
        dialog: true,
        widget: true
    });

    app.start().then(function() {
        viewLocator.useConvention();
        app.setRoot('viewmodels/shell', 'entrance');
    });
});

