define(['plugins/router', 'durandal/app'], function(router, app) {
    return {
        router: router,
        activate: function() {
            router.map([
                { route: '', title: 'Pages', moduleId: 'viewmodels/pages', nav: true }
            ]).buildNavigationModel();
            return router.activate();

        }
    };
});
