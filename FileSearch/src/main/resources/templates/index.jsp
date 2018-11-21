<%--
  Created by IntelliJ IDEA.
  User: huochao2
  Date: 2018/11/16
  Time: 14:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"
    import="java.util.ArrayList" %>
<%@ page import="java.util.regex.Pattern" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName()+
            ":" + request.getServerPort()+ path + "/";
    String regEx_html = "<[^>]+>";
    //创建Pattern对象
    Pattern r = Pattern.compile(regEx_html);
    //创建match对象


%>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
