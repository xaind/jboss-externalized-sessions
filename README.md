# JBoss Externalized Session Management

This project in an Example of externalized HTTP session from JBoss EAP to JBoss Data Grid in Openshift environment.

There is a simple counter that was incremented on every request and the value is preserved after a restart of the
application server that hosts the simple web app.

## Configuration
In order to use it in an xPaaS enviroment, like Openshift, you should perform the following steps:

### Install and configure the JBoss Data Grid
5) Create the JBoss Data Grid enviroment: $ oc new-app --template=datagrid-service -p CACHE_NAMES=http-session-cache -p MEMCACHED_CACHE=memcached
6) Scale to cluster size of 2 after ensuring the first single pod came up correctly: $ oc scale --replicas=2 dc datagrid-service

### Install and configure the JBoss EAP
7) Install JBoss EAP and deploy the application related to this repository: $ oc new-app --template=eap73-basic-s2i -p SOURCE_REPOSITORY_URL=https://git.nti.internal/parkers/jboss-externalized-sessions.git -p SOURCE_REPOSITORY_REF=main -p CONTEXT_DIR= -e JGROUPS_PING_PROTOCOL=dns.DNS_PING -e OPENSHIFT_DNS_PING_SERVICE_NAME=eap-app-ping -e OPENSHIFT_DNS_PING_SERVICE_PORT=8888 -e CACHE_NAME=http-session-cache
8) Scale to cluster size of 2 after ensuring the first single pod came up correctly: $ oc scale --replicas=2 dc eap-app

### Test the application
9) Test the application using the _{routePath}_/http-session-counter where _{routePath}_ is the result of the command $ oc get routes. The value of the counter should be 1.
10) Enable the Network tool of the Developer Tools and then re-run/refresh the same url, the counter is now set to 2.
11) Now capture the cURL command for the network call by right-clicking on the entry and choosing Copy as cURL.
12) Paste the cURL command in terminal and run it couple of times: you should obtain 3
13) Destroy and recreate the EAP pods: <br>
    >$ oc scale --replicas=0 dc eap-app (This will shutdown all the EAP pods) <br>
    > $oc scale --replicas=2 dc eap-app (This will create two fresh new EAP pods) <br>
14) Now go back to the window/terminal where you ran the step # 11 and re-run the same curl command. You should see the value incremented but the node name should be different.

## Important Considerations

## Links

### Github Repo
https://github.com/mvocale/http-session-counter-openshift

### EAP Clustering
https://docs.jboss.org/jbossas/docs/Clustering_Guide/beta422/html/clustering-http-app.html

### Upgrading EAP 7.1 to 7.3
https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.3/html-single/getting_started_with_jboss_eap_for_openshift_container_platform/index#updates-standalone-openshift-upgrade-eap-71-to-73_default
