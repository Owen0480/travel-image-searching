import { useState, useEffect, useRef } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import AuthCallback from './pages/AuthCallback'
import Chat from './pages/Chat'
import MyPage from './pages/MyPage'
import ImageSearch from './pages/ImageSearch'
import Navbar from './components/layout/Navbar'
import api from './api/axios'

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('accessToken'));
    const [isLoading, setIsLoading] = useState(true);
    const isInitialMount = useRef(true);

    useEffect(() => {
        if (!isInitialMount.current) return;
        isInitialMount.current = false;

        const checkAuth = async () => {
            const hasAccessToken = !!localStorage.getItem('accessToken');

            if (!hasAccessToken) {
                try {
                    const response = await api.post('/auth/refresh');
                    localStorage.setItem('accessToken', response.data.accessToken);
                    if (response.data.email) localStorage.setItem('email', response.data.email);
                    setIsAuthenticated(true);
                } catch (err) {
                    console.log('No valid session found');
                    setIsAuthenticated(false);
                }
            } else {
                setIsAuthenticated(true);
            }
            setIsLoading(false);
        };

        checkAuth();

        const handleAuthChange = () => {
            setIsAuthenticated(!!localStorage.getItem('accessToken'));
        };

        window.addEventListener('auth-change', handleAuthChange);
        return () => window.removeEventListener('auth-change', handleAuthChange);
    }, []);

    if (isLoading) {
        return (
            <div className="login-container">
                <div className="login-card glass-card" style={{ textAlign: 'center' }}>
                    <h2>Loading...</h2>
                    <div className="status-dot online" style={{ margin: '20px auto' }}></div>
                </div>
            </div>
        );
    }

    return (
        <>
            <Navbar />
            <main>
                <Routes>
                    <Route path="/login" element={isAuthenticated ? <Navigate to="/chat" /> : <Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/auth/callback" element={<AuthCallback />} />


                    <Route path="/" element={isAuthenticated ? <Navigate to="/chat" replace /> : <Navigate to="/login" replace />} />
                    <Route path="/chat" element={isAuthenticated ? <Chat /> : <Navigate to="/login" replace />} />
                    <Route path="/mypage" element={isAuthenticated ? <MyPage /> : <Navigate to="/login" replace />} />
                    <Route path="/image-search" element={<ImageSearch />} />

                    <Route path="*" element={<Navigate to="/chat" />} />
                </Routes>
            </main>
        </>
    )
}

export default App
