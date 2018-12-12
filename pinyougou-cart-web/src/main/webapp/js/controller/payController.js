app.controller("paysController", function ($scope, payService) {
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee / 100).toFixed(2);	//金额
                $scope.out_trade_no = response.out_trade_no;//订单号

                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    size: 250,
                    level: 'H',
                    value: response.code_url==null?"www.baidu.com":response.code_url
                });
            }
        )
    }
});