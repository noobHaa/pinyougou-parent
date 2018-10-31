app.service("brandService", function ($http) {
    this.findPage = function (page, rows) {
        return $http.get('../brand/findPage.do?num=' + page + '&size=' + rows);
    };
    this.search = function (page, rows, searchEntity) {
        return $http.post('../brand/search.do?num=' + page + '&size=' + rows, searchEntity);
    };
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    };
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    };
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };
    this.delete = function (selectIds) {
        return $http.get("../brand/delete.do?ids=" + selectIds);
    };
});