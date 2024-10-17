package com.apps2you.albaraka.ui.sep.bill;

import java.io.Serializable;
import java.util.List;

public class Service implements Serializable {
    private String serviceId;
    private String serviceName;
    private List<BillingNumber> billingNumbers; // New field for billing numbers

    public Service(String serviceId, String serviceName, List<BillingNumber> billingNumbers) {
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

    public List<BillingNumber> getBillingNumbers() {
        return billingNumbers;
    }
}
