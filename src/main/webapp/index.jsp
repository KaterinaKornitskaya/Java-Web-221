<%--
  Created by IntelliJ IDEA.
  User: Alex
  Date: 01.02.2025
  Time: 5:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>JSP</title>
</head>
<body>
  <h1>JSP</h1>
  <h2><a href="Home">Перейти на сторінку home</a></h2>
  <h2><a href="time">Перейти на сторінку time</a></h2>
  <h2>Вирази</h2>
  <%= 2 + 3 %>
  <h2>Змінні</h2>
  <%
    int x = 10;
  %>
  <%= x %>
  <h2>Інструкції управління (умови, цикли)</h2>
  <% if( x % 2 == 0) { %>
    <b>Число <%= x %> парне</b>
  <% } else { %>
    <i>Число <%= x %> не парне</i>
  <% } %>

  <ul>
  <% for (int i = 0; i < x; i++) { %>
      <li><%= i + 1 %></li>
  <% } %>
  </ul>

</body>
</html>
