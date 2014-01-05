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
        transitions: 'durandal/transitions',
        q: 'q/q'
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

define(['durandal/system', 'durandal/app', 'durandal/viewLocator', 'q/q', 'bootstrap', 'viewmodels/ko_model'],
function(system, app, viewLocator, Q) {
    system.debug(true);
    system.defer = function(action) {
        var deferred = Q.defer();
        action.call(deferred, deferred);
        var promise = deferred.promise;
        deferred.promise = function() {
            return promise;
        };
        return deferred;
    };

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

