define(['./template'],
function(Template) {
    return function(model) {
        return new Template(model);
    };
});
