package com.fitaura.dao;

import com.fitaura.model.DailyQuote;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface DailyQuoteDAO {

    Optional<DailyQuote> findTodayQuote(Date today);

    Optional<DailyQuote> findById(Integer quoteId);

    List<DailyQuote> findUpcomingQuotes(Date today, int limit);

    List<DailyQuote> findPastQuotes(Date today, int limit);

    List<DailyQuote> findAll(int limit, int offset);

    int countAll();

    Integer create(DailyQuote quote);

    boolean update(DailyQuote quote);

    boolean delete(Integer quoteId);

    boolean existsPublishedForDate(Date date, Integer excludeQuoteId);
}
