package com.fitaura.filter;

import com.fitaura.util.AppConstants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * CharacterEncodingFilter guarantees UTF-8 encoding across all HTTP requests and responses.
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = {"/*"})
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(AppConstants.DEFAULT_CHARSET);
        response.setCharacterEncoding(AppConstants.DEFAULT_CHARSET);
        chain.doFilter(request, response);
    }
}
