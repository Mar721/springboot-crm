package crm.workbench.web.controllers;

import crm.commons.contants.Contants;
import crm.commons.pojo.ReturnObject;
import crm.settings.pojo.DicValue;
import crm.settings.pojo.User;
import crm.settings.service.DicValueService;
import crm.settings.service.UserService;
import crm.workbench.pojo.Tran;
import crm.workbench.pojo.TranHistory;
import crm.workbench.pojo.TranRemark;
import crm.workbench.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class TransactionController {
    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private TranService tranService;
    @Autowired
    private TranRemarkService tranRemarkService;
    @Autowired
    private TranHistoryService tranHistoryService;

    @RequestMapping("/workbench/transaction/index.do")
    public ModelAndView toIndex() {
        ModelAndView modelAndView = new ModelAndView();
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");

        modelAndView.addObject("transactionTypeList", transactionTypeList);
        modelAndView.addObject("sourceList", sourceList);
        modelAndView.addObject("stageList", stageList);

        modelAndView.setViewName("workbench/transaction/index");

        return modelAndView;
    }

    @RequestMapping("/workbench/transaction/toSave.do")
    public ModelAndView toSavePage() {
        ModelAndView mav = new ModelAndView();
        List<User> userList = userService.queryAllUser();
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");

        mav.addObject("userList", userList);
        mav.addObject("transactionTypeList", transactionTypeList);
        mav.addObject("sourceList", sourceList);
        mav.addObject("stageList", stageList);

        mav.setViewName("workbench/transaction/save");

        return mav;
    }

    @RequestMapping("/workbench/transaction/getPossibilityByStage.do")
    @ResponseBody
    public Object getPossibilityByStage(String stageValue) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("possibility");
        return resourceBundle.getString(stageValue);
    }

    /**
     * 查询数据传给前台的自动补全插件
     *
     * @param customerName
     * @return
     */
    @RequestMapping("/workbench/transaction/queryCustomerNameByName.do")
    @ResponseBody
    public Object queryCustomerNameByName(String customerName) {
        return customerService.queryCustomerNameByName(customerName);
    }

    @RequestMapping("/workbench/transaction/queryActivityByName.do")
    @ResponseBody
    public Object queryActivityByName(String activityName) {
        return activityService.queryActivityByActivityName(activityName);
    }

    @RequestMapping("/workbench/transaction/queryContactsByName.do")
    @ResponseBody
    public Object queryContactsByName(String contactsName) {
        return contactsService.queryContactsByName(contactsName);
    }


    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    //参数中加注解 @RequestParam ，mvc会把前台的键值封装到这个map中
    public Object saveTran(@RequestParam Map<String, Object> map, HttpSession session) {
        map.put(Contants.SESSION_USER, session.getAttribute(Contants.SESSION_USER));
        try {
            tranService.saveCreateTran(map);
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }


    @RequestMapping("/workbench/transaction/queryTranByConditionForPage.do")
    @ResponseBody
    public Object queryTranByCondition(String owner, String name,
                                       String customerName, String stage,
                                       String type, String source,
                                       String contactsName, Integer pageNo,
                                       Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("owner", owner);
        map.put("name", name);
        map.put("customerName", customerName);
        map.put("stage", stage);
        map.put("type", type);
        map.put("source", source);
        map.put("contactsName", contactsName);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        Map<String, Object> retMap = new HashMap<>();
        //查询
        try {
            List<Tran> tranList = tranService.queryTranByCondition(map);
            int totalRows = tranService.queryCountOfTranByCondition(map);
            retMap.put("tranList", tranList);
            retMap.put("totalRows", totalRows);
        }catch (Exception e){
            e.printStackTrace();
        }
        return retMap;

        //后端分页实在是有bug，只能放弃
    }


    @RequestMapping("/workbench/transaction/toTranDetail.do")
    public ModelAndView toDetail(String tranId){
        ModelAndView modelAndView = new ModelAndView();
        try {
            //交易
            Tran tran = tranService.queryTranForDetailById(tranId);
            //交易备注
            List<TranRemark> tranRemarkList = tranRemarkService.queryTranRemarkForDetailByTranId(tranId);
            //交易历史
            List<TranHistory> tranHistoryList = tranHistoryService.queryTranHistoryForDetailByTranId(tranId);
            //可能性分析
            ResourceBundle bundle = ResourceBundle.getBundle("possibility");
            String possibility = bundle.getString(tran.getStage());
            tran.setPossibility(possibility);
            //交易阶段
            List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
            //return
            modelAndView.addObject("tran",tran);
            modelAndView.addObject("stageList",stageList);
            modelAndView.addObject("tranRemarkList",tranRemarkList);
            modelAndView.addObject("tranHistoryList",tranHistoryList);
            modelAndView.setViewName("workbench/transaction/detail");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelAndView;
    }
}
