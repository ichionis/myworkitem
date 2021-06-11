/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat;

import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.sql.SQLException;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.jbpm.process.workitem.core.util.WidMavenDepends;

@Wid(widfile="MyWorkItemDefinitions.wid", name="MyWorkItemDefinitions",
        displayName="MyWorkItemDefinitions",
        defaultHandler="mvel: new com.redhat.MyWorkItemWorkItemHandler()",
        documentation = "myworkitem/index.html",
        category = "myworkitem",
        icon = "MyWorkItemDefinitions.png",
        parameters={
            @WidParameter(name="SampleParam", required = true),
            @WidParameter(name="SampleParamTwo", required = true)
        },
        results={
            @WidResult(name="mr")
        },
        mavenDepends={
            @WidMavenDepends(group="com.redhat", artifact="myworkitem", version="1.0.0-SNAPSHOT")
        },
        serviceInfo = @WidService(category = "myworkitem", description = "${description}",
                keywords = "",
                action = @WidAction(title = "Sample Title"),
                authinfo = @WidAuth(required = true, params = {"SampleParam", "SampleParamTwo"},
                        paramsdescription = {"SampleParam", "SampleParamTwo"},
                        referencesite = "referenceSiteURL")
        )
)

public class MyWorkItemWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
        private String sampleParam;
        private String sampleParamTwo;
        private String FirstName;
        private String LastName;

    public MyWorkItemWorkItemHandler(String SampleParam, String SampleParamTwo){
            this.sampleParam = sampleParam;
            this.sampleParamTwo = sampleParamTwo;
        }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            String dbUrl = "jdbc:jtds:sqlserver://localhost:1433;databaseName=Drools";
            String dbUsername = "drools_user";
            String dbPassword = "606612Ioannina!!";
            Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement st = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            Map<String, Object> map = new HashMap<String, Object>(0);
            ResultSet rs = st.executeQuery("select FirstName, LastName from dbo.Persons where FirstName = 'Smith'");
            while(rs.next()){
                myRow mr = new myRow(rs.getString(1), rs.getString(2) );
                map.put(rs.getString(1), mr);
            }
          
            RequiredParameterValidator.validate(this.getClass(), workItem);

            // sample parameters
            sampleParam = (String) workItem.getParameter("SampleParam");
            sampleParamTwo = (String) workItem.getParameter("SampleParamTwo");

            // complete workitem impl...

            manager.completeWorkItem(workItem.getId(), map);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


