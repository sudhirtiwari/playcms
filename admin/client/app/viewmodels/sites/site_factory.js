define(['./site', '../pages/page_factory', '../../services/site_service', '../../services/page_service'],
function(Site, pageFactory, siteService, pageService) {
    var fn = function(model) {
        return new Site(model, siteService, pageService, fn, pageFactory);
    };

    return fn;
});