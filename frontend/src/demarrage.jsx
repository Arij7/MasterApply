import { Component, StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import './index.css';

class AfficheErreur extends Component {
  state = { erreur: null };

  static getDerivedStateFromError(erreur) {
    return { erreur };
  }

  render() {
    if (this.state.erreur) {
      return (
        <pre style={{ color: 'red', padding: 20, whiteSpace: 'pre-wrap' }}>
          {String(this.state.erreur.stack || this.state.erreur)}
        </pre>
      );
    }
    return this.props.children;
  }
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AfficheErreur>
      <BrowserRouter>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </AfficheErreur>
  </StrictMode>
);