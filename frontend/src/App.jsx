import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import EspaceAdmin from './pages/EspaceAdmin';
import EspaceCandidat from './pages/EspaceCandidat';
import Login from './pages/Login';
import MasterDetail from './pages/MasterDetail';
import Masters from './pages/Masters';
import NotFound from './pages/NotFound';
import NouvelleCandidature from './pages/NouvelleCandidature';
import Register from './pages/Register';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Masters />} />
        <Route path="/masters/:id" element={<MasterDetail />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route element={<ProtectedRoute role="CANDIDAT" />}>
          <Route path="/candidat" element={<EspaceCandidat />} />
          <Route path="/candidat/nouvelle" element={<NouvelleCandidature />} />
        </Route>

        <Route element={<ProtectedRoute role="ADMIN" />}>
          <Route path="/admin" element={<EspaceAdmin />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}