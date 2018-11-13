//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];//获取参数
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html(response.tbGoodsDesc.introduction);
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.tbGoodsDesc.customAttributeItems);
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse(response.tbGoodsDesc.itemImages);
                $scope.entity.tbGoodsDesc.specificationItems = JSON.parse(response.tbGoodsDesc.specificationItems);
                for (var i = 0; i < response.tbItems.length; i++) {
                    $scope.entity.tbItems[i].spec = JSON.parse(response.tbItems[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save = function () {
        debugger;
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.tbGoods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert("保存成功！");
                    location.href="goods.html";//跳转到商品列表页
                } else {
                    alert(response.message);
                }
            }
        );
    }
   /* //保存
    $scope.add = function () {
        debugger;
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert("新增成功！");
                    $scope.entity = {};
                    editor.html("");//清空富文本
                } else {
                    alert(response.message);
                }
            }
        );
    }*/
    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                } else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        );
    }
    $scope.entity = {tbGoodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []}};//定义页面实体结构
    //保存图片信息
    $scope.add_image_entity = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片
    $scope.remTab = function ($index) {
        $scope.entity.tbGoodsDesc.itemImages.splice($index, 1);
    }

    //一级下拉列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            });
    };
    //二级下拉列表
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            });
    });

    //三级下拉列表
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            });
    });

    //获取模板id
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.tbGoods.typeTemplateId = JSON.parse(response.typeId).id;
            }
        );
    });

    //获取品牌
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板
                $scope.typeTemplate.brandIds = JSON.parse(response.brandIds);
                if ($scope.entity.tbGoods.id == null) {
                    //获取扩展属性
                    $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                }
            }
        );
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });

    //保存规格信息
    $scope.updateSpecAttribute = function ($event, attributeName, attributeValue) {
        //判断是否存在规格
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, attributeName, 'attributeName');
        if (object != null) {
            if ($event.target.checked) {//判断是否勾选
                object.attributeValue.push(attributeValue);
            } else {
                object.attributeValue.splice(object.attributeValue.indexOf(attributeValue), 1);
                //没有勾选就取消规格
                if (object.attributeValue.length == 0) {
                    $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.tbGoodsDesc.specificationItems.push({
                'attributeName': attributeName,
                'attributeValue': [attributeValue]
            });
        }
    }
    //sku列表创建
    $scope.createItemList = function () {
        $scope.entity.tbItems = [{spec: {}, price: 0, num: 0, status: '0', isDefault: '0'}]//初始化一行
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {//规格循环
            var newList = [];
            for (var j = 0; j < $scope.entity.tbItems.length; j++) {//循环已有的行在基础上加列
                var oldRow = $scope.entity.tbItems[j];
                for (var k = 0; k < items[i].attributeValue.length; k++) {
                    newRow = JSON.parse(JSON.stringify(oldRow));
                    newRow.spec[items[i].attributeName] = items[i].attributeValue[k];
                    newList.push(newRow);
                }
            }
            $scope.entity.tbItems = newList;
        }
    }

    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];

    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        )
    }

    //判断规格是否勾选
    $scope.checkAttributeValue = function (attrName, attrValue) {
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, attrName, 'attributeName');
        if (object == null) {
            return false;
        }else{
            if (object.attributeValue.indexOf(attrValue)!=-1){
                return true;
            } else {
                return false;
            }
        }
    }
});
