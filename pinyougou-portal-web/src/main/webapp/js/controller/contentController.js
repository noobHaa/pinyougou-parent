app.controller('contentController', function ($scope,$controller, contentService) {
    $controller('baseController', {$scope: $scope});//继承

    $scope.contentList = [];//广告集合
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                debugger;
                $scope.contentList[categoryId] = response;
            }
        )
    }
})