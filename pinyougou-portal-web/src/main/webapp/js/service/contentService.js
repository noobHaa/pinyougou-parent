app.service('contentService', function ($http) {
    //按照类目查询广告
    this.findByCategoryId = function (categoryId) {
        return $http.get("/content/findByCategoryId.do?categoryId=" + categoryId);
    }
})