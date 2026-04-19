import {useEffect, useRef, useState} from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import {authService, userService} from '@/services';
import {useAuth} from '@/hooks/useAuth';

const PENDING_LINK_KEY = 'pendingLink';
const PENDING_LINK_REDIRECT_KEY = 'pendingLinkRedirectUri';

export function OAuthCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const {refreshUser} = useAuth();
  const [error, setError] = useState<string | null>(null);
  const handled = useRef(false);

  useEffect(() => {
    if (handled.current) return;
    handled.current = true;

    const code = searchParams.get('code');
    const errorParam = searchParams.get('error');

    if (errorParam || !code) {
      setError('OAuth 인증이 취소되었거나 오류가 발생했습니다.');
      return;
    }

    const redirectUri = window.location.origin + '/oauth/callback';
    const pendingProvider = sessionStorage.getItem(PENDING_LINK_KEY);

    if (pendingProvider) {
      // Account linking flow
      sessionStorage.removeItem(PENDING_LINK_KEY);
      sessionStorage.removeItem(PENDING_LINK_REDIRECT_KEY);
      userService
        .linkAccount(pendingProvider, code, redirectUri)
        .then(() => navigate('/profile', {replace: true}))
        .catch(() => {
          setError('계정 연동에 실패했습니다.');
        });
    } else {
      // Login flow
      authService
        .loginWithOAuth('kakao', code, redirectUri)
        .then(() => navigate('/', {replace: true}))
        .catch(() => {
          setError('카카오 로그인에 실패했습니다.');
        });
    }
  }, [searchParams, navigate, refreshUser]);

  if (error) {
    return (
      <div style={{textAlign: 'center', marginTop: '4rem'}}>
        <p style={{color: 'var(--color-danger, red)'}}>{error}</p>
        <button className="btn btn-secondary" onClick={() => navigate('/login')}>
          로그인 페이지로
        </button>
      </div>
    );
  }

  return (
    <div style={{textAlign: 'center', marginTop: '4rem'}}>
      <p>인증 처리 중...</p>
    </div>
  );
}

export function startOAuthLink(provider: string, redirectUri: string) {
  sessionStorage.setItem(PENDING_LINK_KEY, provider);
  sessionStorage.setItem(PENDING_LINK_REDIRECT_KEY, redirectUri);
}
