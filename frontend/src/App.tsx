import {useEffect, useState} from 'react';
import {BrowserRouter, Navigate, Route, Routes, useNavigate} from 'react-router-dom';
import {AuthProvider} from '@/hooks';
import {ErrorBanner, Layout, ProtectedRoute, ServerErrorModal} from '@/components';
import {
  CreateSessionPage,
  GameListPage,
  GroupDashboardPage,
  GroupDetailPage,
  GroupListPage,
  InvitationsPage,
  LoginPage,
  MyPage,
  RegisterPage,
  SessionDetailPage
} from '@/pages';
import type {ApiError} from '@/types';

function App() {
  const [serverError, setServerError] = useState<ApiError | null>(null);
  const [clientError, setClientError] = useState<ApiError | null>(null);

  useEffect(() => {
    const onServerError = (e: Event) => setServerError((e as CustomEvent<ApiError>).detail);
    const onClientError = (e: Event) => setClientError((e as CustomEvent<ApiError>).detail);

    window.addEventListener('boardbuddy:server-error', onServerError);
    window.addEventListener('boardbuddy:client-error', onClientError);
    return () => {
      window.removeEventListener('boardbuddy:server-error', onServerError);
      window.removeEventListener('boardbuddy:client-error', onClientError);
    };
  }, []);

  return (
    <BrowserRouter>
      <AuthErrorListener />
      <AuthProvider>
        {clientError && (
          <ErrorBanner error={clientError} onClose={() => setClientError(null)} />
        )}
        {serverError && (
          <ServerErrorModal error={serverError} onClose={() => setServerError(null)} />
        )}
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/groups" element={<GroupListPage />} />
              <Route path="/groups/:id" element={<GroupDetailPage />} />
              <Route path="/groups/:id/dashboard" element={<GroupDashboardPage />} />
              <Route path="/groups/:groupId/sessions/new" element={<CreateSessionPage />} />
              <Route path="/groups/:groupId/sessions/:sessionId" element={<SessionDetailPage />} />
              <Route path="/games" element={<GameListPage />} />
              <Route path="/invitations" element={<InvitationsPage />} />
              <Route path="/profile" element={<MyPage />} />
              <Route path="/" element={<Navigate to="/groups" replace />} />
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

function AuthErrorListener() {
  const navigate = useNavigate();

  useEffect(() => {
    const onAuthError = () => {
      navigate('/login', { replace: true });
    };

    window.addEventListener('boardbuddy:auth-error', onAuthError);
    return () => {
      window.removeEventListener('boardbuddy:auth-error', onAuthError);
    };
  }, [navigate]);

  return null;
}

export default App;
