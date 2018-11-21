app.controller('contentController', function ($scope, $controller, contentService) {
    $controller('baseController', {$scope: $scope});//继承

    $scope.contentList = [];//广告集合
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                debugger;
                $scope.contentList[categoryId] = response;
            }
        )
    };

    //关键字搜索
    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
})