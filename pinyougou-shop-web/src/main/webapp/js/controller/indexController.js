app.controller("indexController", function ($scope, loginService) {

    $scope.showLoginName = function () {
        loginService.loginName().success(
            function (response) {
                debugger;
                $scope.loginName = response.loginName;
            }
        )
    };
})