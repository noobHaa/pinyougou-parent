app.controller("brandController", function ($scope, $http,$controller, brandService) {

    $controller("baseController",{$scope:$scope});
    //分页
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    $scope.searchEntity = {};//定义搜索对象
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                debugger;
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    //添加品牌
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };
    //回显修改模态框
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };
    //删除按钮
    $scope.delete = function () {
        if ($scope.selectIds.length <= 0) {
            alert("请至少选择一个进行删除");
            return;
        }
        if (confirm("确定删除吗？")) {
            brandService.delete($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();
                        $scope.selectIds = [];
                    } else {
                        alert(response.message);
                    }
                }
            );
        }
    };
});