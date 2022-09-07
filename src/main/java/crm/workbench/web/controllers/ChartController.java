package crm.workbench.web.controllers;

import crm.workbench.pojo.FunnelVO;
import crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChartController {
    @Autowired
    private TranService tranService;

    @RequestMapping("/workbench/chart/transaction/index.do")
    public String index(){
        return "workbench/chart/transaction/index";
    }

    /**
     * 以组为单位查询 交易表中各个阶段的 数量
     * @return
     */
    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    @ResponseBody
    public Object queryCountOfTranGroupByStage(){
        List<FunnelVO> funnelVOList = tranService.queryCountOfTranGroupByStage();
        return funnelVOList;
    }
}
