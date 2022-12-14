package fr.eni.encheres.tests;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.eni.encheres.bll.ArticleManager;
import fr.eni.encheres.bll.BLLException;
import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.dal.ArticleDAO;
import fr.eni.encheres.dal.DALException;

/**
 * Servlet implementation class TesterInsertArticleBLL
 */
@WebServlet("/TesterInsertArticleBLL")
public class TesterInsertArticleBLL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TesterInsertArticleBLL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "";
		ArticleManager article = ArticleManager.getInstance();
		
		String nom = "ballon";
		String description = "C'est un ballon rouge";
		LocalDateTime dateDebut = LocalDateTime.now();
		LocalDateTime dateFin = LocalDateTime.now();
		int miseAPrix = 100;
		int noUser = 3;
		int noCat = 4;
		String img = null;
		
	//	ArticleVendu ballon = new ArticleVendu (nom, description, dateDebut, dateFin, miseAPrix, noUser, noCat, img);
		
		try {
	// TEST INSERT ARTICLE
//		article.insert(ballon);
//		message = "Insertion article réussie";
		
	// Test SELECTBYID Article
		
		ArticleVendu art = article.selectById(2);
		message = "Select article ok";
		System.out.println(art.getCategorie().getLibelle());
		
		}catch(BLLException e) {
			message += "\nErreur lors de l'insertion de l'article.";
			
			for (Exception bllex : ((BLLException) e).getBLLExceptions()) {
				System.out.println(bllex.getMessage());
				e.printStackTrace();
			}
		}
	
		response.getWriter().append("Served at: ")
							.append(request.getContextPath())
							.append(message);
	}

}
