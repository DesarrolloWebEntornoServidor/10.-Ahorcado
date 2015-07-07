package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

@WebServlet("/AhorcadoConJSP")
public class AhorcadoConJSP extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final int INTENTOS_MAXIMOS = 6;  // número máximo de intentos
	// array de palabras a adivinar
	private String[] arrayPalabras = new String[]{"avión", "cañería", "ungüento", "España"};
	
	protected void procesaSolicitud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession laSesion = request.getSession(false);  // vale null si no existía una sesión previa
		String accion = request.getServletPath().substring(1);
		List<String> mensajes = new ArrayList<String>();
		
		// se declaran las variales que se usarán en el programa y se inicializan
		String palabraOcultaOriginal = "";
		String palabraOculta = "";
		String mensajeInicial = "";
		String palabraResuelta = "";
		int numeroIntentosRestantes = 0;
		String letrasProbadas = "";
		String letraRecibida = "";
		String estiloDivLetra = "";
		
		if (laSesion != null) {  // había una sesión previa
			mensajes.add("Existe una sesión");
			palabraOculta = (String) laSesion.getAttribute("palabraOculta");
			if (palabraOculta != null)	{   // ...con la variable palabraOculta registrada
				mensajes.add("Existe una variable de sesión llamada palabraOculta = #" + palabraOculta + "#");
				// se recupera el estado del juego almacenado en variables de sesión
				palabraOcultaOriginal = (String)laSesion.getAttribute("palabraOcultaOriginal");
				palabraOculta = (String)laSesion.getAttribute("palabraOculta");
				palabraResuelta = (String)laSesion.getAttribute("palabraResuelta");
				numeroIntentosRestantes = ((Integer) laSesion.getAttribute("numeroIntentosRestantes")).intValue();
				letrasProbadas = (String)laSesion.getAttribute("letrasProbadas");
				  
				letraRecibida = request.getParameter("letra");
		  		if (letraRecibida != null) {  // se ha recibido un parámetro de nombre letra
		  			mensajes.add("Se ha recibido un parámetro llamado letra");
		  			if (!letraRecibida.isEmpty()) {  // ... y no venía vacío
		  				mensajes.add("El parámetro letra es no vacío");
		  				mensajes.add("La letra recibida antes de procesarla es " + letraRecibida);
		  				letraRecibida = procesaLetraRecibida(letraRecibida);
		  				mensajes.add("La letra recibida tras procesarla es " + letraRecibida);
		  				if ((numeroIntentosRestantes-1) > 0) {  // se han jugado de 1 a INTENTOS_MAXIMOS-1 rondas
		  					mensajes.add("Hasta esta ronda quedaban " + numeroIntentosRestantes + " posibles fallos antes de perder");
		  					mensajeInicial += "La letra recibida es " + letraRecibida + " <br /> \n";
		  					if (compruebaLetraYaProbada(letrasProbadas, letraRecibida)) {  // la letra ya se había probado
		  						mensajes.add("La letra " + letraRecibida + " ya se había probado, inténtelo de nuevo");  // no se contabiliza el intento
		  						mensajeInicial += "La letra " + letraRecibida + " ya se había probado, inténtelo de nuevo" + " <br /> \n";
		  					} else {  // la letra no se había probado previamente
		  						letrasProbadas += letraRecibida;  // se añade la letra recibida a la lista de probadas
		  						if (!letraPerteneceAPalabra(letraRecibida, palabraOculta)) {  // FALLO: la letra recibida no pertenece a la palabra oculta
		  							mensajes.add("La letra <b>" + letraRecibida + "</b> NO se encuentra en la palabra oculta");
		  							mensajeInicial += "La letra <b>" + letraRecibida + "</b> NO se encuentra en la palabra oculta" + " <br /> \n";
		  							numeroIntentosRestantes--;      	
		  							// se actualizan las variables de sesión
		  							almacenaVariablesEnSesion(laSesion, null, null, palabraResuelta,
		  										numeroIntentosRestantes, letrasProbadas);		  							
		  						} else {  // ACIERTO: la letra recibida pertenece a la palabra oculta
		  							mensajes.add("La letra <b>" + letraRecibida + "</b> SÍ se encuentra en la palabra oculta");
		  							mensajeInicial += "La letra <b>" + letraRecibida + "</b> SÍ se encuentra en la palabra oculta" + " <br /> \n";		
		  							int cuentaAciertosTotalesEnPalabra = 0;  // cuenta cuántas letras se han acertado entotal en la palabra
		  							int tamanioPalabraOculta = palabraOculta.length();
		  							for (int i = 0; i < tamanioPalabraOculta; i++) {
		  					            if (palabraOculta.substring(i, (i+1)).equals(letraRecibida)) {  // letraRecibida está en una posición de palabraOculta
		  					            	palabraResuelta = palabraResuelta.substring(0, i) + palabraOcultaOriginal.charAt(i) + palabraResuelta.substring(i+1);  // se modifica su valor en palabraResuelta
		  					            	cuentaAciertosTotalesEnPalabra++;
		  							    } else {  // letraRecibida no está en una posición de palabraOculta
		  							    	if (palabraResuelta.charAt(i) != '-') {  // esa letra ya se había acertado antes
		  							    		cuentaAciertosTotalesEnPalabra++;
		  							    	}		  							      
		  							    }
		  							}
		  						    if (cuentaAciertosTotalesEnPalabra == tamanioPalabraOculta) {  // se han acertado todas las letras de la palabra => has ganado
		  							    mensajes.add("Se han acertado todas las letras de la palabra => Has ganado");
		  						    	mensajeInicial = finalizaPartida("ganado", laSesion, palabraOcultaOriginal, palabraOculta, accion);
		  							    estiloDivLetra = "style=\"display: none;\""; 
		  						    } else {  // se actualizan las variables de sesión
		  						    	mensajes.add("Aún no se han acertado todas las letras de la palabra");
		  						    	almacenaVariablesEnSesion(laSesion, null, null, palabraResuelta, 
			  									numeroIntentosRestantes, letrasProbadas);			
		  							}
		  						}
		  					}
		  				} else {  // el anterior era el último intento => has perdido 
		  					mensajes.add("El anterior era el último intento => has perdido");
		  					numeroIntentosRestantes--;      	
		  					mensajeInicial = finalizaPartida("perdido", laSesion, palabraOcultaOriginal, palabraOculta, accion);
		  					estiloDivLetra = "style=\"display: none;\""; 
		  			    }
		  			} else {  // se ha recibido la letra vacía => se debe solicitar al usuario que la envíe de nuevo
		  				mensajes.add("Se ha recibido un parámetro letra vacío");
		  				mensajeInicial += "No ha introducido ninguna letra, inténtelo de nuevo";
		  			}
		  		} else {  // no se ha recibido el parámetro letra => es la primera ejecución
		  			mensajes.add("No se ha recibido el parámetro llamado letra");
		  			mensajeInicial += "No se ha enviado el parámetro letra";
			
		  		}
			} else {  // la sesión existía pero la variable de sesión palabraOculta no estaba registrada
				mensajes.add("La sesión existe pero no hay una variable de sesión de nombre palabraOculta => Aquí empieza una nueva partida");
				mensajeInicial = "Aquí empieza una nueva partida";
				palabraOcultaOriginal = generaPalabra(arrayPalabras);  // se genera una nueva palabra
				palabraOculta = limpiaPalabraOcultaOriginal(palabraOcultaOriginal);
				palabraResuelta = generaPalabraResuelta(palabraOculta);
				numeroIntentosRestantes = INTENTOS_MAXIMOS;
				letrasProbadas = "";
				almacenaVariablesEnSesion(laSesion, palabraOcultaOriginal, palabraOculta, palabraResuelta, 
						numeroIntentosRestantes, letrasProbadas);
			}
		} else {  // no había sesión => es la 1a ejecución
			mensajes.add("La sesión no existe => Aquí empieza una nueva partida");
			laSesion = request.getSession();  // se crea la sesión
			mensajeInicial = "Aquí empieza una nueva partida";
			palabraOcultaOriginal = generaPalabra(arrayPalabras);  // se genera una nueva palabra
			palabraOculta = limpiaPalabraOcultaOriginal(palabraOcultaOriginal);
			palabraResuelta = generaPalabraResuelta(palabraOculta);
			numeroIntentosRestantes = INTENTOS_MAXIMOS;
			letrasProbadas = "";
			almacenaVariablesEnSesion(laSesion, palabraOcultaOriginal, palabraOculta, palabraResuelta, 
						numeroIntentosRestantes, letrasProbadas);
		}
		
	

		String codigoCeldas = generaCeldas(palabraResuelta);
		String textoMensajes = generaTextoMensajes(mensajes);
		// aquí se pasan los atributos al JSP

		String vistaDestino = "/VistaAhorcado.jsp";
		request.setAttribute("textoMensajesLog", textoMensajes);
		request.setAttribute("mensajeInicial", mensajeInicial);
		request.setAttribute("codigoCeldas", codigoCeldas);
		request.setAttribute("estiloDivLetra", estiloDivLetra);
		request.setAttribute("accion", accion);
		request.setAttribute("palabraOcultaOriginal", palabraOcultaOriginal);
		request.setAttribute("palabraOculta", palabraOculta);
		request.setAttribute("letraRecibida", letraRecibida);
		request.setAttribute("letrasProbadas", letrasProbadas);
		request.setAttribute("numeroIntentosRestantes", numeroIntentosRestantes);
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(vistaDestino);
		dispatcher.forward(request,response);
	}

	
	// devuelve una palabra extraída aleatoriamente de una lista fija
	private String generaPalabra(String[] arrayPalabras) {
		Random rand = new Random(); 
		int posicionAleatoria = rand.nextInt(arrayPalabras.length);
		return arrayPalabras[posicionAleatoria];
	}

	// elimina de la palabra oculta original las tildes, diéresis...
	private String limpiaPalabraOcultaOriginal(String palabraOcultaOriginal) {
		String palabraOcultaLimpia = "";
		String[] caracteresOriginales = {"á", "é", "í", "ó", "ú", "ü"};
		String[] caracteresSustituidos = {"a", "e", "i", "o", "u", "u"};
		palabraOcultaLimpia = StringUtils.replaceEach(palabraOcultaOriginal, caracteresOriginales, caracteresSustituidos);
		return palabraOcultaLimpia;
	}
	
	// devuelve una cadena, de la misma longitud que palabraOculta,
	// en la que cada carácter es un guión (-)
	private String generaPalabraResuelta(String palabraOculta) {
	  String palabraResuelta= "";
	  int tamanioPalabraOculta = palabraOculta.length(); 
	  for (int i = 0; i < tamanioPalabraOculta; i++) {
	    palabraResuelta = palabraResuelta + "-";
	  }
	  return palabraResuelta;  
	}

	// generar el HTML que pinta las celdas de las letras de la palabra a adivivar
	private String generaCeldas(String palabraResuelta) {
	  String celdas = "<table border=\"1\">" + "\n" + "<tr>" + "\n";
	  int tamanioPalabraResuelta = palabraResuelta.length();
	  for (int i = 0; i < tamanioPalabraResuelta; i++) {
	    celdas += "  <td align=\"center\"><input name=\"l" + i + "\" size=\"1\" value=\"";
		char letraAComprobar = palabraResuelta.charAt(i);
		if (letraAComprobar == '-') {  // la letra en esta posición del array todavía no ha sido adivinada
		  celdas += "-";            // no se muestra
		} else {                           // la letra en esta posición del array ya ha sido adivinada
		  celdas += letraAComprobar;  // se muestra
		}  
		celdas += "\" readonly=\"readonly\" />";  
		celdas += "</td>" + "\n";
	  }
	  celdas += "</tr>" + "\n" + "</table>" + "\n";
	  return celdas;
	}
	
	// se procesa la letra recibida para quitarle tildes, diéresis...
	private String procesaLetraRecibida(String letraRecibida) {
		String letraResultante = "";
		// se pasa a minúscula la letra recibida
		letraResultante = letraRecibida.toLowerCase();
		// se le quitan tildes
		String[] caracteresOriginales = {"á", "é", "í", "ó", "ú", "ü"};
		String[] caracteresSustituidos = {"a", "e", "i", "o", "u", "u"};
		letraResultante = StringUtils.replaceEach(letraResultante, caracteresOriginales, caracteresSustituidos);
		return letraResultante;
		}
	
	// almacena variables de sesión
	private void almacenaVariablesEnSesion(HttpSession laSesion, String palabraOcultaOriginal, 
							String palabraOculta, String palabraResuelta, 
							int numeroIntentosRestantes, String letrasProbadas) {
		if (palabraOcultaOriginal != null) {
			laSesion.setAttribute("palabraOcultaOriginal", palabraOcultaOriginal);	
		}
		if (palabraOculta != null) {
			laSesion.setAttribute("palabraOculta", palabraOculta);	
		}		
		laSesion.setAttribute("palabraResuelta", palabraResuelta);
		laSesion.setAttribute("numeroIntentosRestantes", numeroIntentosRestantes);
		laSesion.setAttribute("letrasProbadas", letrasProbadas);
	}
	
	// tratar el final de la partida
	private String finalizaPartida(String resultado, HttpSession laSesion, String palabraOcultaOriginal, 
							String palabraOculta, String accion) {
		laSesion.invalidate();  // se destruye la sesión
		laSesion = null;		
		String mensajeInicial = "";
		mensajeInicial += "<b>¡¡¡ Has " + resultado + " !!!" + "</b> <br />";
		mensajeInicial += "La palabra oculta original era: <b>" + palabraOcultaOriginal.toUpperCase() + "</b> <br />";
		mensajeInicial += "<a href=\"" + accion + "\">Volver a jugar</a>" + " <br />";
		return mensajeInicial;
	}
	
	// comprueba que una letra recibida se hubiera probado ya previamente
	private boolean compruebaLetraYaProbada(String letrasProbadas, String letraRecibida) {
		if (letrasProbadas.indexOf(letraRecibida) == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	// comprueba que una letra recibida pertenezca a una palabra (String) 
	// es la misma función que compruebaLetraYaProbada
	private boolean letraPerteneceAPalabra(String letra, String palabra) {
		if (palabra.indexOf(letra) == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// recorre una lista de mensajes en un objeto List, formateándolos para su impresión en HTML
	private String generaTextoMensajes(List<String> mensajes) {
		String salida = "";
		for (String mensaje: mensajes) {
			salida += mensaje + " <br /> \n"; 
		}
		return salida;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		procesaSolicitud(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		procesaSolicitud(request, response);
	}

}