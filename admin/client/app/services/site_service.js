define(['underscore', './http', './rest_service'],
function(_, http, restService) {
    var siteRoutes = jsRoutes.controllers.playcms.admin.SitesController;

    return _.extend(restService(siteRoutes), {
        uniqueCheck: function(id, domain) {
            return http.get(siteRoutes.uniqueCheck(id, domain));
        },
        children: function(parentId) {
            return http.get(siterotues.children(parentId));
        }
    });
});