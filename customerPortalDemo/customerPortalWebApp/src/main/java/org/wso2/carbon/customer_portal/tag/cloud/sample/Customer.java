/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.customer_portal.tag.cloud.sample;

public class Customer {

    private  int id;
    private String compName;
    private String email;
    private String projectDesc;
    private String products;

    public Customer(int id, String compName, String email, String projectDesc, String products) {
        this.compName = compName;
        this.email = email;
        this.projectDesc = projectDesc;
        this.products = products;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getCompName() {
        return compName;
    }

    public String getEmail() {
        return email;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public String getProducts() {
        return products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
