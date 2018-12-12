app.controller("cartController", function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        )
    };

    //添加到购物车
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    //查询出收货地址
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                //设置默认地址
                if ($scope.addressList.length > 0) {
                    for (var i = 0; i < $scope.addressList.length; i++) {
                        if ($scope.addressList[i].isDefault == '1') {
                            $scope.address = $scope.addressList[i];
                            break;
                        }
                    }
                }
            }
        )
    };

    //选择收货地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    $scope.isSelect = function (address) {
        if ($scope.address == address) {
            return true;
        } else {
            return false;
        }
    };

    $scope.order = {paymentType: '1'};
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    //结算
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {
                    location.href = "pay.html";
                } else {
                    alert(response.message);
                }
            }
        )
    }

});