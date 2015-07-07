package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DestruyeSesion")
public class DestruyeSesion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession laSesion = request.getSession(false);
		if (laSesion == null) {
			out.println("La sesión no existía, por lo que no se puede destruir" + " <br />");
		} else {
			out.println("La sesión existía, conteniendo las siguientes variables" + " <br />");
			Enumeration<String> listaVariablesSesion = laSesion.getAttributeNames();
			while(listaVariablesSesion.hasMoreElements()) {
				String nombreVariableSesion = (String) listaVariablesSesion.nextElement();
				// Object valorVariableSesion = laSesion.getAttribute(nombreVariableSesion);
				// out.println(nombreVariableSesion + " = #" + valorVariableSesion + "#" + " <br />");
				out.println(nombreVariableSesion + " <br />");
			}
			out.println("La sesión se desruirá" + " <br />");
			laSesion.invalidate();
			laSesion = null;			
		}
		out.println("<a href=\"Ahorcado\">Volver al ahorcado</a>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
