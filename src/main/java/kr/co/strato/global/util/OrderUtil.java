package kr.co.strato.global.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import org.springframework.data.domain.Sort;

/**
 * queryDSL order by 관련 OrderSpecifier 생성 유틸
 * @param <T> : order by 절에 지정할 Q객체
 */
public class OrderUtil<T> {
    public static <T> OrderSpecifier<?>[] getOrderSpecifier(T t, Sort sorts){
        return sorts.toList().stream().map(x ->{
            Order order = x.getDirection().name() == "ASC" ? Order.ASC : Order.DESC;
            SimplePath<Object> filedPath = Expressions.path(Object.class, (Path<?>) t, x.getProperty());
            return new OrderSpecifier(order, filedPath);
        }).toArray(OrderSpecifier[]::new);
    }
}
