<%@ page import="composition.Composition" %>
<%@ page import="stock.Stock" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Stock[] stocks = Composition.getAllStocks();
    Composition composition = Composition.getCompositionById(request.getParameter("id")); // Prendre tous les produits
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
        <h1 class="text-align"><%=composition.getNom() %></h1>
        <table class="table rounded">
            <thead class="">
                <tr>
                    <th>Date</th>
                    <th>Quantite</th>
                    <th>Prix Unitaire</th>
                    <th>Type</th>
                    <th>CUMP</th>
                    <th>Valeur en stock</th>
                </tr>
            </thead>
            <tbody>
                <% for (Stock stock :composition.getStockWithCump(stocks)) { %>
                <tr>
                    <td><%=stock.getDate() %></td>
                    <td><%=stock.getQuantite() %></td>
                    <td><%=stock.getPrixUnitaire() %></td>
                    <td><% out.print((stock.getSortie()) ? "Sortie" : "Entrée"); %></td>
                    <td><%=stock.getCump() %></td>
                    <td><%=stock.getValeurStock() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>