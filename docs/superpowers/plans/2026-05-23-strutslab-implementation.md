# StrutsLab — 電力設備巡視点検管理システム 実装計画

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Struts1-based power equipment inspection management system (27 screens, 18 DB tables) as a UI automation test靶场, with classic Japanese SI project style (scriptlet-heavy JSP, table layout, minimal JS, POST/redirect rendering).

**Architecture:** Single Struts1 module with Maven WAR project. Presentation: Struts 1.3 + Tiles + custom tags + JSP scriptlets. Business: Action/DispatchAction classes. Data: MyBatis 3.5 + H2 file-mode. Validation: Validator plugin. Build: Maven 3.x → Tomcat 8.5. Java 8. All UI labels in Japanese.

**Tech Stack:** Java 8, Struts 1.3.10, MyBatis 3.5.x, H2 1.4.200, Tomcat 8.5, Maven 3.x, Tiles 1.3, Validator plugin

**Design Docs:** `docs/design-docs/` (16 Excel + 16 Markdown design documents)

---

## File Structure Map

```
StrutsLab/
├── pom.xml
├── src/main/java/com/struts-lab/
│   ├── action/
│   │   ├── common/     LoginAction, MenuAction, LogoutAction
│   │   ├── mst/         EqpListAction, EqpSaveAction, CheckItemListAction, CheckItemSaveAction
│   │   ├── ins/         YearlyPlanAction, PlanWizardAction, DailyListAction, ExecInputAction,
│   │   │                ExecDetailAction, ApprovalListAction
│   │   ├── inc/         IncidentListAction, IncidentCreateAction, IncidentDetailAction
│   │   ├── counter/     CounterCreateAction, CounterListAction, CounterDetailAction, CapaAction
│   │   ├── org/         DeptListAction, DeptSaveAction, EmpListAction, EmpSaveAction
│   │   ├── cal/         CalendarListAction, CalendarSaveAction
│   │   ├── parts/       PartsListAction, PartsSaveAction, PartsUsageAction
│   │   └── report/      SummaryReportAction
│   ├── form/
│   │   ├── common/     LoginForm
│   │   ├── mst/         EqpSearchForm, EqpForm, CheckItemSearchForm, CheckItemForm
│   │   ├── ins/         YearlyPlanForm, PlanWizardForm, DailyForm, ExecForm, ApprovalForm
│   │   ├── inc/         IncidentSearchForm, IncidentForm
│   │   ├── counter/     CounterForm, CounterSearchForm, CounterDetailForm, CapaForm
│   │   ├── org/         DeptSearchForm, DeptForm, EmpSearchForm, EmpForm
│   │   ├── cal/         CalendarForm, CalendarRegForm
│   │   ├── parts/       PartsSearchForm, PartsForm, PartsUsageSearchForm
│   │   └── report/      ReportForm
│   ├── dto/             EqpDto, ChkTmplDto, PlanDto, ExecResultDto, IncidentDto, CounterDto, etc.
│   ├── service/         EqpService, InspectionService, IncidentService, CounterService, etc.
│   ├── dao/             EqpDao, ChkItemDao, PlanDao, ExecDao, IncidentDao, CounterDao, etc.
│   └── taglib/          EqpTreeSelectTag, DatePickerTag, SectionHeaderTag, StatusBadgeTag,
│                        InspectionChecklistTag, IndexedRowTag, TimelineTag
├── src/main/resources/
│   ├── com/struts-lab/dao/   # MyBatis mapper XMLs
│   ├── struts-config.xml
│   ├── tiles-defs.xml
│   ├── validation.xml
│   ├── validation-rules.xml
│   ├── mybatis-config.xml
│   └── ApplicationResources_ja.properties
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   ├── validation.xml -> resources symlink
│   │   ├── validation-rules.xml -> resources symlink
│   │   ├── tld/          eqp-tree.tld, date-picker.tld, app-common.tld
│   │   └── jsp/
│   │       ├── common/   header.jsp, menu.jsp, footer.jsp, paging.jsp, errorMessages.jsp, confirmDialog.jsp
│   │       ├── tiles/    baseLayout.jsp, masterLayout.jsp, wizardLayout.jsp, listLayout.jsp, inputLayout.jsp,
│   │       │             popupLayout.jsp, printLayout.jsp
│   │       ├── login.jsp
│   │       ├── menu.jsp
│   │       ├── mst/      eqpList.jsp, eqpEdit.jsp, chkItemList.jsp, chkItemEdit.jsp
│   │       ├── ins/      yearlyPlan.jsp, planWiz1.jsp, planWiz2.jsp, planWiz3.jsp, planConfirm.jsp,
│   │       │             dailyList.jsp, execInput.jsp, execDetail.jsp, approvalList.jsp
│   │       ├── inc/      incList.jsp, incCreate.jsp, incDetail.jsp
│   │       ├── counter/  ctrCreate.jsp, ctrList.jsp, ctrDetail.jsp, capaCreate.jsp
│   │       ├── org/      deptList.jsp, deptEdit.jsp, empList.jsp, empEdit.jsp
│   │       ├── cal/      calList.jsp, calEdit.jsp
│   │       ├── parts/    prtList.jsp, prtEdit.jsp, prtUsage.jsp
│   │       └── report/   summaryReport.jsp
│   ├── js/               (minimal: confirmDialog only)
│   └── css/              style.css (minimal: table borders, badge colors, calendar colors)
└── src/main/db/
    ├── schema.sql
    └── seed.sql
```

---

### Task 0: Project Scaffolding (Maven + Struts1)

**Files:**
- Create: `pom.xml`
- Create: `src/main/webapp/WEB-INF/web.xml`
- Create: `src/main/resources/struts-config.xml`
- Create: `src/main/resources/tiles-defs.xml`
- Create: `src/main/resources/validation-rules.xml`
- Create: `src/main/resources/validation.xml`
- Create: `src/main/resources/mybatis-config.xml`
- Create: `src/main/resources/ApplicationResources_ja.properties`

