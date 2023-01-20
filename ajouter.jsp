<%@ page import="composition.Composition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Composition[] produits = Composition.getProduitMatiere(); // Prendre tous les produits
    String error = request.getParameter("error");
    String valid = request.getParameter("valid");
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

    <%-- HEADER --%>
    <div class="container px-3">
        <ul class="nav m-3 p-3 bg-dark rounded shadow">
            <li class="nav-item">
                <a class="nav-link text-white" href="index.jsp">Produit</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="stock.jsp">Stock</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="ajouter.jsp">Ajouter</a>
            </li>
        </ul>
    </div>
    <%-- HEADER --%>

    <div class="container w-50 shadow-sm rounded p-4 mt-5">
        <form action="./traitement.jsp" method="post">
            <div class="mb-3">
                <label for="formGroupExampleInput" class="form-label">Produit</label>
                <select class="form-select" aria-label="Default select example" name="idComposant">
                    <% for (Composition produit : produits) { %>
                    <option value="<%=produit.getIdComposant() %>"><%=produit.getNom() %></option>
                    <% } %>
                </select>
            </div>
            <div class="mb-3">
                <label for="formGroupExampleInput2" class="form-label">Quantite</label>
                <input type="text" class="form-control" id="formGroupExampleInput2" placeholder="" name="quantite">
            </div>
            <div class="mb-3">
                <input type="submit" value="Produire" class="btn btn-outline-primary">
            </div>
        </form>
        <h3><%if (error != null) out.print(error); %></h3>
        <h3><%if (valid != null) out.print(valid); %></h3>
    </div>
</body>
</html>