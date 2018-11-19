app.controller("searchController", function ($scope, searchService) {
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                debugger;
                $scope.resultMap = response;
            }
        )
    }
    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '', 'spec': {}};

    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//查询
    }

    $scope.removeSearchItem=function (key) {
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key]='';
        }else {
          delete  $scope.searchMap.spec[key];
        }
        $scope.search();//查询
    }
});