package crm.workbench.service.impl;

import crm.commons.contants.Contants;
import crm.commons.utils.DateUtil;
import crm.commons.utils.UUIDUtil;
import crm.settings.pojo.User;
import crm.workbench.mappers.CustomerMapper;
import crm.workbench.mappers.TranHistoryMapper;
import crm.workbench.mappers.TranMapper;
import crm.workbench.pojo.Customer;
import crm.workbench.pojo.FunnelVO;
import crm.workbench.pojo.Tran;
import crm.workbench.pojo.TranHistory;
import crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("tranService")
public class TranServiceImpl implements TranService {
    @Autowired
    private TranMapper tranMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public void saveCreateTran(Map<String, Object> map) {
        //根据客户名称精确查询客户
        String customerName = (String) map.get("customerName");
        User user = (User) map.get(Contants.SESSION_USER);
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if (customer == null){
            //客户不存在，新建客户
            customer = new Customer();
            customer.setName(customerName);
            customer.setOwner(user.getId());
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateTime(DateUtil.formatDateTime(new Date()));
            customer.setCreateBy(user.getId());
            customerMapper.insertCustomer(customer);
        }
        //保存交易
        Tran tran = new Tran();
        tran.setStage((String) map.get("stage"));
        tran.setOwner((String) map.get("owner"));
        tran.setNextContactTime((String) map.get("nextContactTime"));
        tran.setName((String) map.get("name"));
        tran.setMoney((String) map.get("money"));
        tran.setId(UUIDUtil.getUUID());
        tran.setExpectedDate((String) map.get("expectedDate"));
        tran.setCustomerId(customer.getId());
        tran.setCreateTime(DateUtil.formatDateTime(new Date()));
        tran.setCreateBy(user.getId());
        tran.setContactSummary((String) map.get("contactSummary"));
        tran.setContactsId((String) map.get("contactsId"));
        tran.setActivityId((String) map.get("activityId"));
        tran.setDescription((String) map.get("description"));
        tran.setSource((String) map.get("source"));
        tran.setType((String) map.get("type"));
        tranMapper.insertTran(tran);
        //保存交易的历史
        TranHistory tranHistory =new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(user.getId());
        tranHistory.setCreateTime(DateUtil.formatDateTime(new Date()));
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistoryMapper.insertTranHistory(tranHistory);
    }

    @Override
    public List<Tran> queryTranByCondition(Map<String, Object> map) {
        return tranMapper.selectTranByCondition(map);
    }

    @Override
    public int queryCountOfTranByCondition(Map<String, Object> map) {
        return tranMapper.selectCountOfTranByCondition(map);
    }

    @Override
    public Tran queryTranForDetailById(String tranId) {
        return tranMapper.selectTranForDetailById(tranId);
    }

    @Override
    public List<FunnelVO> queryCountOfTranGroupByStage() {
        return tranMapper.selectCountOfTranGroupByStage();
    }
}
