<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="googleapils" scope="session" class="fr.paris.lutece.plugins.googleapi.web.GoogleApiLinkServiceJspBean" />

<% 
     response.sendRedirect( googleapils.doInsertLink( request ) );
%>
