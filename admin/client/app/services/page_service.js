define(['underscore', './http', './rest_service'],
function(_, http, restService) {
    var pageRoutes = jsRoutes.controllers.playcms.admin.PagesController;

    return _.extend(restService(pageRoutes), {
        uniqueCheck: function(id, siteId, parentId, relativePath) {
            return http.get(pageRoutes.uniqueCheck(id, siteId, parentId, relativePath));
        },
        children: function(siteId, parentId) {
            return http.get(pageRoutes.children(siteId, parentId));
        }
    });
});