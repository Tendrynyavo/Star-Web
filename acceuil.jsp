<%@ page import="composition.Composition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Composition[] compositions = Composition.getProduits(); // Prendre tous les produits
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./assets/css/bootstrap.css">
    <title>Fabrication</title>
</head>
<body>

    <%@ include file="header.jsp" %>

    <div class="container w-75">
        <table class="table rounded mt-5">
            <thead class="">
                <tr>
                    <th>Nom</th>
                    <th>Prix Unitaire</th>
                </tr>
            </thead>
            <tbody>
                <% for (Composition produit :compositions) { %>
                <tr>
                    <td><%=produit.getNom() %></td>
                    <td><%=produit.getPrixUnitaire() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>