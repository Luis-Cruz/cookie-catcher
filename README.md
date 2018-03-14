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
