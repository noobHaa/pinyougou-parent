app.service("cartService", function ($http) {
    this.findCartList = function () {//查询购物车列表
        return $http.get("cart/findCartList.do");
    };
    this.addGoodsToCartList = function (itemId, num) {//查询购物车列表
        return $http.get("cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num);
    };

    //计算合计数
    this.sum = function (cartList) {
        var totalValue = {totalNum: 0, totalMoney: 0.00}
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];//购物车明细
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    }
});