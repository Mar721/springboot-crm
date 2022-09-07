window.onload = function () {
    var vue = new Vue({
        "el": "#loginForm",
        data: {
            // loginForm:{
            //     loginAct:"",
            //     passWord:"",
            //     isRememberPwd:""
            // },
            //message:""
            loginAct: "",
            passWord: "",
            isRememberPwd: "",
            message: ""
        },
        methods: {
            login: function () {
                if (vue.loginAct === "") {
                    alert("用户名不能为空");
                    return;
                }
                if (vue.passWord === "") {
                    alert("密码不能为空");
                    return;
                }
                vue.message = "正在努力校验中";
                axios({
                    method: "POST",
                    url: "settings/qx/user/login.do",
                    params: {
                        loginAct: vue.loginAct,
                        loginPwd: vue.passWord,
                        isrememberpwd: vue.isRememberPwd
                    }
                }).then(function (value) {
                    if (value.data.code === "1") {
                        window.location.href = "workbench/index.do";
                    } else {
                        vue.message = value.data.message;
                    }
                }).catch(function (reason) {

                })
            },
            getCookie: function (key) {
                if (document.cookie.length > 0) {
                    var str=document.cookie;
                    var start = document.cookie.indexOf(key + '=')
                    if (start !== -1) {
                        start = start + key.length + 1
                        var end = document.cookie.indexOf(';', start)
                        if (end === -1) end = document.cookie.length
                        return unescape(document.cookie.substring(start, end))
                    }
                }
                return ''
            }
        },
        mounted() {
            var self = this;
            document.onkeydown = function (e) {
                //keyCode对应着键盘的编码
                if (window.event.keyCode === 13) {
                    self.login();
                }
            };

        },
        created() {


            // 在页面加载时从cookie获取登录信息
            let loginAct = this.getCookie("loginAct")
            let loginPwd = this.getCookie("loginPwd")
            // 如果存在赋值给表单，并且将记住密码勾选
            if (loginAct) {
                this.loginAct = loginAct
                this.passWord = loginPwd
                this.isRememberPwd = true

                axios({
                    method: "POST",
                    url: "settings/qx/user/login.do",
                    params: {
                        loginAct: this.loginAct,
                        loginPwd: this.passWord,
                        isrememberpwd: this.isRememberPwd,
                        haveCookie:"true"
                    }
                }).then(function (value) {
                    if (value.data.code === "1") {
                        window.location.href = "workbench/index.do";
                    } else {
                        vue.message = value.data.message;
                    }
                }).catch(function (reason) {

                })
            }
        }
    });
}