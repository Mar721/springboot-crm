function $(id) {
    return document.getElementById(id);
}

function preRegist() {
    //用户名不能为空，而且是6~16位数字和字母组成
    var unameReg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$/;
    var unameTxt = $("unameTxt");
    var uname = unameTxt.value;
    var unameSpan = $("unameSpan");
    if (!unameReg.test(uname)) {
        unameSpan.style.visibility = "visible";
        return false;
    } else {
        unameSpan.style.visibility = "hidden";
    }

    //密码的长度至少为8位
    var pwdTxt = $("pwdTxt");
    var pwd = pwdTxt.value;
    var pwdReg = /[\w]{8,}/; // /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$/;
    var pwdSpan = $("pwdSpan");
    if (!pwdReg.test(pwd)) {
        pwdSpan.style.visibility = "visible";
        return false;
    } else {
        pwdSpan.style.visibility = "hidden";
    }

    //密码两次输入不一致
    var pwd2 = $("pwdTxt2").value;
    var pwdSpan2 = $("pwdSpan2");
    if (pwd2 != pwd) {
        pwdSpan2.style.visibility = "visible";
        return false;
    } else {
        pwdSpan2.style.visibility = "hidden";
    }

    return true;
}


window.onload = function () {
    var vue=new Vue({
        "el":"#registerForm",
        data:{
            uname:"",
            trueName:"",
            passWord:""
        },
        methods:{
            ckUname:function (){
                if(preRegist()===false){
                    return;
                }
                axios({
                    method:"POST",
                    url:"settings/qx/user/registerCheckUname.do",
                    params:{
                        uName:vue.uname
                    }
                })
                    .then(function (value){
                        if (value.data.code === "0") {
                            alert(value.data.message);
                            vue.uname = "";
                        }
                    })
                    .catch(function (reason){

                    })
            },
            register:function () {
                axios({
                    method:"POST",
                    url:"settings/qx/user/register.do",
                    params:{
                        loginAct:vue.uname,
                        name:vue.trueName,
                        loginPwd:vue.passWord
                    }
                })
                    .then(function (value) {
                        if (value.data.code === "1") {
                            window.location.href = "settings/qx/user/toLogin.do";
                        } else {
                            alert(value.data.message);
                        }
                    })
                    .catch(function (reason) {

                    });
            }
        }
    });
}
