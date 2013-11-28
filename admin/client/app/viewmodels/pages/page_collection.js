define(['require','Backbone'],
function(require, Backbone) {
    var PageCollection = null;

    return function() {
        if (PageCollection) {
            PageCollection = Backbone.Collection.extend({
                initialize: function(models, options) {
                    Backbone.Collection.apply(this, arguments);
                    this.siteId = options.siteId;
                },
                url: function(model) {
                    var fetchUrl = document.rootPath + '/pages?siteId=' + this.siteId;
                    if (model && !model.isNew) fetchUrl += '&parentId=' + model.get('id');
                    return fetchUrl;
                },
                model: require('viewmodels/pages/page')
            });
        }

        return PageCollection;
    };
});