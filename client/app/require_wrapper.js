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
        transitions: '../components/durandal/js/transitions',
        knockback: '../components/knockback/knockback',
        backbone: '../components/backbone/backbone',
        "backbone-relational": '../components/backbone-relational/backbone-relational'
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
        },
        backbone: {
            deps: ['jquery'],
            exports: 'Backbone'
        },
        "backbone-relational": {
            deps: ['Backbone']
        }
    }
});