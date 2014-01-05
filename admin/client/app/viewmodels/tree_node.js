define(['knockout'],
function(ko) {
    return ko.ViewModel.extend({
        children: ko.observableArray([]),
        expanded: ko.observable(false),
        isLoaded: ko.observable(false),
        isLoading: ko.observable(false),
        expand: function() {
            var self = this;
            if (self.isLoaded() || self.isLoading()) self.expanded(true);
            else self.loadChildren().then(function() {
                self.expanded(true);
            }).done();
        },
        collapse: function() {
            this.expanded(false);
        },
        toggle: function() {
            if (this.expanded()) this.collapse();
            else this.expand();
        },
        refresh: function() {
            this.loadChildren().done();
        }
    });
});
