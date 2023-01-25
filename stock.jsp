<%@ page import="composition.Composition" %>
<%@ page import="stock.Stock" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%

    Stock[] stocks = Composition.getAllStocks();
    Composition[] matieres = Composition.getMatierePremiere(); // Prendre tous les matieres premieres
    Composition[] produits = Composition.getProduits(); // Prendre tous les produits
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
        <div class="row">
            <div class="col">
                <h1 class="">Stock de Produit</h1>
                <table class="table rounded mt-5 w-75">
                    <thead class="">
                        <tr>
                            <th>Nom</th>
                            <th>Prix Unitaire</th>
                            <th>PUMP</th>
                            <th>Valeur de stock</th>
                            <th>Quantite total</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Composition produit : produits) { %>
                        <tr>
                            <td><%=produit.getNom() %></td>
                            <td><%=produit.getPrixUnitaire() %></td>
                            <td><%=produit.getCump(stocks) %></td>
                            <td><%=produit.getValeurStock(stocks) %></td>
                            <td><%=produit.getQuantiteStock(stocks) %></td>
                            <td><a href="detail.jsp?id=<%=produit.getIdComposant() %>"><button class="btn btn-outline-primary">Detail</button></a></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <div class="col">
                <h1 class="">Stock de Matière Première</h1>
                <table class="table rounded mt-5 w-75">
                    <thead class="">
                        <tr>
                            <th>Nom</th>
                            <th>Prix Unitaire</th>
                            <th>PUMP</th>
                            <th>Valeur de stock</th>
                            <th>Quantite total</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Composition matiere :matieres) { %>
                        <tr>
                            <td><%=matiere.getNom() %></td>
                            <td><%=matiere.getPrixUnitaire() %></td>
                            <td><%=matiere.getCump(stocks) %></td>
                            <td><%=matiere.getValeurStock(stocks) %></td>
                            <td><%=matiere.getQuantiteStock(stocks) %></td>
                            <td><a href="detail.jsp?id=<%=matiere.getIdComposant() %>"><button class="btn btn-outline-primary">Detail</button></a></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>