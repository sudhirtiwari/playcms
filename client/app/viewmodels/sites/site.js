define(['Backbone', 'viewmodels/sites/site_collection'],
function(Backbone, SiteCollection) {
    var Site = (function() {
        return Backbone.RelationalModel.extend({
            relations: [
                {
                    type: Backbone.HasOne,
                    key: parentId,
                    relatedModel: Site,
                    collectionType: SiteCollection,
                    reverseRelation: {
                        key: 'id',
                        includeInJson: false
                    }
                }
            ]
        });
    });

    return Site;
});