app.controller("successController", function ($scope, $controller, $interval) {
    $controller('baseController', {$scope: $scope});//继承
    $scope.seconds = 3;
    $scope.redirectLog = function () {
        debugger;
        $interval(function () {
            debugger;
            if ($scope.seconds > 0) {
                $scope.seconds--;
            } else {
                location.href = "shoplogin.html";
            }
        }, 1000);
    }
});