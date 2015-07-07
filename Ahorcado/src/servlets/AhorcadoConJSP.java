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
	private final int INTENTOS_MAXIMOS = 6;  // n�mero m�ximo de intentos
	// array de palabras a adivinar
	private String[] arrayPalabras = new String[]{"avi�n", "ca�er�a", "ung�ento", "Espa�a"};
	
	protected void procesaSolicitud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession laSesion = request.getSession(false);  // vale null si no exist�a una sesi�n previa
		String accion = request.getServletPath().substring(1);
		List<String> mensajes = new ArrayList<String>();
		
		// se declaran las variales que se usar�n en el programa y se inicializan
		String palabraOcultaOriginal = "";
		String palabraOculta = "";
		String mensajeInicial = "";
		String palabraResuelta = "";
		int numeroIntentosRestantes = 0;
		String letrasProbadas = "";
		String letraRecibida = "";
		String estiloDivLetra = "";
		
		if (laSesion != null) {  // hab�a una sesi�n previa
			mensajes.add("Existe una sesi�n");
			palabraOculta = (String) laSesion.getAttribute("palabraOculta");
			if (palabraOculta != null)	{   // ...con la variable palabraOculta registrada
				mensajes.add("Existe una variable de sesi�n llamada palabraOculta = #" + palabraOculta + "#");
				// se recupera el estado del juego almacenado en variables de sesi�n
				palabraOcultaOriginal = (String)laSesion.getAttribute("palabraOcultaOriginal");
				palabraOculta = (String)laSesion.getAttribute("palabraOculta");
				palabraResuelta = (String)laSesion.getAttribute("palabraResuelta");
				numeroIntentosRestantes = ((Integer) laSesion.getAttribute("numeroIntentosRestantes")).intValue();
				letrasProbadas = (String)laSesion.getAttribute("letrasProbadas");
				  
				letraRecibida = request.getParameter("letra");
		  		if (letraRecibida != null) {  // se ha recibido un par�metro de nombre letra
		  			mensajes.add("Se ha recibido un par�metro llamado letra");
		  			if (!letraRecibida.isEmpty()) {  // ... y no ven�a vac�o
		  				mensajes.add("El par�metro letra es no vac�o");
		  				mensajes.add("La letra recibida antes de procesarla es " + letraRecibida);
		  				letraRecibida = procesaLetraRecibida(letraRecibida);
		  				mensajes.add("La letra recibida tras procesarla es " + letraRecibida);
		  				if ((numeroIntentosRestantes-1) > 0) {  // se han jugado de 1 a INTENTOS_MAXIMOS-1 rondas
		  					mensajes.add("Hasta esta ronda quedaban " + numeroIntentosRestantes + " posibles fallos antes de perder");
		  					mensajeInicial += "La letra recibida es " + letraRecibida + " <br /> \n";
		  					if (compruebaLetraYaProbada(letrasProbadas, letraRecibida)) {  // la letra ya se hab�a probado
		  						mensajes.add("La letra " + letraRecibida + " ya se hab�a probado, int�ntelo de nuevo");  // no se contabiliza el intento
		  						mensajeInicial += "La letra " + letraRecibida + " ya se hab�a probado, int�ntelo de nuevo" + " <br /> \n";
		  					} else {  // la letra no se hab�a probado previamente
		  						letrasProbadas += letraRecibida;  // se a�ade la letra recibida a la lista de probadas
		  						if (!letraPerteneceAPalabra(letraRecibida, palabraOculta)) {  // FALLO: la letra recibida no pertenece a la palabra oculta
		  							mensajes.add("La letra <b>" + letraRecibida + "</b> NO se encuentra en la palabra oculta");
		  							mensajeInicial += "La letra <b>" + letraRecibida + "</b> NO se encuentra en la palabra oculta" + " <br /> \n";
		  							numeroIntentosRestantes--;      	
		  							// se actualizan las variables de sesi�n
		  							almacenaVariablesEnSesion(laSesion, null, null, palabraResuelta,
		  										numeroIntentosRestantes, letrasProbadas);		  							
		  						} else {  // ACIERTO: la letra recibida pertenece a la palabra oculta
		  							mensajes.add("La letra <b>" + letraRecibida + "</b> S� se encuentra en la palabra oculta");
		  							mensajeInicial += "La letra <b>" + letraRecibida + "</b> S� se encuentra en la palabra oculta" + " <br /> \n";		
		  							int cuentaAciertosTotalesEnPalabra = 0;  // cuenta cu�ntas letras se han acertado entotal en la palabra
		  							int tamanioPalabraOculta = palabraOculta.length();
		  							for (int i = 0; i < tamanioPalabraOculta; i++) {
		  					            if (palabraOculta.substring(i, (i+1)).equals(letraRecibida)) {  // letraRecibida est� en una posici�n de palabraOculta
		  					            	palabraResuelta = palabraResuelta.substring(0, i) + palabraOcultaOriginal.charAt(i) + palabraResuelta.substring(i+1);  // se modifica su valor en palabraResuelta
		  					            	cuentaAciertosTotalesEnPalabra++;
		  							    } else {  // letraRecibida no est� en una posici�n de palabraOculta
		  							    	if (palabraResuelta.charAt(i) != '-') {  // esa letra ya se hab�a acertado antes
		  							    		cuentaAciertosTotalesEnPalabra++;
		  							    	}		  							      
		  							    }
		  							}
		  						    if (cuentaAciertosTotalesEnPalabra == tamanioPalabraOculta) {  // se han acertado todas las letras de la palabra => has ganado
		  							    mensajes.add("Se han acertado todas las letras de la palabra => Has ganado");
		  						    	mensajeInicial = finalizaPartida("ganado", laSesion, palabraOcultaOriginal, palabraOculta, accion);
		  							    estiloDivLetra = "style=\"display: none;\""; 
		  						    } else {  // se actualizan las variables de sesi�n
		  						    	mensajes.add("A�n no se han acertado todas las letras de la palabra");
		  						    	almacenaVariablesEnSesion(laSesion, null, null, palabraResuelta, 
			  									numeroIntentosRestantes, letrasProbadas);			
		  							}
		  						}
		  					}
		  				} else {  // el anterior era el �ltimo intento => has perdido 
		  					mensajes.add("El anterior era el �ltimo intento => has perdido");
		  					numeroIntentosRestantes--;      	
		  					mensajeInicial = finalizaPartida("perdido", laSesion, palabraOcultaOriginal, palabraOculta, accion);
		  					estiloDivLetra = "style=\"display: none;\""; 
		  			    }
		  			} else {  // se ha recibido la letra vac�a => se debe solicitar al usuario que la env�e de nuevo
		  				mensajes.add("Se ha recibido un par�metro letra vac�o");
		  				mensajeInicial += "No ha introducido ninguna letra, int�ntelo de nuevo";
		  			}
		  		} else {  // no se ha recibido el par�metro letra => es la primera ejecuci�n
		  			mensajes.add("No se ha recibido el par�metro llamado letra");
		  			mensajeInicial += "No se ha enviado el par�metro letra";
			
		  		}
			} else {  // la sesi�n exist�a pero la variable de sesi�n palabraOculta no estaba registrada
				mensajes.add("La sesi�n existe pero no hay una variable de sesi�n de nombre palabraOculta => Aqu� empieza una nueva partida");
				mensajeInicial = "Aqu� empieza una nueva partida";
				palabraOcultaOriginal = generaPalabra(arrayPalabras);  // se genera una nueva palabra
				palabraOculta = limpiaPalabraOcultaOriginal(palabraOcultaOriginal);
				palabraResuelta = generaPalabraResuelta(palabraOculta);
				numeroIntentosRestantes = INTENTOS_MAXIMOS;
				letrasProbadas = "";
				almacenaVariablesEnSesion(laSesion, palabraOcultaOriginal, palabraOculta, palabraResuelta, 
						numeroIntentosRestantes, letrasProbadas);
			}
		} else {  // no hab�a sesi�n => es la 1a ejecuci�n
			mensajes.add("La sesi�n no existe => Aqu� empieza una nueva partida");
			laSesion = request.getSession();  // se crea la sesi�n
			mensajeInicial = "Aqu� empieza una nueva partida";
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
		// aqu� se pasan los atributos al JSP

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

	
	// devuelve una palabra extra�da aleatoriamente de una lista fija
	private String generaPalabra(String[] arrayPalabras) {
		Random rand = new Random(); 
		int posicionAleatoria = rand.nextInt(arrayPalabras.length);
		return arrayPalabras[posicionAleatoria];
	}

	// elimina de la palabra oculta original las tildes, di�resis...
	private String limpiaPalabraOcultaOriginal(String palabraOcultaOriginal) {
		String palabraOcultaLimpia = "";
		String[] caracteresOriginales = {"�", "�", "�", "�", "�", "�"};
		String[] caracteresSustituidos = {"a", "e", "i", "o", "u", "u"};
		palabraOcultaLimpia = StringUtils.replaceEach(palabraOcultaOriginal, caracteresOriginales, caracteresSustituidos);
		return palabraOcultaLimpia;
	}
	
	// devuelve una cadena, de la misma longitud que palabraOculta,
	// en la que cada car�cter es un gui�n (-)
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
		if (letraAComprobar == '-') {  // la letra en esta posici�n del array todav�a no ha sido adivinada
		  celdas += "-";            // no se muestra
		} else {                           // la letra en esta posici�n del array ya ha sido adivinada
		  celdas += letraAComprobar;  // se muestra
		}  
		celdas += "\" readonly=\"readonly\" />";  
		celdas += "</td>" + "\n";
	  }
	  celdas += "</tr>" + "\n" + "</table>" + "\n";
	  return celdas;
	}
	
	// se procesa la letra recibida para quitarle tildes, di�resis...
	private String procesaLetraRecibida(String letraRecibida) {
		String letraResultante = "";
		// se pasa a min�scula la letra recibida
		letraResultante = letraRecibida.toLowerCase();
		// se le quitan tildes
		String[] caracteresOriginales = {"�", "�", "�", "�", "�", "�"};
		String[] caracteresSustituidos = {"a", "e", "i", "o", "u", "u"};
		letraResultante = StringUtils.replaceEach(letraResultante, caracteresOriginales, caracteresSustituidos);
		return letraResultante;
		}
	
	// almacena variables de sesi�n
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
		laSesion.invalidate();  // se destruye la sesi�n
		laSesion = null;		
		String mensajeInicial = "";
		mensajeInicial += "<b>��� Has " + resultado + " !!!" + "</b> <br />";
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
	// es la misma funci�n que compruebaLetraYaProbada
	private boolean letraPerteneceAPalabra(String letra, String palabra) {
		if (palabra.indexOf(letra) == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// recorre una lista de mensajes en un objeto List, formate�ndolos para su impresi�n en HTML
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