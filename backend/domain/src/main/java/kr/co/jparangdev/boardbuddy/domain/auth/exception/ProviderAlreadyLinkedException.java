package kr.co.jparangdev.boardbuddy.domain.auth.exception;

import kr.co.jparangdev.boardbuddy.domain.exception.ConflictException;
import kr.co.jparangdev.boardbuddy.domain.exception.MessageResolvable;

@SuppressWarnings("java:S110")
public class ProviderAlreadyLinkedException extends ConflictException implements MessageResolvable {

    public ProviderAlreadyLinkedException(String provider) {
        super(AuthErrorCode.PROVIDER_ALREADY_LINKED, "Provider already linked: " + provider);
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
