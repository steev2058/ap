package com.apps2you.albaraka.ui.sep.bill;

import java.util.List;

public class Service {
    private String serviceId;
    private String serviceName;
    private List<BillFragment.BillingNumber> billingNumbers; // New field for billing numbers

    public Service(String serviceId, String serviceName, List<BillFragment.BillingNumber> billingNumbers) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.billingNumbers = billingNumbers;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<BillFragment.BillingNumber> getBillingNumbers() {
        return billingNumbers;
    }
}
