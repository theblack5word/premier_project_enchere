package fr.eni.encheres.controllers.utilisateur;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.encheres.bll.BLLException;
import fr.eni.encheres.bll.utilisateurManager;
import fr.eni.encheres.bo.Utilisateur;

/**
 * Servlet implementation class ConnecterServlet
 */
@WebServlet("/login")
public class ConnecterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConnecterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd;
		String deco = request.getParameter("deco");
		if (deco.equals("true") ){
			request.getSession().invalidate();
			rd = request.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp");
		}else {
			rd =request.getRequestDispatcher("/WEB-INF/jsp/utilisateur/connection.jsp");
		}
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		r�cup�ration des donn�es du formulaire
		
		String email = request.getParameter("email")!=null ?request.getParameter("email"):null ;
		String password = request.getParameter("password")!=null ? (String)request.getParameter("password"):null ;
				
		//		gestion de la case se souvenir par cookie : 
		Cookie cookie = new Cookie("identite", email);
		if (request.getParameter("memoriser")!=null) {
			cookie.setHttpOnly(true);
			cookie.setMaxAge(-1); 
			response.addCookie(cookie);
		} else {
			//sinon forcer la destruction du cookie c�t� client
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
		
		utilisateurManager mng = utilisateurManager.getInstance();
		RequestDispatcher rd;
		Utilisateur user =null;
		try {
			user = mng.selectById(email, password);
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			rd =request.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp");

		} catch (BLLException e) {
			rd =request.getRequestDispatcher("/WEB-INF/jsp/utilisateur/connection.jsp");
			request.setAttribute("error", e);
		}
		
		rd.forward(request, response);
		
	}

}