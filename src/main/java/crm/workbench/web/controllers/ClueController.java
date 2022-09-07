package crm.workbench.web.controllers;

import crm.commons.contants.Contants;
import crm.commons.pojo.ReturnObject;
import crm.commons.utils.DateUtil;
import crm.commons.utils.UUIDUtil;
import crm.settings.pojo.DicValue;
import crm.settings.pojo.User;
import crm.settings.service.DicValueService;
import crm.settings.service.UserService;
import crm.workbench.pojo.Activity;
import crm.workbench.pojo.Clue;
import crm.workbench.pojo.ClueActivityRelation;
import crm.workbench.pojo.ClueRemark;
import crm.workbench.service.ActivityService;
import crm.workbench.service.ClueActivityRelationService;
import crm.workbench.service.ClueRemarkService;
import crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {
    @Autowired
    private UserService userService;
    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private ClueService clueService;
    @Autowired
    private ClueRemarkService clueRemarkService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        List<User> usersList = userService.queryAllUser();
        List<DicValue> appellation = dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> clueState = dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> source = dicValueService.queryDicValueByTypeCode("source");

        modelAndView.addObject("userList", usersList);
        modelAndView.addObject("appellationList", appellation);
        modelAndView.addObject("clueStateList", clueState);
        modelAndView.addObject("sourceList", source);
        modelAndView.setViewName("workbench/clue/index");
        return modelAndView;
    }


    @RequestMapping("/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public Object createClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        clue.setId(UUIDUtil.getUUID());
        clue.setCreateTime(DateUtil.formatDateTime(new Date()));
        clue.setCreateBy(user.getId());
        try {
            int i = clueService.saveClue(clue);
            if (i > 0) {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                        "系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }

    @RequestMapping("/workbench/clue/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryClueByCondition(String fullname, String company,
                                       String phone, String source,
                                       String owner, String mphone, String state,
                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        //封装
        Map<String, Object> map = new HashMap<>();
        map.put("name", fullname);
        map.put("owner", owner);
        map.put("company", company);
        map.put("phone", phone);
        map.put("mobilePhone", mphone);
        map.put("source", source);
        map.put("state", state);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        //查询
        List<Clue> clueList = clueService.queryClueByCondition(map);
        int totalRows = clueService.queryCountOfClueByCondition(map);

        //响应
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("clueList", clueList);
        retMap.put("totalRows", totalRows);
        return retMap;

        //后端分页实在是有bug，只能放弃
    }

    @RequestMapping("/workbench/clue/queryClueDetail.do")
    public ModelAndView toDetail(String id) {
        ModelAndView modelAndView = new ModelAndView();
        Clue clue = clueService.queryClueById(id);
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkListByClueId(id);
        List<Activity> activityList = activityService.queryConnectActivityByClueId(id);
        modelAndView.addObject("clue", clue);
        modelAndView.addObject("remarkList", clueRemarkList);
        modelAndView.addObject("activityList", activityList);
        modelAndView.setViewName("workbench/clue/detail");
        return modelAndView;
    }

    @RequestMapping("workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public Object getActivity(String clueId, String activityName) {
        Map<String, Object> map = new HashMap<>();
        map.put("clueId", clueId);
        map.put("activityName", activityName);
        //模糊查询
        return activityService.queryActivityForDetailByNameClueId(map);
    }

    @RequestMapping("workbench/clue/saveBund.do")
    @ResponseBody
    public Object saveActivityClueBund(String[] ids, String clueId) {
        ArrayList<ClueActivityRelation> clueActivityRelationList = new ArrayList<>();
        ClueActivityRelation clueActivityRelation;
        for (String id : ids) {
            clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtil.getUUID());
            clueActivityRelation.setActivityId(id);
            clueActivityRelation.setClueId(clueId);
            clueActivityRelationList.add(clueActivityRelation);
        }
        try {
            int res = clueActivityRelationService.saveBund(clueActivityRelationList);
            if (res > 0) {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                        "系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }


    @RequestMapping("/workbench/clue/saveunBund.do")
    @ResponseBody
    public Object saveActivityClueUnBund(ClueActivityRelation clueActivityRelation) {
        try {
            int res = clueActivityRelationService.saveunBund(clueActivityRelation);
            if (res > 0) {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                        "系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public ModelAndView toConvert(String clueId) {
        ModelAndView modelAndView = new ModelAndView();
        Clue clue = clueService.queryClueById(clueId);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        modelAndView.addObject("clue", clue);
        modelAndView.addObject("stageList", stageList);
        modelAndView.setViewName("workbench/clue/convert");
        return modelAndView;
    }


    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public Object queryActivityForConvert(String clueId, String activityName) {
        Map<String, Object> map = new HashMap<>();
        map.put("clueId", clueId);
        map.put("activityName", activityName);
        //模糊查询
        return activityService.queryActivityForConvertByNameClueId(map);
    }

    @RequestMapping("/workbench/clue/clueConvert.do")
    @ResponseBody
    public Object clueConvert(String clueId,
                              String money,
                              String name,
                              String expectedDate,
                              String stage,
                              String activityId,
                              String isCreateTran,
                              HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("isCreateTran",isCreateTran);
        map.put("money",money);
        map.put("name",name);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("expectedDate",expectedDate);
        map.put("user",session.getAttribute(Contants.SESSION_USER));
        try {
            clueService.convertClue(map);
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                    "系统忙，请稍后重试...");
        }
    }
}
