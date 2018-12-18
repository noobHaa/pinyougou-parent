app.controller("payController", function ($scope, payService, $location) {
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee / 100).toFixed(2);	//金额
                $scope.out_trade_no = response.out_trade_no;//订单号

                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    size: 250,
                    level: 'H',
                    value: response.code_url == null ? "www.baidu.com" : response.code_url
                });
                queryPayStatus();
            }
        )
    }

    //查询订单
    queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success) {
                    location.href = "paysuccess.html#?money=" + $scope.money;
                } else {
                    if (response.message == '二维码超时') {
                        alert('二维码超时');
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        )
    };

    //获取金额
    $scope.getMoney = function () {
        return $location.search()['money'];
    }
});