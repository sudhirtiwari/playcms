define(['knockout'],
function(ko) {
    return ko.ViewModel.extend({
        init: function(model, userService) {

        },
        attributes: ['id', 'username', 'firstName', 'lastName', 'email']
    });
});
