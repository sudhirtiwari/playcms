define(['jQuery'],
function($) {
    return {
        isNullEmptyOrUndefined: function(str) {
            typeof(str) === 'undefined' || str === null || $.trim(str) === '';
        }
    };
});