- [ ] **Step 1: Create pom.xml with all dependencies**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.struts-lab</groupId>
    <artifactId>StrutsLab</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>StrutsLab - 電力設備巡視点検管理システム</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <struts.version>1.3.10</struts.version>
        <mybatis.version>3.5.6</mybatis.version>
    </properties>

    <dependencies>
        <!-- Servlet API -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>javax.servlet.jsp-api</artifactId>
            <version>2.3.1</version>
            <scope>provided</scope>
        </dependency>

        <!-- Struts 1.3 -->
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts-core</artifactId>
            <version>${struts.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts-taglib</artifactId>
            <version>${struts.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts-tiles</artifactId>
            <version>${struts.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts-extras</artifactId>
            <version>${struts.version}</version>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>${mybatis.version}</version>
        </dependency>

        <!-- H2 Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.200</version>
        </dependency>

        <!-- Apache Commons (Struts1 dependencies) -->
        <dependency>
            <groupId>commons-beanutils</groupId>
            <artifactId>commons-beanutils</artifactId>
            <version>1.9.4</version>
        </dependency>
        <dependency>
            <groupId>commons-digester</groupId>
            <artifactId>commons-digester</artifactId>
            <version>2.1</version>
        </dependency>
        <dependency>
            <groupId>commons-validator</groupId>
            <artifactId>commons-validator</artifactId>
            <version>1.7</version>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>commons-chain</groupId>
            <artifactId>commons-chain</artifactId>
            <version>1.2</version>
        </dependency>

        <!-- JSTL -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>StrutsLab</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.2.3</version>
            </plugin>
            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <port>8080</port>
                    <path>/StrutsLab</path>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create web.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <display-name>StrutsLab - 電力設備巡視点検管理システム</display-name>

    <!-- ActionServlet -->
    <servlet>
        <servlet-name>action</servlet-name>
        <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
        <init-param>
            <param-name>config</param-name>
            <param-value>/WEB-INF/struts-config.xml</param-value>
        </init-param>
        <init-param>
            <param-name>validate</param-name>
            <param-value>true</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>action</servlet-name>
        <url-pattern>*.do</url-pattern>
    </servlet-mapping>

    <!-- Tiles Servlet -->
    <servlet>
        <servlet-name>tiles</servlet-name>
        <servlet-class>org.apache.struts.tiles.TilesServlet</servlet-class>
        <init-param>
            <param-name>definitions-config</param-name>
            <param-value>/WEB-INF/tiles-defs.xml</param-value>
        </init-param>
        <load-on-startup>2</load-on-startup>
    </servlet>

    <!-- Welcome File -->
    <welcome-file-list>
        <welcome-file>/login.do</welcome-file>
    </welcome-file-list>

    <!-- Session timeout: 30min -->
    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>

    <!-- Japanese resource encoding -->
    <jsp-config>
        <jsp-property-group>
            <url-pattern>*.jsp</url-pattern>
            <page-encoding>UTF-8</page-encoding>
        </jsp-property-group>
    </jsp-config>
</web-app>
```

- [ ] **Step 3: Create struts-config.xml skeleton**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
  "-//Apache Software Foundation//DTD Struts Configuration 1.3//EN"
  "http://struts.apache.org/dtds/struts-config_1_3.dtd">
<struts-config>
    <!-- Form Beans -->
    <form-beans>
        <!-- Login -->
        <form-bean name="loginForm" type="com.struts-lab.form.common.LoginForm"/>

        <!-- Master: Equipment -->
        <form-bean name="eqpSearchForm" type="com.struts-lab.form.mst.EqpSearchForm"/>
        <form-bean name="eqpForm" type="com.struts-lab.form.mst.EqpForm"/>

        <!-- Master: Check Items -->
        <form-bean name="chkItemSearchForm" type="com.struts-lab.form.mst.CheckItemSearchForm"/>
        <form-bean name="chkItemForm" type="com.struts-lab.form.mst.CheckItemForm"/>

        <!-- Inspection -->
        <form-bean name="yearlyPlanForm" type="com.struts-lab.form.ins.YearlyPlanForm"/>
        <form-bean name="planWizardForm" type="com.struts-lab.form.ins.PlanWizardForm"/>
        <form-bean name="dailyForm" type="com.struts-lab.form.ins.DailyForm"/>
        <form-bean name="execForm" type="com.struts-lab.form.ins.ExecForm"/>
        <form-bean name="approvalForm" type="com.struts-lab.form.ins.ApprovalForm"/>

        <!-- Incident -->
        <form-bean name="incidentSearchForm" type="com.struts-lab.form.inc.IncidentSearchForm"/>
        <form-bean name="incidentForm" type="com.struts-lab.form.inc.IncidentForm"/>

        <!-- Counter -->
        <form-bean name="counterSearchForm" type="com.struts-lab.form.counter.CounterSearchForm"/>
        <form-bean name="counterForm" type="com.struts-lab.form.counter.CounterForm"/>
        <form-bean name="counterDetailForm" type="com.struts-lab.form.counter.CounterDetailForm"/>
        <form-bean name="capaForm" type="com.struts-lab.form.counter.CapaForm"/>

        <!-- Organization -->
        <form-bean name="deptSearchForm" type="com.struts-lab.form.org.DeptSearchForm"/>
        <form-bean name="deptForm" type="com.struts-lab.form.org.DeptForm"/>
        <form-bean name="empSearchForm" type="com.struts-lab.form.org.EmpSearchForm"/>
        <form-bean name="empForm" type="com.struts-lab.form.org.EmpForm"/>

        <!-- Calendar -->
        <form-bean name="calendarForm" type="com.struts-lab.form.cal.CalendarForm"/>
        <form-bean name="calendarRegForm" type="com.struts-lab.form.cal.CalendarRegForm"/>

        <!-- Parts -->
        <form-bean name="partsSearchForm" type="com.struts-lab.form.parts.PartsSearchForm"/>
        <form-bean name="partsForm" type="com.struts-lab.form.parts.PartsForm"/>
        <form-bean name="partsUsageSearchForm" type="com.struts-lab.form.parts.PartsUsageSearchForm"/>

        <!-- Report -->
        <form-bean name="reportForm" type="com.struts-lab.form.report.ReportForm"/>
    </form-beans>

    <!-- Global Forwards -->
    <global-forwards>
        <forward name="login" path="/login.do" redirect="true"/>
        <forward name="menu" path="/WEB-INF/jsp/menu.jsp"/>
        <forward name="error" path="/WEB-INF/jsp/common/error.jsp"/>
    </global-forwards>

    <!-- Action Mappings (filled per-module) -->
    <action-mappings>
        <!-- Common -->
        <action path="/login" type="com.struts-lab.action.common.LoginAction"
                name="loginForm" scope="request" validate="false">
            <forward name="success" path="/WEB-INF/jsp/menu.jsp"/>
            <forward name="input" path="/WEB-INF/jsp/login.jsp"/>
        </action>
        <action path="/logout" type="com.struts-lab.action.common.LogoutAction">
            <forward name="success" path="/WEB-INF/jsp/login.jsp"/>
        </action>

        <!-- Master: Equipment -->
        <action path="/mst/eqp/list" type="com.struts-lab.action.mst.EqpListAction"
                name="eqpSearchForm" scope="session" validate="false">
            <forward name="success" path="eqp.list.tiles"/>
            <forward name="edit" path="/mst/eqp/edit.do"/>
            <forward name="new" path="/mst/eqp/edit.do?method=new"/>
        </action>
        <action path="/mst/eqp/save" type="com.struts-lab.action.mst.EqpSaveAction"
                name="eqpForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/mst/eqpEdit.jsp">
            <forward name="success" path="/mst/eqp/list.do" redirect="true"/>
            <forward name="input" path="eqp.edit.tiles"/>
        </action>
        <action path="/mst/eqp/edit" type="com.struts-lab.action.mst.EqpSaveAction"
                name="eqpForm" scope="request" validate="false"
                parameter="method">
            <forward name="success" path="eqp.edit.tiles"/>
        </action>

        <!-- Master: Check Items -->
        <action path="/mst/chkitem/list" type="com.struts-lab.action.mst.CheckItemListAction"
                name="chkItemSearchForm" scope="session" validate="false">
            <forward name="success" path="chkitem.list.tiles"/>
            <forward name="edit" path="chkitem.edit.tiles"/>
        </action>
        <action path="/mst/chkitem/save" type="com.struts-lab.action.mst.CheckItemSaveAction"
                name="chkItemForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/mst/chkItemEdit.jsp">
            <forward name="success" path="/mst/chkitem/list.do" redirect="true"/>
            <forward name="input" path="chkitem.edit.tiles"/>
        </action>

        <!-- Inspection: Yearly Plan -->
        <action path="/ins/plan/yearly" type="com.struts-lab.action.ins.YearlyPlanAction"
                name="yearlyPlanForm" scope="request" validate="false">
            <forward name="success" path="ins.plan.yearly.tiles"/>
        </action>

        <!-- Inspection: Plan Wizard 4 steps -->
        <action path="/ins/plan/wizard" type="com.struts-lab.action.ins.PlanWizardAction"
                name="planWizardForm" scope="session" validate="true"
                parameter="step" input="/WEB-INF/jsp/ins/planWiz1.jsp">
            <forward name="step1" path="ins.plan.wiz1.tiles"/>
            <forward name="step2" path="ins.plan.wiz2.tiles"/>
            <forward name="step3" path="ins.plan.wiz3.tiles"/>
            <forward name="confirm" path="ins.plan.confirm.tiles"/>
            <forward name="success" path="/ins/plan/yearly.do" redirect="true"/>
        </action>

        <!-- Inspection: Daily List -->
        <action path="/ins/daily" type="com.struts-lab.action.ins.DailyListAction"
                name="dailyForm" scope="request" validate="false">
            <forward name="success" path="ins.daily.list.tiles"/>
            <forward name="exec" path="/ins/exec/input.do"/>
        </action>

        <!-- Inspection: Exec Input -->
        <action path="/ins/exec/input" type="com.struts-lab.action.ins.ExecInputAction"
                name="execForm" scope="request" validate="true"
                input="/WEB-INF/jsp/ins/execInput.jsp">
            <forward name="success" path="/ins/daily.do" redirect="true"/>
            <forward name="input" path="ins.exec.input.tiles"/>
            <forward name="incident" path="/inc/create.do"/>
        </action>

        <!-- Inspection: Exec Detail/Modify -->
        <action path="/ins/exec/detail" type="com.struts-lab.action.ins.ExecDetailAction"
                name="execForm" scope="request" validate="true"
                input="/WEB-INF/jsp/ins/execDetail.jsp">
            <forward name="success" path="ins.exec.detail.tiles"/>
            <forward name="modify" path="ins.exec.detail.tiles"/>
        </action>

        <!-- Inspection: Approval List -->
        <action path="/ins/approval/list" type="com.struts-lab.action.ins.ApprovalListAction"
                name="approvalForm" scope="request" validate="false">
            <forward name="success" path="ins.appr.list.tiles"/>
        </action>

        <!-- Incident -->
        <action path="/inc/list" type="com.struts-lab.action.inc.IncidentListAction"
                name="incidentSearchForm" scope="session" validate="false">
            <forward name="success" path="inc.list.tiles"/>
        </action>
        <action path="/inc/create" type="com.struts-lab.action.inc.IncidentCreateAction"
                name="incidentForm" scope="request" validate="true"
                input="/WEB-INF/jsp/inc/incCreate.jsp">
            <forward name="success" path="/inc/list.do" redirect="true"/>
            <forward name="input" path="inc.create.tiles"/>
            <forward name="similar" path="inc.create.tiles"/>
        </action>
        <action path="/inc/detail" type="com.struts-lab.action.inc.IncidentDetailAction"
                name="incidentForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/inc/incDetail.jsp">
            <forward name="success" path="inc.detail.tiles"/>
            <forward name="investigate" path="inc.detail.tiles"/>
            <forward name="counter" path="inc.detail.tiles"/>
            <forward name="complete" path="inc.detail.tiles"/>
            <forward name="capa" path="/counter/capa/create.do"/>
        </action>

        <!-- Counter -->
        <action path="/counter/create" type="com.struts-lab.action.counter.CounterCreateAction"
                name="counterForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/counter/ctrCreate.jsp">
            <forward name="success" path="/counter/list.do" redirect="true"/>
            <forward name="input" path="ctr.create.tiles"/>
            <forward name="addRow" path="ctr.create.tiles"/>
            <forward name="delRow" path="ctr.create.tiles"/>
        </action>
        <action path="/counter/list" type="com.struts-lab.action.counter.CounterListAction"
                name="counterSearchForm" scope="session" validate="false">
            <forward name="success" path="ctr.list.tiles"/>
        </action>
        <action path="/counter/detail" type="com.struts-lab.action.counter.CounterDetailAction"
                name="counterDetailForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/counter/ctrDetail.jsp">
            <forward name="success" path="/counter/list.do" redirect="true"/>
            <forward name="input" path="ctr.detail.tiles"/>
            <forward name="complete" path="ctr.detail.tiles"/>
        </action>
        <action path="/counter/capa/create" type="com.struts-lab.action.counter.CapaAction"
                name="capaForm" scope="request" validate="true"
                input="/WEB-INF/jsp/counter/capaCreate.jsp">
            <forward name="success" path="/counter/list.do" redirect="true"/>
            <forward name="input" path="ctr.capa.tiles"/>
        </action>

        <!-- Organization -->
        <action path="/org/dept/list" type="com.struts-lab.action.org.DeptListAction"
                name="deptSearchForm" scope="session" validate="false">
            <forward name="success" path="org.dept.list.tiles"/>
            <forward name="edit" path="org.dept.edit.tiles"/>
        </action>
        <action path="/org/dept/save" type="com.struts-lab.action.org.DeptSaveAction"
                name="deptForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/org/deptEdit.jsp">
            <forward name="success" path="/org/dept/list.do" redirect="true"/>
            <forward name="input" path="org.dept.edit.tiles"/>
        </action>
        <action path="/org/emp/list" type="com.struts-lab.action.org.EmpListAction"
                name="empSearchForm" scope="session" validate="false">
            <forward name="success" path="org.emp.list.tiles"/>
            <forward name="edit" path="org.emp.edit.tiles"/>
        </action>
        <action path="/org/emp/save" type="com.struts-lab.action.org.EmpSaveAction"
                name="empForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/org/empEdit.jsp">
            <forward name="success" path="/org/emp/list.do" redirect="true"/>
            <forward name="input" path="org.emp.edit.tiles"/>
        </action>

        <!-- Calendar -->
        <action path="/cal/list" type="com.struts-lab.action.cal.CalendarListAction"
                name="calendarForm" scope="request" validate="false">
            <forward name="success" path="cal.list.tiles"/>
            <forward name="edit" path="cal.edit.tiles"/>
        </action>
        <action path="/cal/save" type="com.struts-lab.action.cal.CalendarSaveAction"
                name="calendarRegForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/cal/calEdit.jsp">
            <forward name="success" path="/cal/list.do" redirect="true"/>
            <forward name="input" path="cal.edit.tiles"/>
        </action>

        <!-- Parts -->
        <action path="/parts/list" type="com.struts-lab.action.parts.PartsListAction"
                name="partsSearchForm" scope="session" validate="false">
            <forward name="success" path="parts.list.tiles"/>
            <forward name="edit" path="parts.edit.tiles"/>
        </action>
        <action path="/parts/save" type="com.struts-lab.action.parts.PartsSaveAction"
                name="partsForm" scope="request" validate="true"
                parameter="method" input="/WEB-INF/jsp/parts/prtEdit.jsp">
            <forward name="success" path="/parts/list.do" redirect="true"/>
            <forward name="input" path="parts.edit.tiles"/>
        </action>
        <action path="/parts/usage" type="com.struts-lab.action.parts.PartsUsageAction"
                name="partsUsageSearchForm" scope="request" validate="false">
            <forward name="success" path="parts.usage.tiles"/>
        </action>

        <!-- Report -->
        <action path="/report/summary" type="com.struts-lab.action.report.SummaryReportAction"
                name="reportForm" scope="request" validate="false">
            <forward name="success" path="report.summary.tiles"/>
        </action>
    </action-mappings>

    <!-- Message Resources -->
    <message-resources parameter="ApplicationResources_ja"/>

    <!-- Plugins -->
    <plug-in className="org.apache.struts.validator.ValidatorPlugIn">
        <set-property property="pathnames"
            value="/WEB-INF/validator-rules.xml,/WEB-INF/validation.xml"/>
    </plug-in>
    <plug-in className="org.apache.struts.tiles.TilesPlugin">
        <set-property property="definitions-config" value="/WEB-INF/tiles-defs.xml"/>
        <set-property property="moduleAware" value="true"/>
    </plug-in>
</struts-config>
```

- [ ] **Step 4: Create tiles-defs.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tiles-definitions PUBLIC
  "-//Apache Software Foundation//DTD Tiles Configuration 1.3//EN"
  "http://struts.apache.org/dtds/tiles-config_1_3.dtd">
<tiles-definitions>
    <!-- Base -->
    <definition name=".base" path="/WEB-INF/jsp/tiles/baseLayout.jsp">
        <put name="title" value="電力設備巡視点検管理システム"/>
        <put name="header" value="/WEB-INF/jsp/common/header.jsp"/>
        <put name="menu" value="/WEB-INF/jsp/common/menu.jsp"/>
        <put name="body" value=""/>
        <put name="footer" value="/WEB-INF/jsp/common/footer.jsp"/>
    </definition>

    <!-- List Layout -->
    <definition name=".list" extends=".base">
        <put name="body" value="/WEB-INF/jsp/tiles/listLayout.jsp"/>
    </definition>

    <!-- Input Layout -->
    <definition name=".input" extends=".base">
        <put name="body" value="/WEB-INF/jsp/tiles/inputLayout.jsp"/>
    </definition>

    <!-- Wizard Layout -->
    <definition name=".wizard" extends=".base">
        <put name="body" value="/WEB-INF/jsp/tiles/wizardLayout.jsp"/>
    </definition>

    <!-- Popup Layout -->
    <definition name=".popup" path="/WEB-INF/jsp/tiles/popupLayout.jsp">
        <put name="title" value="選択"/>
        <put name="header" value="/WEB-INF/jsp/common/header.jsp"/>
        <put name="body" value=""/>
    </definition>

    <!-- Print Layout -->
    <definition name=".print" path="/WEB-INF/jsp/tiles/printLayout.jsp">
        <put name="body" value=""/>
    </definition>

    <!-- === Master: Equipment === -->
    <definition name="eqp.list.tiles" extends=".list">
        <put name="title" value="設備マスタ一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/mst/eqpListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/mst/eqpListTable.jsp"/>
    </definition>
    <definition name="eqp.edit.tiles" extends=".input">
        <put name="title" value="設備マスタ登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/mst/eqpEdit.jsp"/>
    </definition>

    <!-- === Master: Check Items === -->
    <definition name="chkitem.list.tiles" extends=".list">
        <put name="title" value="点検項目マスタ一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/mst/chkItemListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/mst/chkItemListTable.jsp"/>
    </definition>
    <definition name="chkitem.edit.tiles" extends=".input">
        <put name="title" value="点検項目マスタ登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/mst/chkItemEdit.jsp"/>
    </definition>

    <!-- === Inspection === -->
    <definition name="ins.plan.yearly.tiles" extends=".list">
        <put name="title" value="年間点検計画一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/ins/yearlyPlanSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/ins/yearlyPlanMatrix.jsp"/>
    </definition>
    <definition name="ins.plan.wiz1.tiles" extends=".wizard">
        <put name="title" value="点検計画登録 ①設備選択"/>
        <put name="wizardContent" value="/WEB-INF/jsp/ins/planWiz1.jsp"/>
    </definition>
    <definition name="ins.plan.wiz2.tiles" extends=".wizard">
        <put name="title" value="点検計画登録 ②テンプレート選択"/>
        <put name="wizardContent" value="/WEB-INF/jsp/ins/planWiz2.jsp"/>
    </definition>
    <definition name="ins.plan.wiz3.tiles" extends=".wizard">
        <put name="title" value="点検計画登録 ③日程・担当者"/>
        <put name="wizardContent" value="/WEB-INF/jsp/ins/planWiz3.jsp"/>
    </definition>
    <definition name="ins.plan.confirm.tiles" extends=".wizard">
        <put name="title" value="点検計画登録 ④確認"/>
        <put name="wizardContent" value="/WEB-INF/jsp/ins/planConfirm.jsp"/>
    </definition>
    <definition name="ins.daily.list.tiles" extends=".list">
        <put name="title" value="点検実施一覧（当日）"/>
        <put name="searchArea" value="/WEB-INF/jsp/ins/dailyListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/ins/dailyListTable.jsp"/>
    </definition>
    <definition name="ins.exec.input.tiles" extends=".input">
        <put name="title" value="点検実施入力"/>
        <put name="formArea" value="/WEB-INF/jsp/ins/execInput.jsp"/>
    </definition>
    <definition name="ins.exec.detail.tiles" extends=".input">
        <put name="title" value="点検実施詳細"/>
        <put name="formArea" value="/WEB-INF/jsp/ins/execDetail.jsp"/>
    </definition>
    <definition name="ins.appr.list.tiles" extends=".list">
        <put name="title" value="点検実施承認一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/ins/approvalListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/ins/approvalListTable.jsp"/>
    </definition>

    <!-- === Incident === -->
    <definition name="inc.list.tiles" extends=".list">
        <put name="title" value="異常報告一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/inc/incListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/inc/incListTable.jsp"/>
    </definition>
    <definition name="inc.create.tiles" extends=".input">
        <put name="title" value="異常報告登録"/>
        <put name="formArea" value="/WEB-INF/jsp/inc/incCreate.jsp"/>
    </definition>
    <definition name="inc.detail.tiles" extends=".input">
        <put name="title" value="異常報告詳細"/>
        <put name="formArea" value="/WEB-INF/jsp/inc/incDetail.jsp"/>
    </definition>

    <!-- === Counter === -->
    <definition name="ctr.create.tiles" extends=".input">
        <put name="title" value="対応指示登録"/>
        <put name="formArea" value="/WEB-INF/jsp/counter/ctrCreate.jsp"/>
    </definition>
    <definition name="ctr.list.tiles" extends=".list">
        <put name="title" value="対応指示一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/counter/ctrListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/counter/ctrListTable.jsp"/>
    </definition>
    <definition name="ctr.detail.tiles" extends=".input">
        <put name="title" value="対応指示詳細"/>
        <put name="formArea" value="/WEB-INF/jsp/counter/ctrDetail.jsp"/>
    </definition>
    <definition name="ctr.capa.tiles" extends=".input">
        <put name="title" value="是正処置報告書"/>
        <put name="formArea" value="/WEB-INF/jsp/counter/capaCreate.jsp"/>
    </definition>

    <!-- === Organization === -->
    <definition name="org.dept.list.tiles" extends=".list">
        <put name="title" value="部署マスタ一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/org/deptListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/org/deptListTree.jsp"/>
    </definition>
    <definition name="org.dept.edit.tiles" extends=".input">
        <put name="title" value="部署マスタ登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/org/deptEdit.jsp"/>
    </definition>
    <definition name="org.emp.list.tiles" extends=".list">
        <put name="title" value="担当者マスタ一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/org/empListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/org/empListTable.jsp"/>
    </definition>
    <definition name="org.emp.edit.tiles" extends=".input">
        <put name="title" value="担当者マスタ登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/org/empEdit.jsp"/>
    </definition>

    <!-- === Calendar === -->
    <definition name="cal.list.tiles" extends=".input">
        <put name="title" value="休日カレンダー"/>
        <put name="formArea" value="/WEB-INF/jsp/cal/calList.jsp"/>
    </definition>
    <definition name="cal.edit.tiles" extends=".input">
        <put name="title" value="休日登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/cal/calEdit.jsp"/>
    </definition>

    <!-- === Parts === -->
    <definition name="parts.list.tiles" extends=".list">
        <put name="title" value="保守部品一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/parts/prtListSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/parts/prtListTable.jsp"/>
    </definition>
    <definition name="parts.edit.tiles" extends=".input">
        <put name="title" value="保守部品登録・編集"/>
        <put name="formArea" value="/WEB-INF/jsp/parts/prtEdit.jsp"/>
    </definition>
    <definition name="parts.usage.tiles" extends=".list">
        <put name="title" value="部品使用実績一覧"/>
        <put name="searchArea" value="/WEB-INF/jsp/parts/prtUsageSearch.jsp"/>
        <put name="listArea" value="/WEB-INF/jsp/parts/prtUsageTable.jsp"/>
    </definition>

    <!-- === Report === -->
    <definition name="report.summary.tiles" extends=".input">
        <put name="title" value="総合レポート"/>
        <put name="formArea" value="/WEB-INF/jsp/report/summaryReport.jsp"/>
    </definition>
</tiles-definitions>
```

- [ ] **Step 5: Create validation-rules.xml (standard Struts rules)**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE form-validation PUBLIC
  "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN"
  "http://jakarta.apache.org/commons/dtds/validator_1_3_0.dtd">
<form-validation>
    <global>
        <validator name="required"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateRequired"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.required"/>
        <validator name="maxlength"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateMaxLength"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.maxlength"/>
        <validator name="minlength"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateMinLength"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.minlength"/>
        <validator name="intRange"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateIntRange"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.range"/>
        <validator name="date"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateDate"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.date"/>
        <validator name="mask"
            classname="org.apache.struts.validator.FieldChecks"
            method="validateMask"
            methodParams="java.lang.Object,org.apache.commons.validator.ValidatorAction,
                org.apache.commons.validator.Field,org.apache.struts.action.ActionMessages,
                org.apache.commons.validator.Validator,javax.servlet.http.HttpServletRequest"
            msg="errors.mask"/>
    </global>
</form-validation>
```

- [ ] **Step 6: Create validation.xml skeleton**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE form-validation PUBLIC
  "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN"
  "http://jakarta.apache.org/commons/dtds/validator_1_3_0.dtd">
<form-validation>
    <formset>
        <!-- Validation rules filled per-module -->
    </formset>
</form-validation>
```

- [ ] **Step 7: Create mybatis-config.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC
  "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
    <typeAliases>
        <package name="com.struts-lab.dto"/>
    </typeAliases>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="org.h2.Driver"/>
                <property name="url" value="jdbc:h2:file:./struts-lab-db;AUTO_SERVER=TRUE"/>
                <property name="username" value="sa"/>
                <property name="password" value=""/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com/struts-lab/dao/EquipmentMapper.xml"/>
        <mapper resource="com/struts-lab/dao/InspectionTemplateMapper.xml"/>
        <mapper resource="com/struts-lab/dao/InspectionPlanMapper.xml"/>
        <mapper resource="com/struts-lab/dao/InspectionResultMapper.xml"/>
        <mapper resource="com/struts-lab/dao/IncidentMapper.xml"/>
        <mapper resource="com/struts-lab/dao/CounterOrderMapper.xml"/>
        <mapper resource="com/struts-lab/dao/CapaMapper.xml"/>
        <mapper resource="com/struts-lab/dao/DeptMapper.xml"/>
        <mapper resource="com/struts-lab/dao/EmployeeMapper.xml"/>
        <mapper resource="com/struts-lab/dao/CalendarMapper.xml"/>
        <mapper resource="com/struts-lab/dao/PartsMapper.xml"/>
    </mappers>
</configuration>
```

- [ ] **Step 8: Create ApplicationResources_ja.properties**

```properties
# Common
app.title=電力設備巡視点検管理システム
app.login=ログイン
app.logout=ログアウト
app.menu=メニュー

# Errors
errors.required={0}は必須です。
errors.maxlength={0}は{1}文字以内で入力してください。
errors.minlength={0}は{1}文字以上で入力してください。
errors.range={0}は{1}～{2}の範囲で入力してください。
errors.date={0}は日付形式で入力してください。
errors.mask={0}の形式が正しくありません。

# Validation
errors.kana=フリガナはカタカナで入力してください。
errors.password.match=パスワードが一致しません。

# Labels
label.search=検索
label.clear=クリア
label.register=登録
label.update=更新
label.delete=削除
label.back=戻る
label.save=保存
label.cancel=キャンセル
label.csv=CSV出力
label.print=印刷
label.confirm=確認
label.next=進む
label.prev=戻る
```

- [ ] **Step 9: Build and verify project compiles**

```bash
mvn clean compile -q
```

Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```bash
git init
git add pom.xml src/main/resources/ src/main/webapp/WEB-INF/web.xml src/main/webapp/WEB-INF/*.xml
git commit -m "feat: project scaffolding — Maven + Struts1 + Tiles + MyBatis + H2"
```

---

### Task 1: Database Schema + MyBatis Session Factory

**Files:**
- Create: `src/main/db/schema.sql`
- Create: `src/main/db/seed.sql`
- Create: `src/main/java/com/struts-lab/db/MyBatisUtil.java`
- Create: `src/main/java/com/struts-lab/dto/EqpDto.java`
- Create: `src/main/java/com/struts-lab/dao/EqpDao.java`
- Create: `src/main/resources/com/struts-lab/dao/EquipmentMapper.xml`

- [ ] **Step 1: Create schema.sql with all 18 tables**

```sql
-- Equipment Master
CREATE TABLE equipment (
    equipment_code VARCHAR(10) PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(10) NOT NULL,
    voltage_level VARCHAR(10),
    rated_capacity INT,
    rated_current INT,
    frequency VARCHAR(5),
    parent_equipment_code VARCHAR(10),
    install_date VARCHAR(6),
    location_address VARCHAR(200),
    coordinates VARCHAR(50),
    maintenance_rank CHAR(1) NOT NULL,
    inspection_interval INT,
    last_inspection_date VARCHAR(8),
    next_inspection_date VARCHAR(8),
    status VARCHAR(5) NOT NULL DEFAULT '運用中',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Template
CREATE TABLE inspection_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(10) NOT NULL,
    inspection_kind VARCHAR(5) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Items (3-level hierarchy)
CREATE TABLE inspection_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT NOT NULL,
    parent_item_id INT,
    item_level INT NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    judge_criteria VARCHAR(5),
    normal_range VARCHAR(100),
    unit VARCHAR(20),
    sort_order INT DEFAULT 0
);

-- Inspection Plans
CREATE TABLE inspection_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    fiscal_year VARCHAR(4) NOT NULL,
    equipment_code VARCHAR(10) NOT NULL,
    template_id INT,
    planned_date VARCHAR(8) NOT NULL,
    team_code VARCHAR(10),
    person_code VARCHAR(10),
    status VARCHAR(10) NOT NULL DEFAULT '予定',
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Results (header)
CREATE TABLE inspection_results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_id INT NOT NULL,
    executed_date VARCHAR(8) NOT NULL,
    executed_by VARCHAR(10),
    summary_judge VARCHAR(5),
    summary_note TEXT,
    next_recommended_date VARCHAR(8),
    approval_status VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inspection Item Results (detail)
CREATE TABLE inspection_items_results (
    result_item_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT NOT NULL,
    item_id INT NOT NULL,
    judge CHAR(1) NOT NULL,
    measured_value VARCHAR(50),
    note TEXT
);

-- Inspection Photos
CREATE TABLE inspection_photos (
    photo_id INT AUTO_INCREMENT PRIMARY KEY,
    result_item_id INT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    original_name VARCHAR(200),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Incidents
CREATE TABLE incidents (
    incident_no VARCHAR(20) PRIMARY KEY,
    result_id INT,
    incident_datetime TIMESTAMP NOT NULL,
    finder VARCHAR(50) NOT NULL,
    equipment_code VARCHAR(10) NOT NULL,
    weather VARCHAR(5),
    temperature INT,
    incident_type VARCHAR(10) NOT NULL,
    severity VARCHAR(5) NOT NULL,
    incident_part VARCHAR(200) NOT NULL,
    incident_detail TEXT NOT NULL,
    tmp_action TEXT,
    tmp_action_person VARCHAR(10),
    tmp_action_date VARCHAR(8),
    cause TEXT,
    counter_detail TEXT,
    status VARCHAR(10) NOT NULL DEFAULT '未了',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Incident Timeline
CREATE TABLE incident_timeline (
    timeline_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    action_datetime TIMESTAMP NOT NULL,
    action_user VARCHAR(50) NOT NULL,
    action_content TEXT NOT NULL,
    status_from VARCHAR(10),
    status_to VARCHAR(10)
);

-- Incident Attachments
CREATE TABLE incident_attachments (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    original_name VARCHAR(200),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Counter Orders (header)
CREATE TABLE counter_orders (
    order_no VARCHAR(20) PRIMARY KEY,
    incident_no VARCHAR(20),
    order_date VARCHAR(8) NOT NULL,
    issuer VARCHAR(50) NOT NULL,
    overall_deadline VARCHAR(8),
    overall_priority VARCHAR(3) NOT NULL,
    status VARCHAR(5) NOT NULL DEFAULT '未了',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Counter Order Details
CREATE TABLE counter_order_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(20) NOT NULL,
    seq_no INT NOT NULL,
    work_content VARCHAR(500) NOT NULL,
    person_code VARCHAR(10),
    deadline VARCHAR(8),
    priority VARCHAR(3),
    status VARCHAR(3) NOT NULL DEFAULT '未了',
    actual_hours DECIMAL(5,1),
    used_part_code VARCHAR(10),
    used_quantity INT,
    note TEXT
);

-- CAPA Reports
CREATE TABLE capa_reports (
    capa_id INT AUTO_INCREMENT PRIMARY KEY,
    incident_no VARCHAR(20) NOT NULL,
    why1 TEXT NOT NULL,
    why2 TEXT NOT NULL,
    why3 TEXT NOT NULL,
    why4 TEXT NOT NULL,
    why5 TEXT NOT NULL,
    countermeasure TEXT NOT NULL,
    verify_method TEXT NOT NULL,
    verify_deadline VARCHAR(8) NOT NULL,
    status VARCHAR(5) NOT NULL DEFAULT '申請中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Departments (hierarchical)
CREATE TABLE departments (
    dept_code VARCHAR(10) PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL,
    parent_dept_code VARCHAR(10),
    dept_level INT NOT NULL,
    dept_type VARCHAR(5) NOT NULL,
    start_date VARCHAR(8) NOT NULL,
    end_date VARCHAR(8),
    address VARCHAR(200),
    tel VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employees
CREATE TABLE employees (
    emp_no VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_kana VARCHAR(100),
    birth_date VARCHAR(8),
    join_date VARCHAR(6),
    dept_code VARCHAR(10),
    position VARCHAR(10),
    assign_date VARCHAR(8),
    inspection_rank CHAR(1),
    inspection_cert_date VARCHAR(8),
    inspection_cert_expire VARCHAR(8),
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee Qualifications
CREATE TABLE employee_qualifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_no VARCHAR(10) NOT NULL,
    qualification_code VARCHAR(10) NOT NULL,
    cert_date VARCHAR(8),
    expire_date VARCHAR(8)
);

-- Holidays
CREATE TABLE holidays (
    holiday_id INT AUTO_INCREMENT PRIMARY KEY,
    holiday_date VARCHAR(8) NOT NULL UNIQUE,
    holiday_type VARCHAR(5) NOT NULL,
    holiday_name VARCHAR(100),
    is_transfer BOOLEAN NOT NULL DEFAULT FALSE,
    transfer_date VARCHAR(8)
);

-- Parts
CREATE TABLE parts (
    part_code VARCHAR(10) PRIMARY KEY,
    part_name VARCHAR(100) NOT NULL,
    part_type VARCHAR(10),
    unit VARCHAR(5),
    order_point INT,
    safety_stock INT,
    current_stock INT NOT NULL DEFAULT 0,
    unit_price INT,
    supplier VARCHAR(100),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Part-Equipment Relations
CREATE TABLE part_equipment_relations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    part_code VARCHAR(10) NOT NULL,
    equipment_type VARCHAR(10),
    equipment_code VARCHAR(10)
);

-- Part Usages
CREATE TABLE part_usages (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    part_code VARCHAR(10) NOT NULL,
    equipment_code VARCHAR(10),
    usage_date VARCHAR(8) NOT NULL,
    quantity INT NOT NULL,
    stock_before INT NOT NULL,
    stock_after INT NOT NULL,
    purpose VARCHAR(3) NOT NULL,
    used_by VARCHAR(10),
    order_no VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- [ ] **Step 2: Create seed.sql with sample data**

```sql
-- Sample departments (4-level hierarchy)
INSERT INTO departments (dept_code, dept_name, parent_dept_code, dept_level, dept_type, start_date) VALUES
('HONSHA', '本社', NULL, 1, '本社', '20000101'),
('TOHOKU', '東北支社', 'HONSHA', 2, '支社', '20000101'),
('KANTO', '関東支社', 'HONSHA', 2, '支社', '20000101'),
('TOHOKU-MIYAGI', '宮城営業所', 'TOHOKU', 3, '営業所', '20000101'),
('TOHOKU-FUKUSHIMA', '福島営業所', 'TOHOKU', 3, '営業所', '20000101'),
('KANTO-TOKYO', '東京営業所', 'KANTO', 3, '営業所', '20000101'),
('MIYAGI-SENDAI', '仙台出張所', 'TOHOKU-MIYAGI', 4, '出張所', '20100101'),
('TOKYO-SHINJUKU', '新宿出張所', 'KANTO-TOKYO', 4, '出張所', '20100101');

-- Sample employees
INSERT INTO employees (emp_no, name, name_kana, dept_code, position, login_id, password_hash, inspection_rank) VALUES
('EMP-0001', '山田 太郎', 'ヤマダ タロウ', 'TOHOKU-MIYAGI', '係長', 'yamada', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'A'),
('EMP-0002', '佐藤 花子', 'サトウ ハナコ', 'KANTO-TOKYO', '主任', 'sato', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'B'),
('EMP-0003', '鈴木 一郎', 'スズキ イチロウ', 'TOHOKU-FUKUSHIMA', '一般', 'suzuki', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'C');

-- Sample equipment
INSERT INTO equipment (equipment_code, equipment_name, equipment_type, voltage_level, maintenance_rank, status) VALUES
('TR-0001', '南変電所 #1 変圧器', '変圧器', '154kV', 'S', '運用中'),
('TR-0002', '南変電所 #2 変圧器', '変圧器', '154kV', 'A', '運用中'),
('CB-0001', '北変電所 #1 遮断器', '遮断器', '275kV', 'S', '運用中'),
('CB-0002', '北変電所 #2 遮断器', '遮断器', '275kV', 'B', '運用中'),
('DS-0001', '東開閉所 #1 開閉器', '開閉器', '66kV', 'A', '運用中'),
('CB-0003', '西変電所 #3 遮断器', '遮断器', '154kV', 'C', '廃止'),
('TR-0003', '南変電所 #3 変圧器', '変圧器', '154kV', 'A', '停止中'),
('CBL-0001', '南-北線 ケーブル', 'ケーブル', '275kV', 'S', '運用中'),
('RY-0001', '北変電所 保護継電器', '保護継電器', '275kV', 'S', '運用中'),
('VT-0001', '東開閉所 計器用変成器', '計器用変成器', '66kV', 'B', '運用中');

-- Sample inspection template
INSERT INTO inspection_template (template_name, equipment_type, inspection_kind, sort_order) VALUES
('変圧器 定期点検', '変圧器', '定期', 1),
('遮断器 定期点検', '遮断器', '定期', 2),
('変圧器 日常点検', '変圧器', '日常', 3);

-- Sample inspection items (3 levels for template 1)
INSERT INTO inspection_items (item_id, template_id, parent_item_id, item_level, item_name, judge_criteria, normal_range, unit, sort_order) VALUES
(1, 1, NULL, 1, '外観点検', NULL, NULL, NULL, 1),
(2, 1, NULL, 1, '絶縁油点検', NULL, NULL, NULL, 2),
(3, 1, NULL, 1, '電気的特性', NULL, NULL, NULL, 3),
(4, 1, 1, 2, 'ブッシング部', NULL, NULL, NULL, 1),
(5, 1, 1, 2, '本体外観', NULL, NULL, NULL, 2),
(6, 1, 4, 3, 'き裂の有無', 'ONLY_O', NULL, NULL, 1),
(7, 1, 4, 3, '汚損状況', 'O_X_TRI', NULL, NULL, 2),
(8, 1, 5, 3, '塗装剥離', 'O_X_TRI', NULL, NULL, 1),
(9, 1, 5, 3, '腐食の有無', 'O_X_TRI', NULL, NULL, 2),
(10, 1, 5, 3, '漏油の有無', 'ONLY_O', NULL, NULL, 3),
(11, 1, 2, 2, '油面レベル', NULL, NULL, NULL, 1),
(12, 1, 2, 2, '油性状', NULL, NULL, NULL, 2),
(13, 1, 11, 3, '油面計指示値', 'O_X_TRI', '50～90', '%', 1),
(14, 1, 11, 3, '油漏れ痕跡', 'ONLY_O', NULL, NULL, 2),
(15, 1, 12, 3, '絶縁油耐圧試験', 'O_X_TRI', '30以上', 'kV/2.5mm', 1),
(16, 1, 12, 3, '油中ガス分析', 'O_X_TRI', NULL, NULL, 2),
(17, 1, 3, 2, '絶縁抵抗', NULL, NULL, NULL, 1),
(18, 1, 3, 2, '巻線抵抗', NULL, NULL, NULL, 2),
(19, 1, 17, 3, '一次-二次間', 'O_X_TRI', '1000以上', 'MΩ', 1),
(20, 1, 17, 3, '一次-接地間', 'O_X_TRI', '1000以上', 'MΩ', 2),
(21, 1, 18, 3, '一次巻線', 'O_X_TRI', NULL, 'Ω', 1),
(22, 1, 18, 3, '二次巻線', 'O_X_TRI', NULL, 'Ω', 2);

-- Sample parts
INSERT INTO parts (part_code, part_name, part_type, unit, order_point, safety_stock, current_stock, unit_price, supplier) VALUES
('P-GSK-001', 'ガスケット A型', 'ガスケット', '個', 10, 5, 25, 500, '株式会社 電材商事'),
('P-BLT-001', '六角ボルト M12x50', 'ボルト', '個', 50, 20, 200, 50, '株式会社 電材商事'),
('P-OIL-001', '絶縁油 高圧用', '絶縁油', 'L', 100, 50, 500, 300, '日本絶縁油株式会社'),
('P-BSH-001', 'ブッシング 66kV用', 'ブッシング', '式', 2, 1, 5, 150000, '高圧機器工業株式会社');

-- Sample holiday
INSERT INTO holidays (holiday_date, holiday_type, holiday_name, is_transfer) VALUES
('20260101', '法定休日', '元日', FALSE),
('20260112', '法定休日', '成人の日', FALSE),
('20260211', '法定休日', '建国記念の日', FALSE),
('20260429', '法定休日', '昭和の日', FALSE),
('20260503', '法定休日', '憲法記念日', FALSE),
('20260504', '法定休日', 'みどりの日', FALSE),
('20260505', '法定休日', 'こどもの日', FALSE),
('20261231', '会社指定休日', '年末休暇', FALSE);
```

- [ ] **Step 3: Create MyBatisUtil.java**

```java
package com.struts-lab.db;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
    private static SqlSessionFactory factory;

    static {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("MyBatis init failed: " + e.getMessage(), e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }
}
```

- [ ] **Step 4: Create EqpDto.java**

```java
package com.struts-lab.dto;

public class EqpDto {
    private String equipmentCode;
    private String equipmentName;
    private String equipmentType;
    private String voltageLevel;
    private Integer ratedCapacity;
    private Integer ratedCurrent;
    private String frequency;
    private String parentEquipmentCode;
    private String parentEquipmentName;
    private String installDate;
    private String locationAddress;
    private String coordinates;
    private String maintenanceRank;
    private Integer inspectionInterval;
    private String lastInspectionDate;
    private String nextInspectionDate;
    private String status;
    private String note;

    // getters and setters
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getVoltageLevel() { return voltageLevel; }
    public void setVoltageLevel(String v) { this.voltageLevel = v; }
    public Integer getRatedCapacity() { return ratedCapacity; }
    public void setRatedCapacity(Integer v) { this.ratedCapacity = v; }
    public Integer getRatedCurrent() { return ratedCurrent; }
    public void setRatedCurrent(Integer v) { this.ratedCurrent = v; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String v) { this.frequency = v; }
    public String getParentEquipmentCode() { return parentEquipmentCode; }
    public void setParentEquipmentCode(String v) { this.parentEquipmentCode = v; }
    public String getParentEquipmentName() { return parentEquipmentName; }
    public void setParentEquipmentName(String v) { this.parentEquipmentName = v; }
    public String getInstallDate() { return installDate; }
    public void setInstallDate(String v) { this.installDate = v; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String v) { this.locationAddress = v; }
    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String v) { this.coordinates = v; }
    public String getMaintenanceRank() { return maintenanceRank; }
    public void setMaintenanceRank(String v) { this.maintenanceRank = v; }
    public Integer getInspectionInterval() { return inspectionInterval; }
    public void setInspectionInterval(Integer v) { this.inspectionInterval = v; }
    public String getLastInspectionDate() { return lastInspectionDate; }
    public void setLastInspectionDate(String v) { this.lastInspectionDate = v; }
    public String getNextInspectionDate() { return nextInspectionDate; }
    public void setNextInspectionDate(String v) { this.nextInspectionDate = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
```

- [ ] **Step 5: Create EqpDao.java interface**

```java
package com.struts-lab.dao;

import com.struts-lab.dto.EqpDto;
import java.util.List;
import java.util.Map;

public interface EqpDao {
    List<EqpDto> search(Map<String, Object> params);
    int count(Map<String, Object> params);
    EqpDto findById(String equipmentCode);
    void insert(EqpDto eqp);
    void update(EqpDto eqp);
    void delete(String equipmentCode);
    List<EqpDto> findAll();
}
```

- [ ] **Step 6: Create EquipmentMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.struts-lab.dao.EqpDao">

    <select id="search" resultType="EqpDto">
        SELECT e.*, p.equipment_name AS parent_equipment_name
        FROM equipment e
        LEFT JOIN equipment p ON e.parent_equipment_code = p.equipment_code
        <where>
            <if test="equipmentType != null and equipmentType != ''">
                AND e.equipment_type = #{equipmentType}
            </if>
            <if test="voltageLevel != null and voltageLevel != ''">
                AND e.voltage_level = #{voltageLevel}
            </if>
            <if test="yearFrom != null and yearFrom != ''">
                AND e.install_date &gt;= #{yearFrom}
            </if>
            <if test="yearTo != null and yearTo != ''">
                AND e.install_date &lt;= #{yearTo}
            </if>
            <if test="maintenanceRank != null and maintenanceRank != ''">
                AND e.maintenance_rank = #{maintenanceRank}
            </if>
            <if test="deptName != null and deptName != ''">
                AND e.location_address LIKE CONCAT('%', #{deptName}, '%')
            </if>
        </where>
        ORDER BY e.equipment_code
        <if test="offset != null and limit != null">
            LIMIT #{limit} OFFSET #{offset}
        </if>
    </select>

    <select id="count" resultType="int">
        SELECT COUNT(*) FROM equipment e
        <where>
            <if test="equipmentType != null and equipmentType != ''">
                AND e.equipment_type = #{equipmentType}
            </if>
            <if test="voltageLevel != null and voltageLevel != ''">
                AND e.voltage_level = #{voltageLevel}
            </if>
            <if test="yearFrom != null and yearFrom != ''">
                AND e.install_date &gt;= #{yearFrom}
            </if>
            <if test="yearTo != null and yearTo != ''">
                AND e.install_date &lt;= #{yearTo}
            </if>
            <if test="maintenanceRank != null and maintenanceRank != ''">
                AND e.maintenance_rank = #{maintenanceRank}
            </if>
            <if test="deptName != null and deptName != ''">
                AND e.location_address LIKE CONCAT('%', #{deptName}, '%')
            </if>
        </where>
    </select>

    <select id="findById" resultType="EqpDto">
        SELECT e.*, p.equipment_name AS parent_equipment_name
        FROM equipment e
        LEFT JOIN equipment p ON e.parent_equipment_code = p.equipment_code
        WHERE e.equipment_code = #{equipmentCode}
    </select>

    <select id="findAll" resultType="EqpDto">
        SELECT * FROM equipment ORDER BY equipment_code
    </select>

    <insert id="insert">
        INSERT INTO equipment (
            equipment_code, equipment_name, equipment_type, voltage_level,
            rated_capacity, rated_current, frequency, parent_equipment_code,
            install_date, location_address, coordinates, maintenance_rank,
            inspection_interval, last_inspection_date, next_inspection_date,
            status, note
        ) VALUES (
            #{equipmentCode}, #{equipmentName}, #{equipmentType}, #{voltageLevel},
            #{ratedCapacity}, #{ratedCurrent}, #{frequency}, #{parentEquipmentCode},
            #{installDate}, #{locationAddress}, #{coordinates}, #{maintenanceRank},
            #{inspectionInterval}, #{lastInspectionDate}, #{nextInspectionDate},
            #{status}, #{note}
        )
    </insert>

    <update id="update">
        UPDATE equipment SET
            equipment_name = #{equipmentName},
            equipment_type = #{equipmentType},
            voltage_level = #{voltageLevel},
            rated_capacity = #{ratedCapacity},
            rated_current = #{ratedCurrent},
            frequency = #{frequency},
            parent_equipment_code = #{parentEquipmentCode},
            install_date = #{installDate},
            location_address = #{locationAddress},
            coordinates = #{coordinates},
            maintenance_rank = #{maintenanceRank},
            inspection_interval = #{inspectionInterval},
            last_inspection_date = #{lastInspectionDate},
            next_inspection_date = #{nextInspectionDate},
            status = #{status},
            note = #{note},
            updated_at = CURRENT_TIMESTAMP
        WHERE equipment_code = #{equipmentCode}
    </update>

    <delete id="delete">
        DELETE FROM equipment WHERE equipment_code = #{equipmentCode}
    </delete>

</mapper>
```

- [ ] **Step 7: Initialize DB and verify**

```bash
# Create H2 init script runner
```

- [ ] **Step 8: Commit**

```bash
git add src/main/db/ src/main/java/com/struts-lab/db/ src/main/java/com/struts-lab/dto/EqpDto.java src/main/java/com/struts-lab/dao/EqpDao.java src/main/resources/com/struts-lab/dao/EquipmentMapper.xml
git commit -m "feat: database schema + MyBatis session + Equipment mapper"
```

---

### Task 2: Common Infrastructure — Tiles Layouts + Common JSPs + CSS

**Files:**
- Create: `src/main/webapp/WEB-INF/jsp/common/header.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/menu.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/footer.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/paging.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/errorMessages.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/confirmDialog.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/common/error.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/baseLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/listLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/inputLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/wizardLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/popupLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/tiles/printLayout.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/login.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/menu.jsp`
- Create: `src/main/webapp/css/style.css`

- [ ] **Step 1: Create CSS**

```css
/* strutslab style.css — 2005-era minimal styling */
body { font-family: "MS PGothic", sans-serif; font-size: 12px; margin: 0; padding: 0; background: #fff; color: #333; }
table { border-collapse: collapse; width: 100%; }
th, td { border: 1px solid #999; padding: 4px 6px; text-align: left; vertical-align: top; }
th { background: #dde; font-weight: bold; }
h1 { font-size: 16px; border-bottom: 2px solid #369; padding-bottom: 4px; }
h2 { font-size: 14px; color: #369; }

/* Layout */
#header { background: #336699; color: #fff; padding: 8px 16px; }
#header a { color: #fff; }
#header .title { font-size: 16px; font-weight: bold; }
#header .user-info { float: right; font-size: 11px; }
#menu { float: left; width: 200px; min-height: 500px; background: #f0f3f7; padding: 10px; }
#menu ul { list-style: none; padding: 0; margin: 0; }
#menu li { margin: 4px 0; }
#menu a { color: #336699; text-decoration: none; font-size: 13px; }
#menu a:hover { text-decoration: underline; }
#menu .module-title { font-weight: bold; color: #333; margin-top: 12px; border-bottom: 1px solid #ccc; }
#body { margin-left: 220px; padding: 16px; }
#footer { clear: both; background: #f0f3f7; padding: 6px 16px; text-align: center; font-size: 10px; color: #666; border-top: 1px solid #ccc; }

/* Forms — early 2000s style */
.form-table th { width: 180px; background: #f4f4f4; text-align: right; padding-right: 8px; }
.form-table td input[type="text"],
.form-table td input[type="password"],
.form-table td textarea,
.form-table td select { width: 250px; padding: 2px 4px; border: 1px solid #999; font-size: 12px; }
.form-table td textarea { height: 60px; }
input[type="submit"], button, .btn {
    background: #336699; color: #fff; border: 1px solid #225588; padding: 3px 12px;
    font-size: 12px; cursor: pointer; margin: 2px;
}
input[type="submit"]:hover, button:hover { background: #4477aa; }
.btn-back { background: #999; border-color: #888; }
.btn-danger { background: #c33; border-color: #922; }

/* Badges */
.badge { padding: 2px 6px; font-size: 10px; font-weight: bold; }
.badge-green { background: #cfc; color: #060; }
.badge-yellow { background: #ffc; color: #660; }
.badge-red { background: #fcc; color: #600; }
.badge-gray { background: #eee; color: #666; }

/* Calendar */
.calendar td { width: 14%; height: 40px; vertical-align: top; font-size: 11px; }
.calendar .holiday-statutory { background: #fcc; }
.calendar .holiday-company { background: #ccf; }
.calendar .holiday-outage { background: #ffc; }
.calendar .weekday-header { background: #dde; text-align: center; font-weight: bold; }

/* Search area */
.search-area { background: #f8f8f8; border: 1px solid #ccc; padding: 8px; margin-bottom: 12px; }
.search-area td { border: none; padding: 2px 6px; }

/* Paging */
.paging { margin: 12px 0; text-align: center; }
.paging a, .paging span { padding: 2px 6px; border: 1px solid #ccc; margin: 0 2px; text-decoration: none; color: #336699; }
.paging span.current { background: #336699; color: #fff; font-weight: bold; }

/* Wizard step indicator */
.wizard-steps { margin: 0 0 16px 0; padding: 0; }
.wizard-steps td { text-align: center; padding: 6px; border: 1px solid #ccc; }
.wizard-steps td.active { background: #336699; color: #fff; font-weight: bold; }
.wizard-steps td.done { background: #cfc; }
.wizard-steps td.pending { background: #eee; color: #999; }

/* Timeline */
.timeline td { padding: 4px 8px; }
.timeline .dateline { white-space: nowrap; font-size: 11px; color: #666; }

/* Row highlight */
tr.warning { background: #fff3cd; }
tr.danger { background: #fdd; }

/* Print */
@media print {
    #header, #menu, #footer, .no-print { display: none; }
    #body { margin: 0; }
}
```

- [ ] **Step 2: Create baseLayout.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><tiles:getAsString name="title"/> — <bean:message key="app.title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div id="header">
    <tiles:insert attribute="header"/>
</div>
<div id="menu">
    <tiles:insert attribute="menu"/>
</div>
<div id="body">
    <tiles:insert attribute="body"/>
</div>
<div id="footer">
    <tiles:insert attribute="footer"/>
</div>
</body>
</html>
```

- [ ] **Step 3: Create header.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<span class="title"><bean:message key="app.title"/></span>
<%
    String user = (String) session.getAttribute("loginUser");
    if (user != null) {
%>
    <span class="user-info">
        ログインユーザー: <%= user %> &nbsp;
        <a href="<%=request.getContextPath()%>/logout.do">ログアウト</a>
    </span>
<%
    }
%>
```

- [ ] **Step 4: Create menu.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<ul>
    <li><a href="<%=request.getContextPath()%>/login.do">メインメニュー</a></li>
</ul>
<div class="module-title">マスタ管理</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/mst/eqp/list.do">設備マスタ一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/mst/chkitem/list.do">点検項目マスタ一覧</a></li>
</ul>
<div class="module-title">点検計画・実施</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/ins/plan/yearly.do">年間点検計画一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/plan/wizard.do">点検計画登録</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/daily.do">点検実施一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/ins/approval/list.do">点検実施承認一覧</a></li>
</ul>
<div class="module-title">異常報告・対応指示</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/inc/list.do">異常報告一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/counter/list.do">対応指示一覧</a></li>
</ul>
<div class="module-title">組織・要員管理</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/org/dept/list.do">部署マスタ一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/org/emp/list.do">担当者マスタ一覧</a></li>
</ul>
<div class="module-title">その他</div>
<ul>
    <li><a href="<%=request.getContextPath()%>/cal/list.do">休日カレンダー</a></li>
    <li><a href="<%=request.getContextPath()%>/parts/list.do">保守部品一覧</a></li>
    <li><a href="<%=request.getContextPath()%>/report/summary.do">総合レポート</a></li>
</ul>
```

- [ ] **Step 5: Create footer.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
&copy; 2026 StrutsLab — 電力設備巡視点検管理システム v1.0
```

- [ ] **Step 6: Create paging.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    Integer current = (Integer) request.getAttribute("currentPage");
    Integer total = (Integer) request.getAttribute("totalPages");
    String baseUrl = (String) request.getAttribute("pagingUrl");
    if (current == null) current = 1;
    if (total == null) total = 1;
    if (baseUrl == null) baseUrl = "";
%>
<div class="paging">
    <% if (current > 1) { %>
        <a href="<%=baseUrl%>&page=<%=current-1%>">前へ</a>
    <% } %>
    <% for (int i = 1; i <= total; i++) { %>
        <% if (i == current) { %>
            <span class="current"><%=i%></span>
        <% } else { %>
            <a href="<%=baseUrl%>&page=<%=i%>"><%=i%></a>
        <% } %>
    <% } %>
    <% if (current < total) { %>
        <a href="<%=baseUrl%>&page=<%=current+1%>">次へ</a>
    <% } %>
</div>
```

- [ ] **Step 7: Remaining Tiles layouts + common JSPs** (implemented similarly)

- [ ] **Step 8: Commit**

```bash
git add src/main/webapp/WEB-INF/jsp/ src/main/webapp/css/
git commit -m "feat: Tiles layouts + common JSPs + CSS"
```

---

### Task 3: Login + Authentication

**Files:**
- Create: `src/main/java/com/struts-lab/form/common/LoginForm.java`
- Create: `src/main/java/com/struts-lab/action/common/LoginAction.java`
- Create: `src/main/java/com/struts-lab/action/common/LogoutAction.java`
- Create: `src/main/java/com/struts-lab/action/common/BaseAction.java`
- Create: `src/main/webapp/WEB-INF/jsp/login.jsp`

- [ ] **Step 1: Create LoginForm.java**

```java
package com.struts-lab.form.common;

import org.apache.struts.action.ActionForm;

public class LoginForm extends ActionForm {
    private String loginId;
    private String password;

    public String getLoginId() { return loginId; }
    public void setLoginId(String v) { this.loginId = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
}
```

- [ ] **Step 2: Create LoginAction.java**

```java
package com.struts-lab.action.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.struts-lab.form.common.LoginForm;
import com.struts-lab.db.MyBatisUtil;
import com.struts-lab.dao.EmpDao;
import com.struts-lab.dto.EmpDto;

import org.apache.ibatis.session.SqlSession;

import java.security.MessageDigest;

public class LoginAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoginForm loginForm = (LoginForm) form;
        String loginId = loginForm.getLoginId();
        String password = loginForm.getPassword();

        // GET request — show login page
        if (loginId == null || loginId.isEmpty()) {
            return mapping.findForward("input");
        }

        // Validate credentials
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            EmpDto emp = dao.findByLoginId(loginId);

            if (emp == null) {
                request.setAttribute("errorMessage", "ログインIDまたはパスワードが正しくありません。");
                return mapping.findForward("input");
            }

            String hash = sha256(password);
            if (!hash.equals(emp.getPasswordHash())) {
                request.setAttribute("errorMessage", "ログインIDまたはパスワードが正しくありません。");
                return mapping.findForward("input");
            }

            if (emp.getIsLocked()) {
                request.setAttribute("errorMessage", "アカウントがロックされています。");
                return mapping.findForward("input");
            }

            // Set session
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", emp.getName());
            session.setAttribute("empNo", emp.getEmpNo());
            session.setAttribute("deptCode", emp.getDeptCode());

            return mapping.findForward("success");
        }
    }

    private String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(s.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
```

- [ ] **Step 3: Create LogoutAction.java**

```java
package com.struts-lab.action.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class LogoutAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        return mapping.findForward("success");
    }
}
```

- [ ] **Step 4: Create login.jsp**

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>ログイン — <bean:message key="app.title"/></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div style="width:400px; margin:100px auto; border:1px solid #ccc; padding:24px;">
<h2 style="text-align:center;">電力設備巡視点検管理システム</h2>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33; background:#fcc; padding:8px; margin-bottom:12px; border:1px solid #c33;">
        <%= errMsg %>
    </div>
<%
    }
%>
<html:form action="/login" method="post">
<table class="form-table" style="width:100%;">
    <tr>
        <th>ログインID</th>
        <td><html:text property="loginId" styleClass="input-text"/></td>
    </tr>
    <tr>
        <th>パスワード</th>
        <td><html:password property="password" styleClass="input-text"/></td>
    </tr>
</table>
<div style="text-align:center; margin-top:16px;">
    <html:submit value="ログイン"/>
</div>
</html:form>
</div>
</body>
</html>
```

- [ ] **Step 5: Build and verify login works**

```bash
mvn clean compile -q
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/struts-lab/form/common/ src/main/java/com/struts-lab/action/common/ src/main/webapp/WEB-INF/jsp/login.jsp
git commit -m "feat: login/logout with session authentication"
```

---

> **Note:** Tasks 4-12 below follow the same step-by-step pattern. Due to document length, each is summarized with key code blocks and file lists. The full plan details 40+ tasks covering all 27 screens, remaining DAOs/DTOs, custom tags, and remaining Mappers.

### Task 4: Custom Tags — 7 custom tags

**Files:**
- Create: `src/main/java/com/struts-lab/taglib/EqpTreeSelectTag.java`
- Create: `src/main/java/com/struts-lab/taglib/DatePickerTag.java`
- Create: `src/main/java/com/struts-lab/taglib/SectionHeaderTag.java`
- Create: `src/main/java/com/struts-lab/taglib/StatusBadgeTag.java`
- Create: `src/main/java/com/struts-lab/taglib/InspectionChecklistTag.java`
- Create: `src/main/java/com/struts-lab/taglib/IndexedRowTag.java`
- Create: `src/main/java/com/struts-lab/taglib/TimelineTag.java`
- Create: `src/main/webapp/WEB-INF/tld/eqp-tree.tld`
- Create: `src/main/webapp/WEB-INF/tld/date-picker.tld`
- Create: `src/main/webapp/WEB-INF/tld/app-common.tld`

Each tag extends `javax.servlet.jsp.tagext.TagSupport` and generates HTML via `JspWriter`. See design doc `14_共通部品設計書` for tag specifications.

### Task 5: Master — Equipment List/Edit (screens 1-2)

**Files:**
- Create: `src/main/java/com/struts-lab/form/mst/EqpSearchForm.java`
- Create: `src/main/java/com/struts-lab/form/mst/EqpForm.java`
- Create: `src/main/java/com/struts-lab/action/mst/EqpListAction.java`
- Create: `src/main/java/com/struts-lab/action/mst/EqpSaveAction.java`
- Create: `src/main/webapp/WEB-INF/jsp/mst/eqpListSearch.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/mst/eqpListTable.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/mst/eqpEdit.jsp`

### Task 6: Master — Check Item List/Edit (screens 3-4)

**Files:**
- Create: `src/main/java/com/struts-lab/form/mst/CheckItemSearchForm.java`
- Create: `src/main/java/com/struts-lab/form/mst/CheckItemForm.java`
- Create: `src/main/java/com/struts-lab/action/mst/CheckItemListAction.java`
- Create: `src/main/java/com/struts-lab/action/mst/CheckItemSaveAction.java`
- Create: `src/main/webapp/WEB-INF/jsp/mst/chkItemListSearch.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/mst/chkItemListTable.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/mst/chkItemEdit.jsp`
- Create: `src/main/resources/com/struts-lab/dao/InspectionTemplateMapper.xml`
- Create: `src/main/java/com/struts-lab/dao/ChkItemDao.java`
- Create: `src/main/java/com/struts-lab/dto/ChkTmplDto.java`
- Create: `src/main/java/com/struts-lab/dto/ChkItemDto.java`

### Task 7: Inspection — Yearly Plan + Plan Wizard (screens 5-6)

**Files:**
- Create: `src/main/java/com/struts-lab/form/ins/YearlyPlanForm.java`
- Create: `src/main/java/com/struts-lab/form/ins/PlanWizardForm.java`
- Create: `src/main/java/com/struts-lab/action/ins/YearlyPlanAction.java`
- Create: `src/main/java/com/struts-lab/action/ins/PlanWizardAction.java`
- Create: JSPs: yearlyPlanSearch.jsp, yearlyPlanMatrix.jsp, planWiz1/2/3.jsp, planConfirm.jsp
- Create: `src/main/resources/com/struts-lab/dao/InspectionPlanMapper.xml`
- Create: `src/main/java/com/struts-lab/dao/PlanDao.java`
- Create: `src/main/java/com/struts-lab/dto/PlanDto.java`

### Task 8: Inspection — Daily List + Exec Input + Detail + Approval (screens 7-10)

**Files:**
- Create: Forms: DailyForm, ExecForm, ApprovalForm
- Create: Actions: DailyListAction, ExecInputAction, ExecDetailAction, ApprovalListAction
- Create: JSPs: dailyListSearch/Table.jsp, execInput.jsp, execDetail.jsp, approvalListSearch/Table.jsp
- Create: `src/main/resources/com/struts-lab/dao/InspectionResultMapper.xml`
- Create: `src/main/java/com/struts-lab/dao/ExecDao.java`
- Create: DTOs: ExecResultDto, ExecItemResultDto, ApprovalDto

### Task 9: Incident — List + Create + Detail (screens 11-13)

**Files:**
- Create: Forms: IncidentSearchForm, IncidentForm
- Create: Actions: IncidentListAction, IncidentCreateAction, IncidentDetailAction
- Create: JSPs: incListSearch/Table.jsp, incCreate.jsp, incDetail.jsp
- Create: `src/main/resources/com/struts-lab/dao/IncidentMapper.xml`
- Create: `src/main/java/com/struts-lab/dao/IncidentDao.java`
- Create: DTOs: IncidentDto, TimelineDto

### Task 10: Counter — Create + List + Detail + CAPA (screens 14-17)

**Files:**
- Create: Forms: CounterForm, CounterSearchForm, CounterDetailForm, CapaForm
- Create: Actions: CounterCreateAction, CounterListAction, CounterDetailAction, CapaAction
- Create: JSPs: ctrCreate.jsp, ctrListSearch/Table.jsp, ctrDetail.jsp, capaCreate.jsp
- Create: `src/main/resources/com/struts-lab/dao/CounterOrderMapper.xml`
- Create: `src/main/resources/com/struts-lab/dao/CapaMapper.xml`
- Create: `src/main/java/com/struts-lab/dao/CounterDao.java`
- Create: `src/main/java/com/struts-lab/dao/CapaDao.java`
- Create: DTOs: CounterDto, CounterDetailDto, CapaDto

### Task 11: Organization + Calendar + Parts + Report (screens 18-27)

**Files:**
- Create: All remaining Forms, Actions, JSPs, Mappers, DAOs, DTOs for:
  - Dept List/Edit (screens 19-20)
  - Emp List/Edit (screens 21-22)
  - Calendar List/Edit (screens 23-24)
  - Parts List/Edit/Usage (screens 25-27)
  - Summary Report (screen 18)

### Task 12: Validation Rules + Error Messages + Final Integration

**Files:**
- Modify: `src/main/resources/validation.xml` — add all form validation rules
- Modify: `src/main/resources/ApplicationResources_ja.properties` — add all labels and messages
- Create: `src/main/java/com/struts-lab/db/DbInitializer.java` — initialize DB on startup

### Task 13: DB Initializer (ServletContextListener)

**Files:**
- Create: `src/main/java/com/struts-lab/db/DbInitializer.java`

```java
package com.struts-lab.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.Reader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

public class DbInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = MyBatisUtil.getSqlSessionFactory().openSession().getConnection()) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setAutoCommit(true);
            runner.setStopOnError(true);

            Reader schemaReader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("db/schema.sql"), "UTF-8");
            runner.runScript(schemaReader);
            schemaReader.close();

            Reader seedReader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("db/seed.sql"), "UTF-8");
            runner.runScript(seedReader);
            seedReader.close();

            System.out.println("[StrutsLab] Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("[StrutsLab] Database init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
```

- [ ] **Step 1: Register in web.xml**

```xml
<listener>
    <listener-class>com.struts-lab.db.DbInitializer</listener-class>
</listener>
```

- [ ] **Step 2: Final build and deploy test**

```bash
mvn clean package
mvn tomcat7:run
```

- [ ] **Step 3: Verify all 27 screens accessible**

Visit `http://localhost:8080/StrutsLab/` → login (yamada / password = "123") → navigate all modules.

---

## Implementation Order Summary

| Task | Module | Screens | Est. Time |
|---|---|---|---|
| 0 | Project Scaffolding | — | 30 min |
| 1 | DB Schema + MyBatis | — | 1 hr |
| 2 | Tiles + Common JSPs + CSS | — | 1.5 hr |
| 3 | Login/Auth | — | 30 min |
| 4 | Custom Tags (7) | — | 2 hr |
| 5 | Master: Equipment | 1-2 | 1.5 hr |
| 6 | Master: Check Items | 3-4 | 1.5 hr |
| 7 | Inspection: Plan + Wizard | 5-6 | 2 hr |
| 8 | Inspection: Daily + Exec + Approval | 7-10 | 3 hr |
| 9 | Incident: List + Create + Detail | 11-13 | 2 hr |
| 10 | Counter: Create + List + Detail + CAPA | 14-17 | 2.5 hr |
| 11 | Org + Calendar + Parts + Report | 18-27 | 4 hr |
| 12 | Validation + Messages | — | 1 hr |
| 13 | DB Initializer + Final Integration | — | 30 min |

**Total estimated: ~23 hours** for full implementation of all 27 screens with 18 DB tables.
