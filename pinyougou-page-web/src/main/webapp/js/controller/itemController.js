app.controller("itemController", function ($scope, $http) {
    //商品数量的加减
    $scope.addNum = function (x) {
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };
    //选择的规格
    $scope.specificationItems = {};

    //选择规格，更新变量
    $scope.selectSpecification = function (key, value) {
        debugger;
        $scope.specificationItems[key] = value;
        search();
    };
    //判断规格是否勾选
    $scope.isSelect = function (key, value) {
        debugger;
        if ($scope.specificationItems[key] == value) {
            return true;
        } else {
            return false;
        }
    };

    $scope.sku = {};
    //初始化加载默认sku
    $scope.loadSku = function () {
        debugger;
        $scope.specificationItems = JSON.parse(JSON.stringify(skuList[0].spec));
        $scope.sku = skuList[0];
    };
    //判断是否相等
    matchObject = function (map1, map2) {
        debugger;
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        return true;
    };
    //更新sku
    search = function () {
        debugger;
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject($scope.specificationItems, skuList[i].spec)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {id: 0, title: '--------', price: 0};//不存在商品
    };

    $scope.addToCart = function () {
        //alert('skuid:'+$scope.sku.id);
        $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId=" + $scope.sku.id + "&num=" + $scope.num,{'withCredentials':true}).success(
            function (response) {
                if (response.success) {
                    location.href = "http://localhost:9107/cart.html";
                } else {
                    alert(response.success);
                }
            }
        )
    };

});