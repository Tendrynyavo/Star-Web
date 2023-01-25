<%@ page import="composition.Composition" %>
<%@ page import="stock.Stock" %>
<%@ page import="stock.EtatStock" %>
<%@ page import="magasin.Magasin" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Magasin magasin = (Magasin) session.getAttribute("magasin");
    String date = request.getParameter("date");
    EtatStock[] etats = magasin.getEtatStock(date);
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

    <div class="container w-75 mt-5">
        <table class="table rounded">
            <thead class="">
                <tr>
                    <th>Produit</th>
                    <th>Entrée</th>
                    <th>Sortie</th>
                    <th>Reste</th>
                    <th>Valeur</th>
                </tr>
            </thead>
            <tbody>
                <% for (EtatStock etat : etats) { %>
                <tr>
                    <td><%=etat.getProduit().getNom() %></td>
                    <td><%=etat.getEntree() %></td>
                    <td><%=etat.getSortie() %></td>
                    <td><%=etat.getReste() %></td>
                    <td><%=etat.getValeur() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>