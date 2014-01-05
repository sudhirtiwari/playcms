define(['jquery', 'q'],
function($, Q, _) {
    var req = function(method, url, data, more) {
        more = more || {};

        var config = _.extend({
            type: method,
            data: data || ''
        }, more);

        return Q.when($.ajax(url, config));
    };

    var contentType = function(data) {
      return data ? { contentType: 'text/json' } : null;
    };

    return {
        get: _.partial(req, 'GET'),
        delete: _.partial(req, 'DELETE'),
        post: function(url, data) {
            return req('POST', url, data, contentType(data));
        },
        put: function(url, data) {
            return req('PUT', url, data, contentType(data));
        }
    };
});