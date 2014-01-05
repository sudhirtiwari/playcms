define(['jquery', 'underscore', 'knockout', 'q/q', '../tree_node'],
function($, _, ko, Q, TreeNode) {
    var ContentAreas = ko.ViewModel.extend({
        init: function(model) {
            var self = this;
            self.addArea = function(name, content) {
                content = content || "";
                model[name] = content;
                self[name] = ko.mapped(model, name);
                return self[name];
            };
        }
    });

    return TreeNode.extend({
        init: function(model, pageService, pageFactory) {
            var self = this;
            model.contentAreas = model.contentAreas || {};
            self.contentAreas = new ContentAreas(model.contentAreas);

            self.loadChildren = function() {
                self.isLoading(true);
                return pageService.getChildren(self.id(), self['siteId'](), self['parentId'](), self['relativePath']()).then(
                    function(pages) {
                        self.children.removeAll();
                        _.each(pages, self.addChild);
                        self.isLoading(false);
                        self.isLoaded(true);
                    }
                );
            };

            self.save = function() {
                var promise = model.id ? pageService.update(model) : pageService.create(model);
                promise.done();
            };

            self.addChild = function(child) {
                self.children.push(pageFactory(child));
            };

            self.deleteChild = function(child) {
                var id = child.id();
                pageService.delete(id).fin(function() {
                    self.removeChild(id);
                });
            };

            self.findInTree = function(filter) {
                if (filter(self))
                    return self;

                var promises = self.children().map(function(child) {
                    return Q.fcall(child.findInTree, filter);
                });

                return Q.all(promises).then(function(values) {
                    return _.head(_.compact(values)) || null;
                });
            };
        },
        attributes: ['id', 'siteId', 'parentId', 'templateId', 'relativePath', 'fullPath'],
        removeChild: function(id) {
            this.children.remove(function(child) {
                return child.id() === id;
            });
        }
    });
});