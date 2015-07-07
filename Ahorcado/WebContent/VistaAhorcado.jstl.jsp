<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>El ahorcado</title>
</head>	
<body onload="document.getElementById('letra').focus();">
<h1>El ahorcado</h1>

	<c:if test="${requestScope.textoMensajesLog ne null }">
		<hr />
		<h4>Mensajes de log</h4>
		<c:out value="${requestScope.textoMensajesLog}" escapeXml="false"></c:out>
		<hr />		
	</c:if>

<c:out value="${requestScope.mensajeInicial}" escapeXml="false"></c:out>

<br /><br />
<c:out value="${requestScope.codigoCeldas}" escapeXml="false"></c:out>
<br />
<div id="introLetra" <c:out value="${requestScope.estiloDivLetra}"></c:out>>
<form name="formAhorcado" action="<c:out value="${requestScope.accion}"></c:out>"> 
  Introduce una letra <input type="text" name="letra" id="letra" size="1" maxlength="1" />
  <input type="submit" value="Prueba letra" />
</form>
</div>
<br />
<div id="mensajes" style="border: 3px black solid; width: 400px;">
  <div id="palabraOcultaOriginal">Palabra oculta original: <c:out value="${requestScope.palabraOcultaOriginal}"></c:out></div>
  <div id="palabraOcultaPelada">Palabra oculta pelada: <c:out value="${requestScope.palabraOculta}"></c:out></div>
  <div id="letraRecibida">Última letra recibida: <c:out value="${requestScope.letraRecibida}"></c:out></div>
  <div id="letrasProbadas">Letras ya probadas: <c:out value="${requestScope.letrasProbadas}"></c:out></div>
  	<c:set var="letrasProbadas" value="${requestScope.letrasProbadas}" scope="request" />
   
  <div id="numeroIntentos">Número intentos: <c:out value="${fn:length(letrasProbadas)}"></c:out></div>
  <div id="numeroIntentosRestantes">Número de intentos fallidos que quedan: <c:out value="${requestScope.numeroIntentosRestantes}"></c:out></div>
</div>
<a href="DestruyeSesion">Destruye la sesión</a>
<br />
<a href="javascript:window.close();">Cerrar la ventana</a>
</body>
</html>