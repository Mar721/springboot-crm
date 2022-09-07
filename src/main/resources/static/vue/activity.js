$(function () {
    /**
     * 查询按钮
     */
    $("#queryButton").click(function () {
        //点击查询按钮改变查询条件后，显示第一页数据,保持用户选择的每页显示条数不变
        vue2.getActivity(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
    });

    //修改活动的日期日历不起作用，无法解决该问题
    $(".myDate").datetimepicker({
        language: "zh-CN",//语言
        format: "yyyy-mm-dd",//日期的格式
        minView: "month",//可以选择的最小视图
        initialDate: new Date(),//初始化显示的日期
        autoclose: true,//设置选择完日期或时间之后，是否自动关闭日历
        todayBtn: true,//设置是否显示”今天“按钮
        clearBtn: true//设置是否显示”清空“按钮
    });
    //vue无法使用
    //v-model无法绑定，导致axios传参报错
    //vue data内容未初始化
    var vue2 = new Vue({
        "el": "#mainDiv",
        data: {
            ids: [],
            isCheckedAll: false,
            activityList: [],
            activityCount: "",
        },
        methods: {
            //向后台发送请求查询活动
            getActivity: function (pageNo, pageSize) {
                axios({
                    method: "GET",
                    url: "workbench/activity/queryActivity.do",
                    params: {
                        name: $("#query-name").val(),
                        owner: $("#query-owner").val(),
                        beginDate: $("#query-startTime").val(),
                        endDate: $("#query-endTime").val(),
                        pageNo: pageNo,
                        pageSize: pageSize
                    }
                })
                    .then(function (value) {
                        //重置删除勾选
                        vue2.ids = [];

                        vue2.activityList = value.data.activityList;
                        vue2.activityCount = value.data.activityCount;
                        /**
                         * 计算总页数
                         */
                        var totalPages;
                        if (vue2.activityCount % pageSize === 0) {
                            totalPages = vue2.activityCount / pageSize;
                        } else {
                            totalPages = parseInt(vue2.activityCount / pageSize) + 1;
                        }
                        /**
                         * 调用分页插件
                         */
                        $("#page").bs_pagination({
                            currentPage: pageNo,//当前页号,相当于pageNo

                            rowsPerPage: pageSize,//每页显示条数,相当于pageSize
                            totalRows: vue2.activityCount,//总条数
                            totalPages: totalPages,  //总页数,必填参数.

                            visiblePageLinks: 5,//最多可以显示的卡片数

                            showGoToPage: true,//是否显示"跳转到"部分,默认true--显示
                            showRowsPerPage: true,//是否显示"每页显示条数"部分。默认true--显示
                            showRowsInfo: true,//是否显示记录的信息，默认true--显示

                            //用户每次切换页号，都自动触发本函数;
                            //每次返回切换页号之后的pageNo和pageSize
                            onChangePage: function (event, pageObj) { // returns page_num and rows_per_page after a link has clicked
                                vue2.getActivity(pageObj.currentPage, pageObj.rowsPerPage);
                            }
                        });
                    })
                    .catch(function (reason) {
                        console.log(reason);
                    });
            },
            //全选按钮单击事件
            checkAll: function () {
                //点击全选按钮，带动其他按钮选中，通过:checked="ids.indexOf(activity.id)>=0"
                this.isCheckedAll = !this.isCheckedAll
                if (this.isCheckedAll) {
                    // 全选时
                    this.ids = []
                    this.activityList.forEach(function (activity) {
                        this.ids.push(activity.id)
                    }, this)
                } else {
                    this.ids = []
                }
            },
            //选中某一个活动
            checkOne: function (id) {
                //所有都被选中时可以带动全选按钮，通过:checked="ids.length === activityList.length"
                let idIndex = this.ids.indexOf(id)
                if (idIndex >= 0) {
                    // 如果已经包含了该id, 则去除(单选按钮由选中变为非选中状态)
                    this.ids.splice(idIndex, 1)
                } else {
                    // 选中该checkbox
                    this.ids.push(id)
                }
            },
            //删除活动
            deleteActivity: function () {
                if (vue2.ids.length===0) {
                    alert("请选择至少一个活动");
                    return;
                }
                if (confirm("是否确定删除？")) {
                    axios({
                        method: "POST",
                        url: "workbench/activity/deleteActivity.do",
                        params: {
                            ids: vue2.ids
                        },
                        paramsSerializer: function (params) {
                            return Qs.stringify(params, {indices: false})
                        }
                    })
                        .then(function (value) {
                            if (value.data.code === "1") {
                                vue2.getActivity(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                            } else {
                                alert(value.data.message);
                            }
                        })
                        .catch(function (reason) {
                            console.log(reason);
                        });
                }
            },
            //点击修改按钮，显示对应的活动内容
            clickChangeButton: function () {
                if (vue2.ids.length === 0) {
                    alert("请选择至少一个活动");
                    return;
                }
                if (vue2.ids.length !== 1) {
                    alert("只能选择一个活动");
                    return;
                }
                /**
                 * 本来想着直接使用之前查询到的数据来显示修改页面
                 * 但后来发现批量查询的数据中owner所有者是具体的名字（因为使用了多表查询）
                 * 但在修改页面中的所有者是下拉列表，通过thymelef来渲染页面
                 * th：text显示的是所有者owner的名字，但th：value用的是所有者的id，
                 * 由上可知activity.owner不是id，
                 * 所以无法实现$("#edit-marketActivityOwner").val(activity.owner)显示所有者，需要重新查询
                 */

                const id = vue2.ids[0];
                // let activity = vue2.activityList.find(function (e) {
                //     return e.id === id
                // });
                // $("#edit-marketActivityOwner").val(activity.owner);
                // $("#edit-marketActivityName").val(activity.name);
                // $("#edit-startDate").val(activity.startDate);
                // $("#edit-endDate").val(activity.endDate);
                // $("#edit-cost").val(activity.cost);
                // $("#edit-description").val(activity.description);
                axios({
                    method:"POST",
                    url:"workbench/activity/queryActivityById.do",
                    params:{
                        id:id
                    }
                }).then(function (value) {
                    //id是隐藏域，在修改时提交，主键
                    //把市场活动的信息显示在模态窗口中
                    $("#edit-id").val(value.data.id);
                    $("#edit-marketActivityOwner").val(value.data.owner);
                    $("#edit-marketActivityName").val(value.data.name);
                    $("#edit-startDate").val(value.data.startDate);
                    $("#edit-endDate").val(value.data.endDate);
                    $("#edit-cost").val(value.data.cost);
                    $("#edit-description").val(value.data.description);
                    $("#editActivityModal").modal('show');
                }).catch(function (reason) {
                    console.log(reason);
                })

            },
            //批量导出按钮
            allActivityDown:function (){
                window.location.href="workbench/activity/exportAllActivity.do";
            },
            //选择导出按钮
            selectedActivityDown:function () {
                if (vue2.ids.length===0){
                    alert("请至少选中一个活动!");
                    return;
                }
                var idStr="";
                for(var i=0;i<vue2.ids.length;i++){
                    idStr+="id="+vue2.ids[i]+"&";
                }
                idStr = idStr.substr(0,idStr.length-1);
                window.location.href="workbench/activity/exportSelectedActivity.do?"+idStr;
            },
            //查看详情按钮
            toDetail:function (id) {
                window.location.href="workbench/activity/detail.do?id="+id;
            }
        },

        mounted: function () {
            this.getActivity(1, 10);
            /**
             * 日历插件
             */
            $(".myDate").datetimepicker({
                language: "zh-CN",//语言
                format: "yyyy-mm-dd",//日期的格式
                minView: "month",//可以选择的最小视图
                initialDate: new Date(),//初始化显示的日期
                autoclose: true,//设置选择完日期或时间之后，是否自动关闭日历
                todayBtn: true,//设置是否显示”今天“按钮
                clearBtn: true//设置是否显示”清空“按钮
            });
        }
    });


    var vue = new Vue({
        "el": "#createActivityModal",
        data: {
            owner: "",
            activityName: "",
            beginTime: "",
            endTime: "",
            cost: "",
            describe: ""
        },
        methods: {
            createActivity: function () {
                if (vue.owner === "") {
                    alert("所有者不能为空!");
                    return;
                }
                if (vue.activityName === "") {
                    alert("名称不能为空!");
                    return;
                }
                if (vue.beginTime > vue.endTime) {
                    alert("结束日期不能比开始日期小！");
                    return;
                }
                if (vue.cost !== "") {
                    var regExp = /^(([1-9]\d*)|0)$/;
                    if (!regExp.test(vue.cost)) {
                        alert("成本只能是非负数！");
                        return;
                    }
                }
                axios({
                    method: "PUT",
                    url: "workbench/activity/createActivity.do",
                    params: {
                        owner: vue.owner,
                        name: vue.activityName,
                        startDate: vue.beginTime,
                        endDate: vue.endTime,
                        cost: vue.cost,
                        description: vue.describe
                    }
                }).then(function (value) {
                    if (value.data.code === "1") {
                        $("#createActivityModal").modal("hide");
                        vue2.getActivity(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                        vue.owner = "";
                        vue.activityName = "";
                        vue.beginTime = "";
                        vue.endTime = "";
                        vue.cost = "";
                        vue.describe = "";    //在这里清空比较人性化
                        $("#createActivityForm").get(0).reset();	//重置表单
                    } else {
                        alert(value.data.message);
                        $("#createActivityModal").modal("show");//可以不写
                    }
                }).catch(function (reason) {
                    console.log(reason);
                });
            }
        },
        mounted: function () {
            $(".myDate").datetimepicker({
                language: "zh-CN",//语言
                format: "yyyy-mm-dd",//日期的格式
                minView: "month",//可以选择的最小视图
                initialDate: new Date(),//初始化显示的日期
                autoclose: true,//设置选择完日期或时间之后，是否自动关闭日历
                todayBtn: true,//设置是否显示”今天“按钮
                clearBtn: true//设置是否显示”清空“按钮
            }).on('changeDate', function () {
                vue.beginTime = $("#create-startTime").val();
                vue.endTime = $("#create-endTime").val();
            });
        }
    });

    var vue3 = new Vue({
        "el":"#editActivityModal",
        data:{},
        methods:{
            changeActivity:function () {
                //收集此参数
                var id = $("#edit-id").val();
                var owner = $("#edit-marketActivityOwner").val();
                var name =$.trim($("#edit-marketActivityName").val());
                var startDate = $("#edit-startDate").val();
                var endDate = $("#edit-endDate").val();
                var cost = $.trim($("#edit-cost").val());
                var description = $.trim($("#edit-description").val());
                //表单验证
                if (owner === "" || owner===null){
                    alert("所有者不能为空!");
                    return;
                }
                if (name === "" || name ===null){
                    alert("名称不能为空!");
                    return;
                }
                if (startDate!==""){
                    var reg = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|          (?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
                    var regExp1=new RegExp(reg);
                    if(!regExp1.test(startDate)) {
                        alert("日期格式不正确，应为yyyy-mm-dd,且年月日合法");
                        return;
                    }
                }
                if (endDate!==""){
                    var reg2 = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|          (?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
                    var regExp2=new RegExp(reg2);
                    if(!regExp2.test(endDate)) {
                        alert("日期格式不正确，应为yyyy-mm-dd，且年月日合法");
                        return;
                    }
                }

                if (startDate>endDate){
                    alert("结束日期不能比开始日期小！");
                    return;
                }
                if (cost!==null&&cost!==""){
                    var regExp = /^(([1-9]\d*)|0)$/;
                    if (!regExp.test(cost)){
                        alert("成本只能是非负数！");
                        return;
                    }
                }
                axios({
                    method: "POST",
                    url: "workbench/activity/changeActivity.do",
                    params: {
                        id:id,
                        owner: owner,
                        name: name,
                        startDate: startDate,
                        endDate: endDate,
                        cost: cost,
                        description: description
                    }
                })
                    .then(function (value) {
                        if (value.data.code === "1") {
                            $("#editActivityModal").modal("hide");
                            vue2.getActivity($("#page").bs_pagination('getOption', 'currentPage'), $("#page").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(value.data.message);
                            $("#editActivityModal").modal("show");//可以不写
                        }
                    })
                    .catch(function (reason) {
                        console.log(reason);
                    });
            }
        },
        mounted: function () {
            $(".myDate").datetimepicker({
                language: "zh-CN",//语言
                format: "yyyy-mm-dd",//日期的格式
                minView: "month",//可以选择的最小视图
                initialDate: new Date(),//初始化显示的日期
                autoclose: true,//设置选择完日期或时间之后，是否自动关闭日历
                todayBtn: true,//设置是否显示”今天“按钮
                clearBtn: true//设置是否显示”清空“按钮
            }).on('changeDate', function () {
            });
        }
    });

    var vue4=new Vue({
        "el":"#importActivityModal",
        data:{},
        methods:{
            importActivity:function () {

                var activityFileName = $("#activityFile").val(); //只能获取文件名
                //后缀
                var suffix = activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
                if (suffix!=="xls"){
                    alert("只支持xls文件");
                    return;
                }
                //文件本身
                var activityFile = $("#activityFile")[0].files[0];//获取DOM对象
                if(activityFile.size > 5*1024*1024 ){
                    alert("文件大小不能超过5M");
                    return;
                }
                var formData = new FormData();
                formData.append("multipartFile",activityFile);
                axios({
                    method:"POST",
                    url:"workbench/activity/fileUpLoad.do",
                    processData:false,//是否把参数统一转成字符串
                    contentType:false,//是否把所有的参数按urlencoded编码，文件上传
                    data:formData,
                })
                    .then(function (value) {
                        if (value.data.code==="1"){
                            $("#importActivityModal").modal("hide");
                            alert(value.data.message);

                        }else {
                            alert(value.data.message);
                            $("#importActivityModal").modal("show");
                        }
                    })
                    .catch(function (reason) {
                        console.log(reason);
                    });
            }
        }
    });

    // $("#backClick").click(function () {
    //     window.history.back();
    //     vue2.getActivity($("#page").bs_pagination('getOption', 'currentPage'), $("#page").bs_pagination('getOption', 'rowsPerPage'));
    // })
});
