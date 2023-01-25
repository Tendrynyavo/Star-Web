<%@ page import="magasin.Magasin" %>
<%
    session.setAttribute("magasin", new Magasin());
    response.sendRedirect("acceuil.jsp");
%>