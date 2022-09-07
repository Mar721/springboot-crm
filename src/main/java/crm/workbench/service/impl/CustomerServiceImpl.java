package crm.workbench.service.impl;

import crm.workbench.mappers.CustomerMapper;
import crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;
    @Override
    public List<String> queryCustomerNameByName(String customerName) {
        return customerMapper.selectCustomerNameByName(customerName);
    }
}
