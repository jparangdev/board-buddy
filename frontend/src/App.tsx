import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {AuthProvider} from '@/hooks';
import {Layout, ProtectedRoute} from '@/components';
import {CreateSessionPage, GameListPage, GroupDashboardPage, GroupDetailPage, GroupListPage, LoginPage, RegisterPage, SessionDetailPage} from '@/pages';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
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
              <Route path="/" element={<Navigate to="/groups" replace />} />
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
