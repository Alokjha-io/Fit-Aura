package com.fitaura.service.impl;

import com.fitaura.dao.ActivityLogDAO;
import com.fitaura.dao.DailyQuoteDAO;
import com.fitaura.dao.UserDAO;
import com.fitaura.dao.impl.ActivityLogDAOImpl;
import com.fitaura.dao.impl.DailyQuoteDAOImpl;
import com.fitaura.dao.impl.UserDAOImpl;
import com.fitaura.exception.AuthorizationException;
import com.fitaura.exception.ResourceNotFoundException;
import com.fitaura.exception.ValidationException;
import com.fitaura.model.ActivityLog;
import com.fitaura.model.DailyQuote;
import com.fitaura.model.User;
import com.fitaura.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class QuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final DailyQuoteDAO quoteDAO;
    private final UserDAO userDAO;
    private final ActivityLogDAO activityLogDAO;

    public QuoteServiceImpl() {
        this(new DailyQuoteDAOImpl(), new UserDAOImpl(), new ActivityLogDAOImpl());
    }

    public QuoteServiceImpl(DailyQuoteDAO quoteDAO, UserDAO userDAO, ActivityLogDAO activityLogDAO) {
        this.quoteDAO = quoteDAO;
        this.userDAO = userDAO;
        this.activityLogDAO = activityLogDAO;
    }

    @Override
    public DailyQuote getTodayQuote() {
        Date today = Date.valueOf(LocalDate.now());
        Optional<DailyQuote> opt = quoteDAO.findTodayQuote(today);
        if (opt.isPresent()) {
            return opt.get();
        }

        // Graceful fallback when no database quote is scheduled for today
        DailyQuote fallback = new DailyQuote();
        fallback.setQuoteId(0);
        fallback.setQuoteText("Stay consistent. Every workout counts.");
        fallback.setAuthorName("FitAura Motivation");
        fallback.setQuoteDate(today);
        fallback.setStatus(DailyQuote.Status.PUBLISHED);
        return fallback;
    }

    @Override
    public DailyQuote getQuoteById(Integer quoteId) {
        if (quoteId == null) {
            throw new ValidationException("Quote ID is required.");
        }
        return quoteDAO.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote #" + quoteId + " not found."));
    }

    @Override
    public DailyQuote createQuote(DailyQuote quote, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        validateQuoteFields(quote);

        if (quote.getStatus() == DailyQuote.Status.PUBLISHED) {
            if (quoteDAO.existsPublishedForDate(quote.getQuoteDate(), null)) {
                throw new ValidationException("A published quote already exists for " + quote.getQuoteDate() +
                        ". Please choose a different date or save as DRAFT.");
            }
        }

        quote.setCreatedBy(adminUserId);
        Integer id = quoteDAO.create(quote);
        quote.setQuoteId(id);

        logActivity(adminUserId, "QUOTE_CREATED", id,
                "Created quote for " + quote.getQuoteDate() + " (status: " + quote.getStatus() + ")", clientIp);
        logger.info("Admin {} created quote #{} for date {}", adminUserId, id, quote.getQuoteDate());
        return quote;
    }

    @Override
    public DailyQuote updateQuote(DailyQuote quote, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        if (quote == null || quote.getQuoteId() == null) {
            throw new ValidationException("Quote ID is required for update.");
        }
        validateQuoteFields(quote);

        DailyQuote existing = getQuoteById(quote.getQuoteId());

        if (quote.getStatus() == DailyQuote.Status.PUBLISHED) {
            if (quoteDAO.existsPublishedForDate(quote.getQuoteDate(), quote.getQuoteId())) {
                throw new ValidationException("Another published quote already exists for " + quote.getQuoteDate() + ".");
            }
        }

        quoteDAO.update(quote);

        logActivity(adminUserId, "QUOTE_UPDATED", quote.getQuoteId(),
                "Updated quote #" + quote.getQuoteId() + " (status: " + quote.getStatus() + ")", clientIp);
        logger.info("Admin {} updated quote #{}", adminUserId, quote.getQuoteId());
        return quote;
    }

    @Override
    public boolean deleteQuote(Integer quoteId, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        DailyQuote existing = getQuoteById(quoteId);

        boolean deleted = quoteDAO.delete(quoteId);
        if (deleted) {
            logActivity(adminUserId, "QUOTE_DELETED", quoteId,
                    "Deleted quote #" + quoteId + " scheduled for " + existing.getQuoteDate(), clientIp);
            logger.info("Admin {} deleted quote #{}", adminUserId, quoteId);
        }
        return deleted;
    }

    @Override
    public boolean publishQuote(Integer quoteId, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        DailyQuote quote = getQuoteById(quoteId);

        if (quoteDAO.existsPublishedForDate(quote.getQuoteDate(), quoteId)) {
            throw new ValidationException("Another published quote already exists for date " + quote.getQuoteDate() + ".");
        }

        quote.setStatus(DailyQuote.Status.PUBLISHED);
        boolean updated = quoteDAO.update(quote);
        if (updated) {
            logActivity(adminUserId, "QUOTE_PUBLISHED", quoteId,
                    "Published quote #" + quoteId + " for date " + quote.getQuoteDate(), clientIp);
        }
        return updated;
    }

    @Override
    public boolean archiveQuote(Integer quoteId, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        DailyQuote quote = getQuoteById(quoteId);
        quote.setStatus(DailyQuote.Status.ARCHIVED);
        boolean updated = quoteDAO.update(quote);
        if (updated) {
            logActivity(adminUserId, "QUOTE_ARCHIVED", quoteId,
                    "Archived quote #" + quoteId, clientIp);
        }
        return updated;
    }

    @Override
    public boolean setAsTodayQuote(Integer quoteId, Integer adminUserId, String clientIp) {
        validateAdmin(adminUserId);
        DailyQuote quote = getQuoteById(quoteId);
        Date today = Date.valueOf(LocalDate.now());

        // Archive any previous published quote for today
        Optional<DailyQuote> previousToday = quoteDAO.findTodayQuote(today);
        if (previousToday.isPresent() && !previousToday.get().getQuoteId().equals(quoteId)) {
            DailyQuote prev = previousToday.get();
            prev.setStatus(DailyQuote.Status.ARCHIVED);
            quoteDAO.update(prev);
        }

        quote.setQuoteDate(today);
        quote.setStatus(DailyQuote.Status.PUBLISHED);
        boolean updated = quoteDAO.update(quote);
        if (updated) {
            logActivity(adminUserId, "QUOTE_SET_TODAY", quoteId,
                    "Marked quote #" + quoteId + " as today's active quote (" + today + ")", clientIp);
            logger.info("Admin {} set quote #{} as today's active quote", adminUserId, quoteId);
        }
        return updated;
    }

    @Override
    public List<DailyQuote> getUpcomingQuotes(int limit) {
        return quoteDAO.findUpcomingQuotes(Date.valueOf(LocalDate.now()), limit > 0 ? limit : 20);
    }

    @Override
    public List<DailyQuote> getPastQuotes(int limit) {
        return quoteDAO.findPastQuotes(Date.valueOf(LocalDate.now()), limit > 0 ? limit : 20);
    }

    @Override
    public List<DailyQuote> getAllQuotes(int limit, int offset) {
        return quoteDAO.findAll(limit, offset);
    }

    @Override
    public int countAllQuotes() {
        return quoteDAO.countAll();
    }

    private void validateAdmin(Integer userId) {
        if (userId == null) {
            throw new AuthorizationException("Authentication required.");
        }
        Optional<User> opt = userDAO.findById(userId);
        if (opt.isEmpty() || opt.get().getRole() != User.Role.ADMIN) {
            throw new AuthorizationException("Only administrators can manage daily motivational quotes.");
        }
    }

    private void validateQuoteFields(DailyQuote quote) {
        if (quote == null) {
            throw new ValidationException("Quote details cannot be empty.");
        }
        if (quote.getQuoteText() == null || quote.getQuoteText().trim().isEmpty()) {
            throw new ValidationException("Quote text cannot be blank.");
        }
        if (quote.getQuoteText().trim().length() > 1000) {
            throw new ValidationException("Quote text must not exceed 1,000 characters.");
        }
        if (quote.getAuthorName() != null && quote.getAuthorName().trim().length() > 150) {
            throw new ValidationException("Author name must not exceed 150 characters.");
        }
        if (quote.getQuoteDate() == null) {
            throw new ValidationException("Scheduled quote date is required.");
        }
        if (quote.getStatus() == null) {
            quote.setStatus(DailyQuote.Status.PUBLISHED);
        }
    }

    private void logActivity(Integer userId, String actionType, Integer entityId, String description, String ip) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setActionType(actionType);
            log.setEntityType("DAILY_QUOTE");
            log.setEntityId(entityId);
            log.setDescription(description);
            log.setIpAddress(ip);
            activityLogDAO.create(log);
        } catch (Exception e) {
            logger.warn("Failed to log activity {}: {}", actionType, e.getMessage());
        }
    }
}
