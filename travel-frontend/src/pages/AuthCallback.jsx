import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../api/axios';

const AuthCallback = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const checkAuth = async () => {
            const params = new URLSearchParams(location.search);
            const accessToken = params.get('accessToken');

            if (accessToken) {
                // Store access token first
                localStorage.setItem('accessToken', accessToken);

                try {
                    // Fetch user info from backend
                    const response = await api.get('/v1/users/info');
                    const userData = response.data.data;

                    // Store user information
                    if (userData.email) localStorage.setItem('email', userData.email);
                    if (userData.fullName) localStorage.setItem('fullname', userData.fullName);

                    // Trigger auth state change for App component
                    window.dispatchEvent(new Event('auth-change'));
                    navigate('/chat', { replace: true });
                } catch (error) {
                    console.error('Failed to fetch user info:', error);
                    // Even if user info fetch fails, proceed to chat
                    window.dispatchEvent(new Event('auth-change'));
                    navigate('/chat', { replace: true });
                }
            } else {
                console.error('Authentication failed: No token found');
                navigate('/login', { replace: true });
            }
        };

        checkAuth();
    }, [location, navigate]);

    return (
        <div style={{ display: 'flex', minHeight: '100vh', alignItems: 'center', justifyContent: 'center' }}>
            <div className="glass-card" style={{ padding: '40px', textAlign: 'center' }}>
                <div style={{ fontSize: '2rem', marginBottom: '15px' }}>🔒</div>
                <h2>Authenticating...</h2>
                <p style={{ color: 'var(--text-muted)', marginTop: '10px' }}>잠시만 기다려주세요.</p>
            </div>
        </div>
    );
};

export default AuthCallback;