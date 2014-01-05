define(['jquery', 'knockout', 'underscore'],
function($, ko, _) {
    ko.bindingHandlers['contentArea'] = {
        init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            var key = valueAccessor(),
                vm = bindingContext.$data,
                region = vm[key] || vm.addArea(key),
                el = $(element);

            el.attr("contenteditable", "true").html(region());

            vm[key].subscribe(function(newValue) {
                el.html(newValue);
            });

            el.on("blur input", function() {
                var current = el.html();
                if (current !== region()) region(current);
            });
        }
    };
});
