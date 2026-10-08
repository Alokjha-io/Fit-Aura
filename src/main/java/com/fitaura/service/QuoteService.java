package com.fitaura.service;

import com.fitaura.model.DailyQuote;

import java.util.List;

public interface QuoteService {

    DailyQuote getTodayQuote();

    DailyQuote getQuoteById(Integer quoteId);

    DailyQuote createQuote(DailyQuote quote, Integer adminUserId, String clientIp);

    DailyQuote updateQuote(DailyQuote quote, Integer adminUserId, String clientIp);

    boolean deleteQuote(Integer quoteId, Integer adminUserId, String clientIp);

    boolean publishQuote(Integer quoteId, Integer adminUserId, String clientIp);

    boolean archiveQuote(Integer quoteId, Integer adminUserId, String clientIp);

    boolean setAsTodayQuote(Integer quoteId, Integer adminUserId, String clientIp);

    List<DailyQuote> getUpcomingQuotes(int limit);

    List<DailyQuote> getPastQuotes(int limit);

    List<DailyQuote> getAllQuotes(int limit, int offset);

    int countAllQuotes();
}
