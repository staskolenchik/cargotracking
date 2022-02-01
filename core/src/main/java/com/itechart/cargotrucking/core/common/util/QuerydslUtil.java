package com.itechart.cargotrucking.core.common.util;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collections;
import java.util.List;

public class QuerydslUtil extends QuerydslRepositorySupport {
    public QuerydslUtil(Class<?> domainClass) {
        super(domainClass);
    }

    protected <T> Page<T> fetch(JPQLQuery<T> query, Pageable pageable) {
        long total = query.fetchCount();
        List<T> content = Collections.emptyList();

        if (total != 0) {
            query = getQuerydsl().applyPagination(pageable, query);
            content = query.fetch();
        }

        return new PageImpl<T>(content, pageable, total);
    }
}
