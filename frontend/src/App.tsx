import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {AuthProvider} from '@/hooks';
import {Layout, ProtectedRoute} from '@/components';
import {GroupDetailPage, GroupListPage, LoginPage} from '@/pages';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/groups" element={<GroupListPage />} />
              <Route path="/groups/:id" element={<GroupDetailPage />} />
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
