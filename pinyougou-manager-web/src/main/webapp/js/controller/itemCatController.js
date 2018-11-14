//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        debugger;
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                $scope.entity.typeId = JSON.parse(response.typeId);
            }
        );
    }
    $scope.parentId = 0;
    //保存
    $scope.save = function () {
        debugger;
        /*判断对象是否为空*/
        if ($scope.entity.tbItemCat.name == null || $scope.entity.tbItemCat.name == '') {
            alert("商品类型名称不可为空！");
            return;
        }
        if ($scope.entity.typeId == null || $scope.entity.typeId == '') {
            alert("商品模板不可为空！");
            return;
        }
        var serviceObject;//服务层对象
        if ($scope.entity.tbItemCat.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            debugger;
            $scope.entity.tbItemCat.parentId = $scope.parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    // $scope.reloadList();//重新加载
                    $scope.findByParentId($scope.parentId);
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        if ($scope.selectIds.length <= 0) {
            alert("请至少选择一个进行删除");
            return;
        }
        if (confirm("确定删除吗？")) {
            //获取选中的复选框
            debugger;
            itemCatService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        //$scope.reloadList();//刷新列表
                        $scope.findByParentId($scope.parentId);
                        $scope.selectIds = [];
                    }else {
                        alert(response.message);
                    }
                }
            );
        }
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询级别目录
    $scope.findByParentId = function (parentId) {
        $scope.parentId = parentId;
        itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //面包屑目录
    $scope.grade = 1;
    $scope.setGrade = function (value) {
        $scope.grade = value;
    }

    $scope.selectEntity = function (entity) {
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = entity;
        }
        $scope.findByParentId(entity.id);
    }

    //select下拉框初始化
    $scope.templateIds = {data: []};

    $scope.findTemplateList = function () {
        debugger;
        typeTemplateService.findTemplateList().success(
            function (response) {
                $scope.templateIds.data = response;
            }
        )
    }
});
