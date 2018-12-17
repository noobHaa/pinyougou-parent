app.controller("seckillGoodsController", function ($scope, seckillGoodsService, $location, $interval) {
    $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.list = response;
            }
        )
    };

    $scope.findOne = function () {
        var id = $location.search()['id'];
        seckillGoodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                allsecond = Math.floor((new Date(response.endTime) - new Date()) / 1000);

                //计算活动时间
                time = $interval(
                    function () {
                        allsecond = allsecond - 1;
                        if (allsecond > 0) {
                            $scope.timeString = convertTimeString(allsecond);
                        } else {
                            $interval.cancel(time);
                            alert("秒杀服务已结束");
                        }
                    }, 1000
                );
            }
        )
    };

    convertTimeString = function (allsecond) {
        var days = Math.floor(allsecond / (60 * 60 * 24));//天数
        var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));//小时
        var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60);//分钟
        var seconds = Math.floor(allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60);//秒
        var timeString = "";
        if (days > 0) {
            timeString = days + "天 ";
        }
        return timeString + hours + ":" + minutes + ":" + seconds;
    }

    //提交订单
    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if (response.success) {
                    alert("抢购成功，请在5分钟内完成支付");
                    location.href = "pay.html";
                } else {
                    alert(response.message);
                }
            }
        )
    }
})
