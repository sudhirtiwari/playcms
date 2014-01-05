define(['knockout', 'underscore'],
function(ko, _) {
    var update = function(model, key, value) {
        if (_.isObject(model) && _.isString(key)) {
            if (_.has(model, key) && _.isFunction(model[key])) model[key](value);
            else model[key] = value;
        }
    };

    var read = function(model, key) {
        if (_.isObject(model) && _.isString(key) && _.has(model, key)) {
            if (_.isFunction(model[key])) return model[key]();
            else return model[key];
        }
        return null;
    };

    var get = function(model, path) {
        var step = function(model, path) {
            var head = _.head(path);
            var tail = _.tail(path);

            if (head && tail) return step(model[head], tail);
            else if (head) return read(model, head);

            return null;
        };

        return step(model, path);
    };

    var set = function(model, path, value) {
        var step = function (model, path) {
            var head = _.head(path),
                tail = _.tail(path);

            if (head && tail) {
                step(model[head], tail);
            } else if (head) {
                update(model, head, value);
            }
        };

        step(model, path);
    };

    var mapped = function(observableFn) {
        return function(model, path) {
            var keys = path.split("."),
                observable = observableFn(get(model, keys));

            observable.subscribe(function(value) {
                set(model, keys, value);
            });

            return observable;
        };
    };

    ko.mapped = mapped(ko.observable);
    ko.mappedArray = mapped(ko.observableArray);

    var ViewModel = ko.ViewModel = function(model) {
        model = model || {};
        var self = this;
        _.each(self.attributes, function(attr) {
            if (_.isArray(get(model, attr.split(".")))) {
                self[attr] = ko.mappedArray(model, attr);
            } else {
                self[attr] = ko.mapped(model, attr);
            }
        });
        self.init.apply(self, arguments);
    };

    _.extend(ViewModel.prototype, {
        attributes: [],
        updateFromModel: function(model) {
            var self = this;
            _.each(self.attributes, function(attr) {
                self[attr](model[attr]);
            });
        },
        init: function() {

        }
    });

    ViewModel.extend = function(protoProps, staticProps) {
        var parent = this;
        var child;

        // The constructor function for the new subclass is either defined by you
        // (the "constructor" property in your `extend` definition), or defaulted
        // by us to simply call the parent's constructor.
        if (protoProps && _.has(protoProps, 'constructor')) {
            child = protoProps.constructor;
        } else {
            child = function(){ return parent.apply(this, arguments); };
        }

        // Add static properties to the constructor function, if supplied.
        _.extend(child, parent, staticProps);

        // Set the prototype chain to inherit from `parent`, without calling
        // `parent`'s constructor function.
        var Surrogate = function(){ this.constructor = child; };
        Surrogate.prototype = parent.prototype;
        child.prototype = new Surrogate;

        // Add prototype properties (instance properties) to the subclass,
        // if supplied.
        if (protoProps) _.extend(child.prototype, protoProps);

        // Set a convenience property in case the parent's prototype is needed
        // later.
        child.__super__ = parent.prototype;

        return child;
    };
});