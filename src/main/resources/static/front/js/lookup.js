$(function () {
    var jsBridge_cmd_handlers = {};
    var unames = "";
    var html = "";

    function openFile() {
        //发送消息。
        jsBridge.postNotification('CLIENT_CHOOSE_IMAGE');

        if (!jsBridge_cmd_handlers["CLIENT_CHOOSE_IMAGE_RESULT"]) {

            var handler = jsBridge_cmd_handlers["CLIENT_CHOOSE_IMAGE_RESULT"] = function (object) {
                var html = "";
                //返回格式:{"files":[{"type":"image/jpeg","name":"xxx.jepg","data":"....."},{"type":"image/jpeg","name":"xxx.jepg","data":"....."}]}
                //客户端返回的图片type分类
                var image_mime = {
                    "jpe": "image/jpeg",
                    "jpeg": "image/jpeg",
                    "jpg": "image/jpeg",
                    "bmp": "image/bmp",
                    "dib": "image/bmp",
                    "gif": "image/gif",
                    "ief": "image/ief",
                    "png": "image/png",
                    "pnz": "image/png",
                    "svg": "image/svg+xml",
                    "ico": "image/x-icon",
                };
                if (object && object.files) {
                    object.files.forEach(function (item) {
                        var img = "data:" + item.type + ";base64," + item.data;
                        html += "<img src='" + img + "' width='200' height='200' /> <br />";
                    })
                } else {
                    html = "无数据";
                }
                document.getElementById("imghtml").innerHTML = html;
            };
            try {
                jsBridge.bind('CLIENT_CHOOSE_IMAGE_RESULT', handler);
            } catch (e) {
                alert(e);
            }
        }
    }

    $(".lookUp").on("click", function () {
        if (!jsBridge_cmd_handlers["CLIENT_CHOOSE_CONTACT_RESULT"]) {
            var handler = jsBridge_cmd_handlers["CLIENT_CHOOSE_CONTACT_RESULT"] = function (object) {
                var personList = object.personList;
                if (object && personList && !unames) {
                    for (var i = 0; i < personList.length; i++) {
                        var person = personList[i];
                        var uname = eval('person.name');
                        /* var uid = eval('person.uid');*/
                        html += '<span>' + uname + '<i></i></span>';
                        unames += uname + ","
                    }
                } else if (object && personList && unames) {
                    for (var i = 0; i < personList.length; i++) {
                        var person = personList[i];
                        var uname = eval('person.name');
                        /*  var uid = eval('person.uid');*/
                        if (!checkIsExsitUname(unames, uname)) {
                            html += '<span>' + uname + '<i></i></span>';
                            unames += uname + ","
                        }
                    }
                }
                document.getElementById("userList").innerHTML = html;
            };
            try {
                jsBridge.bind('CLIENT_CHOOSE_CONTACT_RESULT', handler);
            } catch (e) {
                alert(e);
            }
        }
        //发送消息。
        jsBridge.postNotification('CLIENT_CHOOSE_NEWCONTACT', {"multiple": true, "chooseDept": false});
    });

    function checkIsExsitUname(unames, uname) {
        var uidlist = unames.split(",");
        for (var i = 0; i < uidlist.length; i++) {
            if (uname === uidlist[i]) {
                return true;
            }
        }
        return false;
    }
});