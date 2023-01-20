<%@ page import="composition.Composition" %>
<%
    Composition composition = Composition.getCompositionById(request.getParameter("idComposant"));
    double quantite = Double.parseDouble(request.getParameter("quantite"));
    try {
        if (composition.getProduit()) {
            composition.fabriquer(quantite);
        } else {
            composition.add(quantite);
        }
        response.sendRedirect("./ajouter.jsp?valid=Insertion Done");
    } catch(Exception e) {
        response.sendRedirect("./ajouter.jsp?error=" + e.getMessage());
    }
%>