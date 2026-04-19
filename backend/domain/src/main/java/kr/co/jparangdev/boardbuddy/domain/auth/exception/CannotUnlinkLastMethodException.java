package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class CannotUnlinkLastMethodException extends ConflictException implements MessageResolvable {

    public CannotUnlinkLastMethodException() {
        super(AuthErrorCode.CANNOT_UNLINK_LAST_METHOD,
                "Cannot unlink the only remaining login method");
    }

    @Override
    public String getMessageKey() {
        return getErrorCode().name();
    }

    @Override
    public Object[] getMessageArgs() {
        return new Object[0];
    }
}
