<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page language="java" import="java.util.ArrayList"%>
    <%@ page language="java" import="java.util.HashMap"%>
    <%@ page language="java" import="java.util.Date"%>
    <%@ page language="java" import="java.util.Calendar"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Session</title>
</head>
<body>
<h2>Hello World!</h2>
<% 	session.setAttribute("ip", request.getRemoteHost());%>
</body>
</html>
