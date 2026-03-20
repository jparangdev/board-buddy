package kr.co.jparangdev.boardbuddy.domain.exception;

public interface MessageResolvable {

    String getMessageKey();

    Object[] getMessageArgs();
}
