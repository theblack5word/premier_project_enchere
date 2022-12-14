package fr.eni.encheres.controllers.article;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.encheres.bll.ArticleManager;
import fr.eni.encheres.bll.BLLException;
import fr.eni.encheres.bll.EnchereManager;
import fr.eni.encheres.bll.UtilisateurManager;
import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;

/**
 * Servlet implementation class encherirServlet
 */
@WebServlet("/article/encherir")
public class EncherirServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EncherirServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/enchere/detailEnchere.jsp");
        rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd;
		ArticleManager articleManager = ArticleManager.getInstance();
		
		ArticleVendu article = null;
		
		int nouveauMontantEnchere = request.getParameter("montantEnchere")!=null ? Integer.parseInt(request.getParameter("montantEnchere")):null;
		HttpSession session = request.getSession();
		Utilisateur currentUser = session.getAttribute("user")!= null ? (Utilisateur)session.getAttribute("user"):null;
		UtilisateurManager userManager = UtilisateurManager.getInstance();
		EnchereManager enchereManager = EnchereManager.getInstance();
		List<ArticleVendu> listeEncheresEC = new ArrayList<ArticleVendu>();
		try {
			article = articleManager.selectById(Integer.valueOf(request.getParameter("noArticle")));
			if (nouveauMontantEnchere > currentUser.getCredit()) {
				throw new Exception ("votre ench??re pour l'article : " + article.getNom() + " est sup??rieure ?? votre cr??dit " );
			} else {
					if (article.enchereIsNull()) {
						if (nouveauMontantEnchere < article.getPrixInitial()) {
							throw new Exception("Votre ench??re est insufisante sur l'article : "+ article.getNom());
						} else {
							Enchere enchere = new Enchere(currentUser.getNoUtilisateur(),article.getNoArticle(),LocalDateTime.now(),nouveauMontantEnchere);
							article.setEnchere(enchere);
							enchereManager.insert(enchere);
							currentUser.setCredit(currentUser.getCredit()-nouveauMontantEnchere);
							session.setAttribute("user", currentUser);
							userManager.update(currentUser, currentUser);
						}
	
					} else {
						Enchere enchere = article.getEnchere();
						if (nouveauMontantEnchere <= enchere.getMontantEnchere()) {
							throw new Exception("Votre ench??re est insufisante sur l'article : "+ article.getNom());
						}
						Utilisateur oldUser = userManager.selectById(enchere.getIdUser());
						oldUser.setCredit(oldUser.getCredit()+ enchere.getMontantEnchere());
						enchere.setMontantEnchere(nouveauMontantEnchere);
						enchere.setIdUser(currentUser.getNoUtilisateur());
						enchereManager.update(enchere);
						userManager.update(oldUser);
						currentUser.setCredit(currentUser.getCredit()-nouveauMontantEnchere);
						userManager.update(currentUser);
						session.setAttribute("user", currentUser);
					}
			}
			request.setAttribute("success", "Votre ench??re est prise en compte sur l'article " + article.getNom() +", f??licitation !");
			listeEncheresEC = enchereManager.getListeEncheres();
			request.setAttribute("listeEncheresEC", listeEncheresEC);
			rd = request.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp");
			rd.forward(request, response);
		} catch (BLLException e) {
			e.printStackTrace();
			request.setAttribute("error", e);
		} catch (Exception e) {
			request.setAttribute("erreur", e);
			rd = request.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp");
			rd.forward(request, response);
		}
		
	}

}
