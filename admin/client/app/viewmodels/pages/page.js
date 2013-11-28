define(['Backbone', 'viewmodels/pages/page_collection'],
function(Backbone, PageCollection) {
    var Page = (function() {
        Backbone.RelationalModel.extend({
            urlRoot: document.rootPath + '/pages/',
            relativePath: '',
            contentAreas: {},
            isDeleted: false,
            relations: [
                {
                    type: Backbone.HasOne,
                    key: 'parentId',
                    relatedModel: Page,
                    collectionType: PageCollection,
                    reverseRelation: {
                        key: 'id',
                        includeInJson: false
                    }
                }
            ]
        });
    })();

    return Page;
});