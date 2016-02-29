<%@page import="testmann.Analyse"%>
<html>
<body>
<h1>!!Output Sheet!!</h1>
<ul>
<li><p><b>Results:</b>
<br>
   <%
   //out.println(Analyse.test());
   out.println(Analyse.doCommand(request.getParameter("operation"),request.getParameter("set")));
   %>
</p></li>
</ul>
</body>
</html>
