package com.fitaura.controller;

import com.fitaura.model.DailyQuote;
import com.fitaura.service.QuoteService;
import com.fitaura.service.impl.QuoteServiceImpl;
import com.fitaura.util.CsrfUtil;
import com.fitaura.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "AdminQuoteServlet", urlPatterns = {"/admin/quotes", "/admin/quotes/*"})
public class AdminQuoteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminQuoteServlet.class);

    private QuoteService quoteService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.quoteService = new QuoteServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/edit")) {
            handleEditForm(request, response);
            return;
        }

        try {
            DailyQuote todayQuote = quoteService.getTodayQuote();
            List<DailyQuote> upcoming = quoteService.getUpcomingQuotes(15);
            List<DailyQuote> past = quoteService.getPastQuotes(15);
            List<DailyQuote> allQuotes = quoteService.getAllQuotes(50, 0);

            request.setAttribute("todayQuote", todayQuote);
            request.setAttribute("upcomingQuotes", upcoming);
            request.setAttribute("pastQuotes", past);
            request.setAttribute("allQuotes", allQuotes);
            request.setAttribute("totalQuotes", quoteService.countAllQuotes());

            request.getRequestDispatcher("/admin/quotes.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading admin quotes page: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to load quotes: " + e.getMessage());
            request.getRequestDispatcher("/admin/quotes.jsp").forward(request, response);
        }
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/quotes");
            return;
        }
        try {
            int quoteId = Integer.parseInt(idStr);
            DailyQuote quote = quoteService.getQuoteById(quoteId);
            request.setAttribute("editQuote", quote);
            request.getRequestDispatcher("/admin/quote-edit.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/quotes?error=QuoteNotFound");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        Integer adminUserId = SessionUtil.getAuthenticatedUserId(request);
        String clientIp = request.getRemoteAddr();
        String action = request.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                String text = request.getParameter("quoteText");
                String author = request.getParameter("authorName");
                String dateStr = request.getParameter("quoteDate");
                String statusStr = request.getParameter("status");

                DailyQuote quote = new DailyQuote();
                quote.setQuoteText(text != null ? text.trim() : "");
                quote.setAuthorName(author != null && !author.trim().isEmpty() ? author.trim() : "Anonymous");
                quote.setQuoteDate(Date.valueOf(dateStr));
                quote.setStatus(statusStr != null ? DailyQuote.Status.valueOf(statusStr) : DailyQuote.Status.PUBLISHED);

                quoteService.createQuote(quote, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=created");
                return;

            } else if ("update".equalsIgnoreCase(action)) {
                int quoteId = Integer.parseInt(request.getParameter("quoteId"));
                String text = request.getParameter("quoteText");
                String author = request.getParameter("authorName");
                String dateStr = request.getParameter("quoteDate");
                String statusStr = request.getParameter("status");

                DailyQuote quote = new DailyQuote();
                quote.setQuoteId(quoteId);
                quote.setQuoteText(text != null ? text.trim() : "");
                quote.setAuthorName(author != null && !author.trim().isEmpty() ? author.trim() : "Anonymous");
                quote.setQuoteDate(Date.valueOf(dateStr));
                quote.setStatus(statusStr != null ? DailyQuote.Status.valueOf(statusStr) : DailyQuote.Status.PUBLISHED);

                quoteService.updateQuote(quote, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=updated");
                return;

            } else if ("publish".equalsIgnoreCase(action)) {
                int quoteId = Integer.parseInt(request.getParameter("quoteId"));
                quoteService.publishQuote(quoteId, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=published");
                return;

            } else if ("archive".equalsIgnoreCase(action)) {
                int quoteId = Integer.parseInt(request.getParameter("quoteId"));
                quoteService.archiveQuote(quoteId, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=archived");
                return;

            } else if ("delete".equalsIgnoreCase(action)) {
                int quoteId = Integer.parseInt(request.getParameter("quoteId"));
                quoteService.deleteQuote(quoteId, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=deleted");
                return;

            } else if ("setToday".equalsIgnoreCase(action)) {
                int quoteId = Integer.parseInt(request.getParameter("quoteId"));
                quoteService.setAsTodayQuote(quoteId, adminUserId, clientIp);
                response.sendRedirect(request.getContextPath() + "/admin/quotes?success=todaySet");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/quotes");

        } catch (Exception e) {
            logger.warn("Admin quote action '{}' failed: {}", action, e.getMessage());
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/quotes?error=" + e.getMessage());
        }
    }
}
