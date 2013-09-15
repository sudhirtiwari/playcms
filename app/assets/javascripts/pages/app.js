var sitesModule = angular.module('sitesApp', []);

sitesModule.controller('SitesController', function($scope, $http) {
    $http.get(document.rootPath + '/sites').success(function(data) {
        $scope.sites = data;
    });
});


