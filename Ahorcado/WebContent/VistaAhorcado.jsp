<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	// aquí se reciben todos los atributos enviados desde el servlet
  	String textoMensajesLog = (String) request.getAttribute("textoMensajesLog");
	String mensajeInicial = (String) request.getAttribute("mensajeInicial");
	String codigoCeldas = (String) request.getAttribute("codigoCeldas");
	String estiloDivLetra = (String) request.getAttribute("estiloDivLetra");
	String accion = (String) request.getAttribute("accion");
	String palabraOcultaOriginal = (String) request.getAttribute("palabraOcultaOriginal");
	String palabraOculta = (String) request.getAttribute("palabraOculta");
	String letraRecibida = (String) request.getAttribute("letraRecibida");
	String letrasProbadas = (String) request.getAttribute("letrasProbadas");
	int numeroIntentosRestantes = (Integer) request.getAttribute("numeroIntentosRestantes");
	
%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>El ahorcado</title>
</head>	
<body onload="document.getElementById('letra').focus();">
<h1>El ahorcado</h1>
<% if (textoMensajesLog != null) { %>
<hr />
<h4>Mensajes de log</h4>
<%=textoMensajesLog  %>
<hr />
<% } %>
<%=mensajeInicial  %>
<br /><br />
<%=codigoCeldas  %>
<br />
<div id="introLetra" <%=estiloDivLetra %>>
<form name="formAhorcado" action="<%=accion %>"> 
  Introduce una letra <input type="text" name="letra" id="letra" size="1" maxlength="1" />
  <input type="submit" value="Prueba letra" />
</form>
</div>
<br />
<div id="mensajes" style="border: 3px black solid; width: 400px;">
  <div id="palabraOcultaOriginal">Palabra oculta original: <%=palabraOcultaOriginal  %></div>
  <div id="palabraOcultaPelada">Palabra oculta pelada: <%=palabraOculta%></div>
  <div id="letraRecibida">Última letra recibida: <%=letraRecibida  %></div>
  <div id="letrasProbadas">Letras ya probadas: <%=letrasProbadas %></div>
  <div id="numeroIntentos">Número intentos: <%=letrasProbadas.length() %></div>
  <div id="numeroIntentosRestantes">Número de intentos fallidos que quedan: <%=numeroIntentosRestantes %></div>
</div>
<a href="DestruyeSesion">Destruye la sesión</a>
<br />
<a href="javascript:window.close();">Cerrar la ventana</a>
</body>
</html>