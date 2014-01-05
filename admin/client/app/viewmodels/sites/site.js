define(['jquery', 'underscore', 'knockout', 'q/q', '../tree_node'],
function($, _, ko, Q, TreeNode) {
    return TreeNode.extend({
        init: function(model, siteService, pageService, siteFactory, pageFactory) {
            var self = this;

            self.children = ko.computed(function() {
                var sites = self.sites(),
                    pages = self.pages();

                return sites.concat(pages);
            });

            self.loadChildren = function() {
                var promises = [siteService.children(model.id), pageService.children(model.id)];
                return Q.all(promises).spread(function(sites, pages) {
                    _.each(sites, function(site) {
                        self.sites.push(siteFactory(site));
                    });

                    _.each(pages, function(page) {
                        self.sites.push(pageFactory(page));
                    });
                });
            };

            self.save = function() {
                var promise = model.id ? siteService.update(model) : siteService.create(model);
                promise.done();
            };

            self.deleteSite = function(id) {
                siteService.delete(id).fin(function() {
                    self.removeSite(id);
                });
            };

            self.deletePage = function(id) {
                pageService.delete(id).fin(function() {
                    self.removePage(id);
                });
            };
        },
        attributes: ['id', 'parentId', 'title', 'description', 'name', 'domain'],
        sites: ko.observableArray([]),
        pages: ko.observableArray([]),
        removeSite: function(id) {
            this.sites.remove(function(site) {
                return site.id() === id;
            });
        },
        removePage: function(id) {
            this.pages.remove(function(page) {
                return page.id() === id;
            });
        }
    });
});