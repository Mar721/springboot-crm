package crm.workbench.web.controllers;

import crm.commons.contants.Contants;
import crm.commons.pojo.ReturnObject;
import crm.commons.utils.DateUtil;
import crm.commons.utils.HSSFUtil;
import crm.commons.utils.UUIDUtil;
import crm.settings.pojo.User;
import crm.settings.service.UserService;
import crm.workbench.pojo.Activity;
import crm.workbench.pojo.ActivityRemark;
import crm.workbench.service.ActivityRemarkService;
import crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        List<User> usersList = userService.queryAllUser();
        modelAndView.addObject("userList", usersList);
        modelAndView.setViewName("workbench/activity/index");
        return modelAndView;
    }

    @RequestMapping("/workbench/activity/createActivity.do")
    @ResponseBody
    public Object createActivity(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setId(UUIDUtil.getUUID());
        activity.setCreateTime(DateUtil.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        try {
            int i = activityService.saveActivity(activity);
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

    @RequestMapping("/workbench/activity/queryActivity.do")
    @ResponseBody
    public Object QueryActivity(String name, String owner, String beginDate,
                                String endDate, int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("beginDate", beginDate);
        map.put("endDate", endDate);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int activityCount = activityService.queryCountActivityCondition(map);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("activityList", activityList);
        returnMap.put("activityCount", activityCount);
        return returnMap;
    }

    @RequestMapping("/workbench/activity/deleteActivity.do")
    @ResponseBody
    public Object deleteActivity(String[] ids) {
        try {
            int i = activityService.deleteActivityIds(ids);
            //删除对应市场活动的备注
            int res = activityRemarkService.deleteRemarkByActivityIds(ids);
            if (i > 0 && res > 0) {
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

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id) {
        return activityService.queryActivityById(id);
    }


    @RequestMapping("/workbench/activity/changeActivity.do")
    @ResponseBody
    public Object changeActivity(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setEditBy(user.getId());
        activity.setEditTime(DateUtil.formatDateTime(new Date()));
        try {
            int i = activityService.changeActivity(activity);
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

    @RequestMapping("/workbench/activity/exportAllActivity.do")
    public void exportAllActivity(HttpServletResponse response) throws IOException {
        List<Activity> activityList = activityService.getAllActivity();
        /*
          使用apache-poi插件生成excel文件
         */
        HSSFWorkbook wb = HSSFUtil.getExportFile(activityList);

        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        //流谁创建谁close
        wb.close();
        outputStream.flush();


//        OutputStream fileOutputStream = new
//                FileOutputStream("E:\\idea-workspace\\ssm\\crm\\src\\" +
//                "main\\resources\\activityList.xls");
//        wb.write(fileOutputStream);
//        fileOutputStream.close();
//        wb.close();
//
//        /*
//        这里wb要先把文件写入磁盘，然后系统又要从磁盘中读取数据，效率太低
//        wb.write可以直接写到输出流，但使用了responseEntity的作为返回值无法实现，需要改为用response响应文件
//        public ResponseEntity<byte[]> fileDownload()
//        改为 public void fileDownload(response)
//         */
//        InputStream is = new FileInputStream("E:\\idea-workspace\\ssm\\crm\\src\\" +
//                "main\\resources\\activityList.xls");
//        //创建字节数组
//        byte[] bytes = new byte[is.available()];
//        //将流读到字节数组中
//        is.read(bytes);
//        //创建HttpHeaders对象设置响应头信息
//        MultiValueMap<String, String> headers = new HttpHeaders();
//        //设置要下载方式以及下载文件的名字
//        headers.add("Content-Disposition", "attachment;filename=activityList.xls");
//        //设置响应状态码
//        HttpStatus statusCode = HttpStatus.OK;
//        //创建ResponseEntity对象
//        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, statusCode);
//        //关闭输入流
//        is.close();
//        return responseEntity;
    }

    @RequestMapping("/workbench/activity/exportSelectedActivity.do")
    public void exportSelectedActivity(HttpServletResponse response, String[] id) throws IOException {
        List<Activity> activityList = activityService.getActivityByIds(id);
        HSSFWorkbook wb = HSSFUtil.getExportFile(activityList);

        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        //流谁创建谁close
        wb.close();
        outputStream.flush();
    }


    @RequestMapping("/workbench/activity/fileUpLoad.do")
    @ResponseBody
    public Object fileUpLoad(MultipartFile multipartFile, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        try {
//            String originalFilename = multipartFile.getOriginalFilename();
//            File file = new File("E:\\idea-workspace\\ssm\\crm\\src\\main\\resources", Objects.requireNonNull(originalFilename));
//            multipartFile.transferTo(file);
//            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream inputStream = multipartFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtil.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtil.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j = 2; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    String cellValueFormStr = HSSFUtil.getCellValueFormStr(cell);
                    switch (j) {
                        case 2:
                            activity.setName(cellValueFormStr);
                            break;
                        case 3:
                            activity.setStartDate(cellValueFormStr);
                            break;
                        case 4:
                            activity.setEndDate(cellValueFormStr);
                            break;
                        case 5:
                            activity.setCost(cellValueFormStr);
                            break;
                        case 6:
                            activity.setDescription(cellValueFormStr);
                            break;
                    }
                }
                activityList.add(activity);
            }
            int res = activityService.saveCreateActivityByList(activityList);
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS, "成功添加" + res + "条数据");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL, "系统忙，请稍后重试...");
        }
    }

    @RequestMapping("/workbench/activity/detail.do")
    public ModelAndView showDetail(String id) {
        ModelAndView modelAndView = new ModelAndView();
        Activity activityById = activityService.getActivityForDetailById(id);
        List<ActivityRemark> activityRemarkList = activityRemarkService.getRemarkByActivityId(id);
        modelAndView.addObject("activity", activityById);
        modelAndView.addObject("remarkList", activityRemarkList);
        modelAndView.setViewName("workbench/activity/detail");
        return modelAndView;
    }

    @RequestMapping("/workbench/activity/addRemark.do")
    @ResponseBody
    public Object addRemark(String noteContent, String activityId, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(UUIDUtil.getUUID());
        activityRemark.setNoteContent(noteContent);
        activityRemark.setCreateTime(DateUtil.formatDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Contants.REMARK_EDIT_FLAG_FALSE);
        activityRemark.setActivityId(activityId);
        try {
            int res = activityRemarkService.addRemark(activityRemark);
            if (res > 0) {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS, null, activityRemark);
            } else {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL,
                        "系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(Contants.RETURN_OBJECT_CODE_FAIL, "系统忙，请稍后重试...");
        }
    }


    @RequestMapping("/workbench/activity/delRemark.do")
    @ResponseBody
    public Object delRemark(String id) {
        try {
            int res = activityRemarkService.deleteRemark(id);
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

    @RequestMapping("/workbench/activity/changeRemark.do")
    @ResponseBody
    public Object changeRemark(ActivityRemark activityRemark, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activityRemark.setEditFlag(Contants.REMARK_EDIT_FLAG_TRUE);
        activityRemark.setEditBy(user.getId());
        activityRemark.setEditTime(DateUtil.formatDateTime(new Date()));
        try {
            int res = activityRemarkService.changeRemark(activityRemark);
            if (res > 0) {
                return new ReturnObject(Contants.RETURN_OBJECT_CODE_SUCCESS, null, activityRemark);
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
}
