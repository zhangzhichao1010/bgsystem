$(function () {
    getAuths();
});

function getAuths() {
    $.ajax({
        type: "GET",
        url: "/manage/getAuths",
        async: false,
        dataType: "text",
        success: function (data) {
            if (data) {
                var userAuthList = data.split(",");
                var audatas = $(".verify");
                for (var i = 0; i < audatas.length; i++) {
                    var audata = $(audatas[i]).attr("authority");
                    var isExit = false;
                    for (var j = 0; j < userAuthList.length; j++) {
                        if (audata == userAuthList[j]) {
                            isExit = true;
                            break;
                        }
                    }
                    if (!isExit) {
                        $(audatas[i]).remove();
                    }
                }
            }
        }

    })
}