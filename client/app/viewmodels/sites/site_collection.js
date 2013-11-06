define(['require', 'Backbone'],
function(require, Backbone) {
    var SiteCollection = null;

    return function() {
        if (!SiteCollection) {
            SiteCollection = Backbone.Collection.extend({
                url: function(model) {
                    var fetchUrl = document.rootPath + '/sites';
                    if (model && !model.isNew()) fetchUrl += '?parentId=' + model.get('id');
                    return fetchUrl;
                },
                model: require('viewmodels/sites/site')
            });
        }

        return SiteCollection;
    };
});