<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<jsp:useBean id="googleapils" scope="session" class="fr.paris.lutece.plugins.googleapi.web.GoogleApiLinkServiceJspBean" />

<%= googleapils.getSearch( request ) %>
