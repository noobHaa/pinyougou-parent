app.service('uploadService', function ($http) {

    this.uploadFile = function () {
        debugger;
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http({
            url: '../upload.do',
            method: 'POST',
            data: formData,
            headers: {'Content-type': undefined},
            transformRequest: angular.identity   //序列化formdata
        });
    }
})