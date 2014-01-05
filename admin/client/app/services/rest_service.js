define(['http'],
function(http) {
    return function(routes) {
        return {
            get: function(id) {
                return http.get(routes.get(id));
            },
            create: function(model) {
                return http.post(routes.create(), model);
            },
            update: function(model) {
                return http.put(routes.update(model.id), model);
            },
            delete: function(id) {
                return http.delete(routes.delete(id));
            }
        };
    };
});