define(['underscore', './http', './rest_service'],
function(_, http, restService) {
    var templateRoutes = jsRoutes.controllers.playcms.admin.TemplatesController;

    return _.extend(restService(templateRoutes), {
        uniqueCheck: function(id, name) {
            return http.get(templateRoutes.uniqueCheck(id, name));
        }
    });
});
