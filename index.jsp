<%@ page import="composition.Composition" %>
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
    <div class="container px-3">
        <ul class="nav m-3 p-3 bg-dark rounded shadow">
            <li class="nav-item">
                <a class="nav-link text-white" href="index.jsp">Produit</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="stock.jsp">Stock</a>
            </li>
        </ul>
    </div>
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
    <%-- <h2 class="text-center">Prix Unitaire des Produits</h2> --%>
</body>
</html>