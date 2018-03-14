# cookie-catcher
Capture And Analyse Cookies To Find Potential Security Issues

## Overview
This project provides a web application filter to log cookie information.

If log level is set to INFO or higher, cookie names are logged.

If log level is set to DEBUG or higher, all cookie information is logged.

The class to set log level for is: pt.ist.servlet.CookieCatcher

After this is up and running in your application, analyse your logs to
find potential security issues. Some applications have been known to set
user session information in domain cookies available to other applications.
Other applications even place sensitive data in cookies.

## Building

```bash
mvn clean install
```

If you don't want to build this module, and wish to use our packaged jar,
you will need to configure our maven repo:

```xml
    <repository>
        <id>fenixedu-maven-repository</id>
        <url>https://repo.fenixedu.org/fenixedu-maven-repository</url>
    </repository>
```

## Using

Add the dependency in your project, module or application, adjusting the
version adequately:

```xml
   <dependency>
      <groupId>pt.ist</groupId>
      <artifactId>cookie-catcher</artifactId>
      <version>${version.pt.ist.cookie-catcher}</version>
   </dependency>
```
