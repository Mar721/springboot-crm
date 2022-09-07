package crm.workbench.service.impl;

import crm.commons.utils.DateUtil;
import crm.commons.utils.UUIDUtil;
import crm.settings.pojo.User;
import crm.workbench.mappers.*;
import crm.workbench.pojo.*;
import crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("clueService")
public class ClueServiceImpl implements ClueService {
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private TranMapper tranMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ContactsMapper contactsMapper;
    @Autowired
    private ClueRemarkMapper clueRemarkMapper;
    @Autowired
    private TranRemarkMapper tranRemarkMapper;
    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;
    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;
    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;
    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int saveClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clue> queryClueByCondition(Map<String, Object> map) {
        return clueMapper.selectClueByCondition(map);
    }

    @Override
    @Transactional(readOnly = true)
    public Clue queryClueById(String id) {
        return clueMapper.selectClueById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int queryCountOfClueByCondition(Map<String, Object> map) {
        return clueMapper.selectCountOfClueBycondition(map);
    }


    /**
     * 线索转换
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void convertClue(Map<String, Object> map) {
        String clueId = (String) map.get("clueId");
        User user = (User) map.get("user");
        Clue clue = clueMapper.selectClueById(clueId);
//        把线索中有关公司的信息转换到客户表中
        Customer customer = new Customer();
        customer.setId(UUIDUtil.getUUID());
        customer.setOwner(user.getId());
        customer.setName(clue.getCompany());
        customer.setWebsite(clue.getWebsite());
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtil.formatDateTime(new Date()));
        customer.setContactSummary(clue.getContactSummary());
        customer.setNextContactTime(clue.getNextContactTime());
        customer.setPhone(clue.getPhone());
        customer.setDescription(clue.getDescription());
        customer.setAddress(clue.getAddress());
        customerMapper.insertCustomer(customer);
//        把线索中有关个人的信息转换到联系人表中
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setOwner(user.getId());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());//公司id在上面转换时已经生成
        contacts.setFullname(clue.getFullname());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtil.formatDateTime(new Date()));
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setDescription(clue.getDescription());
        contacts.setAddress(clue.getAddress());
        contacts.setContactSummary(clue.getContactSummary());
        contactsMapper.insertContacts(contacts);
        //将线索转客户公海,未做
//        把线索的备注信息转换到客户备注表中一份
//        把线索的备注信息转换到联系人备注表中一份
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        List<ContactsRemark> contactsRemarkList = new ArrayList<>();
        List<CustomerRemark> customerRemarkList = new ArrayList<>();
        CustomerRemark customerRemark;
        ContactsRemark contactsRemark;
        for (ClueRemark clueRemark:clueRemarkList){
            customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(clueRemark.getNoteContent());
            customerRemark.setCreateBy(clueRemark.getCreateBy());
            customerRemark.setCreateTime(clueRemark.getCreateTime());
            customerRemark.setEditBy(clueRemark.getEditBy());
            customerRemark.setEditTime(clueRemark.getEditTime());
            customerRemark.setEditFlag(clueRemark.getEditFlag());
            customerRemark.setCustomerId(customer.getId());
            customerRemarkList.add(customerRemark);

            contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(clueRemark.getNoteContent());
            contactsRemark.setCreateBy(clueRemark.getCreateBy());
            contactsRemark.setCreateTime(clueRemark.getCreateTime());
            contactsRemark.setEditFlag(clueRemark.getEditFlag());
            contactsRemark.setEditBy(clueRemark.getEditBy());
            contactsRemark.setEditTime(clueRemark.getEditTime());
            contactsRemark.setContactsId(contacts.getId());
            contactsRemarkList.add(contactsRemark);
        }
        customerRemarkMapper.insertCustomerRemarkByList(customerRemarkList);
        contactsRemarkMapper.insertContactsRemarkByList(contactsRemarkList);
//        把线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);

        List<ContactsActivityRelation> contactsActivityRelationList = new ArrayList<>();
        ContactsActivityRelation contactsActivityRelation;
        for (ClueActivityRelation car:clueActivityRelationList){
            contactsActivityRelation =new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(car.getActivityId());
            contactsActivityRelation.setContactsId(contacts.getId()); //获取前面联系人的id
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelationList.add(contactsActivityRelation);
        }
        contactsActivityRelationMapper.insertContactsActivityRelationByList(contactsActivityRelationList);

        //        如果需要创建交易,还要往交易表中添加一条记录
        //        如果需要创建交易,还要把线索的备注信息转换到交易备注表中一份
        if ("true".equals(map.get("isCreateTran"))){
            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(contacts.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtil.formatDateTime(new Date()));
            tran.setCustomerId(customer.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtil.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setStage((String) map.get("stage"));
            tran.setOwner(user.getId());
            tranMapper.insertTran(tran);

            TranRemark tranRemark;
            List<TranRemark> tranRemarkList = new ArrayList<>();
            for (ClueRemark clueRemark:clueRemarkList){
                tranRemark = new TranRemark();
                tranRemark.setTranId(tran.getId());
                tranRemark.setCreateBy(clueRemark.getCreateBy());
                tranRemark.setCreateTime(clueRemark.getCreateTime());
                tranRemark.setEditFlag(clueRemark.getEditFlag());
                tranRemark.setEditBy(clueRemark.getEditBy());
                tranRemark.setEditTime(clueRemark.getEditTime());
                tranRemark.setNoteContent(clueRemark.getNoteContent());
                tranRemark.setId(UUIDUtil.getUUID());
                tranRemarkList.add(tranRemark);
            }
            tranRemarkMapper.insertTranRemarkByList(tranRemarkList);
        }

        //删除线索备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);

        //删除线索和市场活动的关联
        clueActivityRelationMapper.deleteClueActivityRelationListByClueId(clueId);

        //删除线索
        clueMapper.deleteClueByClueId(clueId);
    }
}
