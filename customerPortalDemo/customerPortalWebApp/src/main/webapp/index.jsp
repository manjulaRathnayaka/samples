<%
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
  %>

<%@page import="org.wso2.carbon.customer_portal.tag.cloud.sample.Customer" %>
<%@page import="org.wso2.carbon.customer_portal.tag.cloud.sample.CustomerService" %>
<%@page import="java.util.*" %>

<%

    List<Customer> customerList=null;
    CustomerService service = new CustomerService();

    String action = request.getParameter("action");
    if("register".equals(action)){
        String comName = request.getParameter("company_name");
        String email = request.getParameter("email");
        String desc_product = request.getParameter("desc_product");
        String in_products = request.getParameter("in_products");
        try {
            service.registerCustomer(comName,email,desc_product,in_products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    try {
        customerList = service.getCustomerList();
        Collections.reverse(customerList);
    } catch (Exception e) {
        e.printStackTrace();
    }

%>

<html>
<head>
    <title>Customer Portal</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <style>
        h1{
            -webkit-margin-before: 0;
            -webkit-margin-after: 0;
            padding:10px;
            background-color: #3D3333;
            border-bottom: 3px solid rgb(86, 83, 83);
            color:#fff;
        }

        form {
            margin-top: 40px;
        }

        hr {
            margin: 40px -15px;
        }
    </style>
</head>
<body>

<h1 style="text-align: center">Customer Portal</h1>

<div class="container">
    <div class="row">
        <a  target="_blank" href="https://docs.google.com/a/wso2.com/drawings/d/1ImDDJJ6dCsbkb8hfD5WxB9qWcXO4k9qvAiAiV0OpS5g/edit?usp=sharing">Demo Use Case</a>
    </div>

    <form class="form-horizontal" action="${pageContext.request.contextPath}/" method="post">
        <div class="form-group">
            <label for="company_name" class="col-2 col-form-label">Company Name</label>
            <div class="col-10">
                <input class="form-control" id="company_name" name="company_name">
            </div>
        </div>
        <div class="form-group">
            <label for="email" class="col-2 col-form-label">Email</label>
            <div class="col-10">
                <input class="form-control" type="email" id="email" name="email">
            </div>
        </div>
        <div class="form-group">
            <label for="desc_product" class="col-2 col-form-label">Project Description</label>
            <div class="col-10">
                <input class="form-control" type="text" id="desc_product" name="desc_product">
            </div>
        </div>
        <div class="form-group">
            <label for="in_products" class="col-2 col-form-label">Involved Products</label>
            <div class="col-10">
                <input class="form-control" type="text" id="in_products" name="in_products">
            </div>
        </div>
        <div class="form-group">
            <div class="col-10">
                <input type="submit" class="btn btn-primary" id="register" name="register">
            </div>
        </div>
        <input type="hidden" value="register" name="action" />
    </form>

    <hr>

    <div class="row">
        <a  target="_blank" href="https://docs.google.com/spreadsheets/d/1iU3FAETX3zE0xt4Bfw9Z6rFjmDMHQcqhWpwAPk3bTZs/edit?usp=sharing">View Google Sheet For Updated Data</a>
    </div>

    <hr>
    <%
        if(customerList!=null && customerList.size()>0){
    %>

    <div class="row">
        <h2>Data From Enterprise Database</h2>
        <table class="table table-bordered">
           <tr>
               <th class="active">Company Name</th>
               <th class="active">Email</th>
               <th class="active">Product Description</th>
               <th class="active">Involved Products</th>
           </tr>
            <%
                for(Customer customer: customerList){
            %>
            <tr>
                <td class="warning"><%=customer.getCompName()%></td>
                <td class="warning"><%=customer.getEmail()%></td>
                <td class="warning"><%=customer.getProjectDesc()%></td>
                <td class="warning"><%=customer.getProducts()%></td>
            </tr>
            <%
                }
            %>
        </table>
    </div>
    <%
        }
    %>

</div>

<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.1.1.js" integrity="sha256-16cdPddA6VdVInumRGo6IbivbERE8p7CQR3HzTBuELA=" crossorigin="anonymous"></script>
</body>
</html>
