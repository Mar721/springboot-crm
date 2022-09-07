package crm.workbench.mappers;

import crm.workbench.pojo.CustomerRemark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerRemarkMapper {

    void insertCustomerRemarkByList(@Param("customerRemarkList") List<CustomerRemark> customerRemarkList);
}