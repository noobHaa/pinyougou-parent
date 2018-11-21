app.controller("searchController", function ($scope, $location, searchService) {
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPage();
                $scope.hiddenBrand = keywordsIsBrand();
            }
        )
    };
    $scope.pageLabel = [];
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sort': '',
        'sortField': ''
    };

    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//查询
    };

    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete  $scope.searchMap.spec[key];
        }
        $scope.search();//查询
    };

    $scope.findPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPage) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        $scope.search();
    };

    //构建分页
    buildPage = function () {
        $scope.pageLabel = [];
        if ($scope.resultMap.totalPage <= 5) {
            for (var i = 1; i <= $scope.resultMap.totalPage; i++) {
                $scope.pageLabel.push(i);
            }
            $scope.firstDot = false;
            $scope.lastDot = false;
        } else {//多于五页需要根据当前页构建
            var lastPage = 1;
            var firstPage = 1;
            if ($scope.searchMap.pageNo <= 3) {//如果当前页小于等于三页
                lastPage = 5;
                $scope.firstDot = false;
                $scope.lastDot = true;
            } else {//当前页大于第三页
                firstPage = $scope.searchMap.pageNo - 2;
                if ($scope.resultMap.totalPage - $scope.searchMap.pageNo < 2) {
                    firstPage = $scope.resultMap.totalPage - 4;//显示最后五页
                    lastPage = $scope.resultMap.totalPage;
                    $scope.firstDot = true;
                    $scope.lastDot = false;
                } else {//显示中间五页
                    lastPage = $scope.searchMap.pageNo + 2;
                    $scope.firstDot = true;
                    $scope.lastDot = true;
                }
            }
            for (var i = firstPage; i <= lastPage; i++) {
                $scope.pageLabel.push(i);
            }
        }
    };

    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    };
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPage) {
            return true;
        } else {
            return false;
        }
    };

    //排序设置
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
    };
    $scope.hiddenBrand = false;
    //隐藏品牌
    keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    };

    //关键字搜索
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    };
});