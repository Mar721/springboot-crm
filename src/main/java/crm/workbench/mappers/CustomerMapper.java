package crm.workbench.mappers;


import crm.workbench.pojo.Customer;

import java.util.List;

public interface CustomerMapper {

    void insertCustomer(Customer customer);

    List<String> selectCustomerNameByName(String customerName);

    Customer selectCustomerByName(String customerName);
}