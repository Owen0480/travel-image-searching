import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import '../styles/Login.css';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [fullName, setFullName] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await api.post('/auth/register', { email, password, fullName });
            alert('회원가입이 완료되었습니다! 로그인해주세요.');
            navigate('/login');
        } catch (err) {
            setError(err.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="login-container">
            <div className="login-card glass-card">
                <h1>Create Account</h1>
                <p className="subtitle">Join our community today</p>

                <form onSubmit={handleRegister}>
                    <div className="input-group">
                        <label>Full Name</label>
                        <input
                            type="text"
                            placeholder="Your Name"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label>Email Address</label>
                        <input
                            type="email"
                            placeholder="name@company.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label>Password</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn-primary">Sign Up</button>

                    {error && <p className="error-message">{error}</p>}
                </form>

                <p className="footer-text">
                    Already have an account? <Link to="/login">Sign In</Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
