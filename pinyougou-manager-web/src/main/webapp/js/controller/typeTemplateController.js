//控制层
app.controller('typeTemplateController', function ($scope, $controller, typeTemplateService, brandService, specificationService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        typeTemplateService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        typeTemplateService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                $scope.entity.brandIds = JSON.parse(response.brandIds);
                $scope.entity.specIds = JSON.parse(response.specIds);
                $scope.entity.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
        );
    }

    //保存
    $scope.save = function () {
        /*判断对象是否为空*/
        if ($scope.entity.name == null || $scope.entity.name == '') {
            alert("商品类型名称不可为空！");
            return;
        }
        for (var i = 0; i < $scope.entity.customAttributeItems.length; i++) {
            //数据存在空
            if (($scope.entity.customAttributeItems[i].text == null) || ($scope.entity.customAttributeItems[i].text == '')) {
                $scope.entity.customAttributeItems.splice(i, 1);
            }
        }
        debugger;
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = typeTemplateService.update($scope.entity); //修改
        } else {
            serviceObject = typeTemplateService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
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
            typeTemplateService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                        $scope.selectIds = [];
                    }
                }
            );
        }
    }
    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        typeTemplateService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //新增规格列
    $scope.addTab = function () {
        $scope.entity.customAttributeItems.push({});
    };

    $scope.remTab = function ($index) {
        $scope.entity.customAttributeItems.splice($index, 1);
    };

    $scope.brandList = {data: []};
    //初始化所有品牌信息
    $scope.findBrandList = function () {
        brandService.selectOptionList().success(
            function (response) {
                $scope.brandList.data = response;
            }
        );
    }
    //初始化所有规格信息
    $scope.specList = {data: []};
    //初始化所有品牌信息
    $scope.findSpecificationList = function () {
        specificationService.selectOptionList().success(
            function (response) {
                $scope.specList.data = response;
            }
        );
    }

    //将页面的json转换
    $scope.jsonTOString = function (jsonString, key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ","
            }
            value += json[i][key];
        }
        return value;
    }
});	
