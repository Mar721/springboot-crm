package crm.workbench.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WorkbenchIndexController {
    @RequestMapping("/workbench/index.do")
    public String workIndex(){
        return "workbench/index";
    }
}
