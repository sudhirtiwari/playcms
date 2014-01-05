define(['./page', '../../services/page_service'],
function(Page, pageService) {
    var fn = function(model) {
        return new Page(model, pageService, fn);
    };
});
