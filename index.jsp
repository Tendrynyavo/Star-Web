<%@ page import="magasin.Magasin" %>
<%
    session.setAttribute("magasin", new Magasin("M1"));
    response.sendRedirect("acceuil.jsp");
%>