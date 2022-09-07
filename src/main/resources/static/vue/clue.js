$(function(){
    var vue = new Vue({
        "el":"#tBody",
        data:{
            clueList:[]
        },
        methods:{
            toDetail:function (id) {
                window.location.href="workbench/clue/queryClueDetail.do?id="+id;
            }
        }
    });

    /**
     * 给全选按钮添加单击事件,给checkbox绑定全选
     */
    $("#checkAll").click(function () {
        //全选 this表示当前正在发生的对象的dom
        $("#tBody input[type='checkbox']").prop("checked",this.checked);
    });
    $("#tBody").on("click","input[type='checkbox']",function () {
        $("#checkAll").prop("checked",
            $("#tBody input[type='checkbox']").size() ==
            $("#tBody input[type='checkbox']:checked").size());
    });

    /**
     * 日历插件  容器加载完后，对容器调用工具函数
     */
    $(".mydate").datetimepicker({
        language:'zh-CN',
        format:'yyyy-mm-dd',
        minView:'month',	//可选择的最小视图
        initData:new Date(),	//初始化显示的时间
        autoclose:true,		//选择完自动关闭
        todayBtn:true,		//显示今天的按钮
        clearBtn:true		//显示清空按钮，但是是英文，改中文要在框架中修改
    });


    /**
     * 给创建绑定单击事件
     */
    $("#createClueBtn").click(function () {
        //表单重置
        $("#createClueFrom")[0].reset();
        $("#createClueModal").modal("show");

    });
    /**
     * 给保存绑定单击事件
     */
    $("#saveCreateClueBtn").click(function () {
        //收集数据
        var fullname = $.trim($("#create-fullname").val());
        var appellation = $("#create-appellation").val();
        var owner = $("#create-owner").val();
        var company = $.trim($("#create-company").val());
        var job = $.trim($("#create-job").val());
        var email = $.trim($("#create-email").val());
        var phone = $.trim($("#create-phone").val());
        var website = $.trim($("#create-website").val());
        var mphone = $.trim($("#create-mphone").val());
        var state = $("#create-state").val();
        var source = $("#create-source").val();
        var description = $.trim($("#create-description").val());
        var contactSummary = $.trim($("#create-contactSummary").val());
        var nextContactTime = $.trim($("#create-nextContactTime").val());
        var address = $.trim($("#create-address").val());
        //表单验证 * regx

        //send
        axios({
            method: "POST",
            url: "workbench/clue/saveCreateClue.do",
            params: {
                fullname: fullname,
                appellation: appellation,
                owner: owner,
                company: company,
                job: job,
                email: email,
                phone: phone,
                website: website,
                mphone: mphone,
                state: state,
                source: source,
                description: description,
                contactSummary: contactSummary,
                nextContactTime: nextContactTime,
                address: address
            }

        })
            .then(function (value) {
                if (value.data.code === "1") {
                    //关闭模态窗口
                    $("#createClueModal").modal("hide");
                    //flush,分页
                    queryClueByConditionForPage(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                } else {
                    alert(value.data.message);
                    $("#createClueModal").modal("show");
                }
            })
            .catch(function (reason) {
                console.log(reason);
            });
    });

    //分页 的 发送请求,主页加载完成显示第一页及数据总记录条数
    //查询时，不做去空格，参数验证等，因为对系统只读，无写数据
    queryClueByConditionForPage(1,10);

    //给条件查询的查询 按钮 添加单击事件
    //分页 的 发送请求,主页加载完成显示第一页及数据总记录条数
    //type="submit" 是同步请求，返回的数据会覆盖整个页面，异步请求要改为button，绑定ajax 点击事件
    $("#queryClueBtn").click(function () {
        queryClueByConditionForPage(1,$("#page").bs_pagination('getOption','rowsPerPage'));
    });


    /**
     * 分页回调函数
     * @param pageNo 页号
     * @param pageSize 每页大小
     */
    function queryClueByConditionForPage(pageNo,pageSize) {
        //收集参数
        var fullname = $("#query-fullname").val();
        var owner = $("#query-owner").val();
        var source = $("#query-source").val();
        var state = $("#query-state").val();
        var company = $("#query-company").val();
        var phone = $("#query-phone").val();
        var mphone = $("#query-mphone").val();
        //发送请求
        axios({
            method: "POST",
            url:"workbench/clue/queryActivityByConditionForPage.do",
            params: {
                fullname:fullname,
                owner:owner,
                source:source,
                state:state,
                company:company,
                phone:phone,
                mphone:mphone,
                pageNo: pageNo,
                pageSize: pageSize
            }
        })
            .then(function (value) {
                vue.clueList = value.data.clueList;

                //计算总页数
                var totalPages = 1;
                totalPages = Math.ceil(value.data.totalRows / pageSize);
                //对容器调用bs_pagination工具函数，显示翻页信息
                $("#page").bs_pagination({
                    currentPage: pageNo,//当前页号,相当于pageNo

                    rowsPerPage: pageSize,//每页显示条数,相当于pageSize
                    totalRows: value.data.totalRows,//总条数
                    totalPages: totalPages,  //总页数,必填参数.

                    visiblePageLinks: 5,//最多可以显示的卡片数

                    showGoToPage: true,//是否显示"跳转到"部分,默认true--显示
                    showRowsPerPage: true,//是否显示"每页显示条数"部分。默认true--显示
                    showRowsInfo: true,//是否显示记录的信息，默认true--显示

                    //用户每次切换页号，都自动触发本函数;
                    //每次返回切换页号之后的pageNo和pageSize
                    onChangePage: function (event, pageObj) { // returns page_num and rows_per_page after a link has clicked
                        queryClueByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                    }
                });
            }).catch(function (reason) {
            console.log(reason);
        });
    }

});
	