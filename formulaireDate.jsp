<%@ page import="composition.Composition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String traitement = request.getParameter("traitement");
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

    <div class="container w-50 shadow-sm rounded p-4 mt-5">
        <form action="<%=traitement %>.jsp" method="post">
            <div class="mb-3">
                <label for="formGroupExampleInput2" class="form-label">Date</label>
                <input type="text" class="form-control" id="formGroupExampleInput2" placeholder="" name="date">
            </div>
            <div class="mb-3">
                <input type="submit" value="Valider" class="btn btn-outline-primary">
            </div>
        </form>
    </div>
</body>
</html>