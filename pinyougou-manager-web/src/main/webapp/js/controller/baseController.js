app.controller("baseController", function ($scope, $http, brandService) {
    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };
    $scope.reset = function () {
        $scope.searchEntity = {};
        $scope.reloadList();
    };
    /*实现全选*/
    $scope.selectIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//选中就放入数组
            $scope.selectIds.push(id);
        } else {
            $scope.selectIds.splice($scope.selectIds.indexOf(id), 1);
        }
    };

    $scope.selectAll = function ($event) {
        if ($event.target.checked) {
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.selectIds.indexOf($scope.list[i].id) == -1) {
                    $scope.selectIds.push($scope.list[i].id);//放入不重复的id
                }
            }
        } else {
            $scope.selectIds = [];
        }
    };
    //判断全选是否勾选
    $scope.ifCheckedAll = function () {
        for (var i = 0; i < $scope.list.length; i++) {
            if ($scope.selectIds.indexOf($scope.list[i].id) == -1) {
                return false;//数组中没有包含此数据，表示没有全选
            }
        }
        return true;
    };
    //勾选全选需要显示所有列勾选
    $scope.ifChecked = function (id) {
        for (var i = 0; i < $scope.selectIds.length; i++) {
            if ($scope.selectIds.indexOf(id) != -1) {
                return true;//数组中没有包含此数据，表示没有全选
            }
        }
        return false;
    };
});