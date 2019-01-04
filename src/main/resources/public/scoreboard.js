angular.module('bwsc', [])
.controller('Scoreboard', function($scope, $http, $interval) {
    $interval(function () {
        $http.get('/data').
            then(function(response) {
                $scope.scoreboard = response.data;
                $scope.scoreboard.background = $scope.scoreboard.result ? "green" : "blue";
        });
      }, 200);
});