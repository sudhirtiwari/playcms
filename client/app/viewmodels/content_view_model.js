define(['knockout', 'knockback', 'viewmodels/sites/site_collection'],
function(ko, kb, SitesCollection) {
    var sites = new SitesCollection()();
    sites.fetch();

    return function() {
        //this.sites = kb.collectionObservable(sites);
        this.activeItem = ko.observable();
    };
});
