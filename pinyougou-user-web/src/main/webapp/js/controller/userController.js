//控制层
app.controller('userController', function ($scope, $controller, userService) {

    $scope.reg = function () {
        if ($scope.entity.password != $scope.password) {
            alert("两次密码输入不一致，请重新输入！");
            return;
        }
        //查询验证码是否正确
        checkSmsCode();
        //验证码是否正确
        if (!$scope.correct) {
            alert("验证码错误，请重新输入！");
        }
        userService.add($scope.entity).success(
            function (response) {
                alert(response.message);
            }
        )
    }
    $scope.correct=false;
    checkSmsCode=function(){
        userService.checkSmsCode($scope.entity.phone ,$scope.smscode).success(
            function (response) {
                $scope.correct=response.success;
                //alert(response.message);
            }
        )
    }

    $scope.sendCode = function () {
        if ($scope.entity.phone == null) {
            alert("请输入手机号！");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        )
    }
});	
