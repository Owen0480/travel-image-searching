import React, { useEffect, useState } from 'react';
import api from '../api/axios';

const MyPage = () => {
    const [template, setTemplate] = useState('');
    const [isLoadingTemplate, setIsLoadingTemplate] = useState(true);

    const handleLogout = async () => {
        try {
            await api.post('/auth/logout');
        } catch (err) {
            console.error('Logout failed', err);
        } finally {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('email');
            localStorage.removeItem('fullname');
            window.location.href = '/login';
        }
    };

    const withdrawAccount = async () => {
        if (window.confirm('정말로 탈퇴하시겠습니까? 모든 정보가 삭제되며 복구할 수 없습니다.')) {
            try {
                await api.delete('/auth/withdraw');
                alert('탈퇴가 완료되었습니다.');
            } catch (err) {
                console.error('Withdrawal failed', err);
                alert('탈퇴 중 오류가 발생했습니다.');
            } finally {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('email');
                localStorage.removeItem('fullname');
                window.location.href = '/login';
            }
        }
    };

    useEffect(() => {
        let isMounted = true;
        const resources = [];

        const loadPage = async () => {
            try {
                // 1. Fetch HTML template with cache buster
                const timestamp = new Date().getTime();
                const res = await fetch(`/mypage_template.html?t=${timestamp}`);
                const html = await res.text();

                if (html.includes('id="root"')) {
                    throw new Error('Template collision detected');
                }

                // 2. Prepare resources
                const scriptRes = { type: 'script', src: 'https://cdn.tailwindcss.com?plugins=forms,container-queries', id: 'tailwind-cdn' };
                const fontRes1 = { type: 'link', href: 'https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap', rel: 'stylesheet' };
                const fontRes2 = { type: 'link', href: 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght@100..700,0..1&display=swap', rel: 'stylesheet' };

                // function to inject and wait for load if it's a script
                const inject = (def) => {
                    return new Promise((resolve) => {
                        const el = document.createElement(def.type);
                        Object.keys(def).forEach(key => { if (key !== 'type') el[key] = def[key]; });
                        if (def.type === 'script') {
                            el.onload = () => resolve(el);
                            el.onerror = () => resolve(el);
                        } else {
                            resolve(el); // Links don't reliably fire onload in all browsers
                        }
                        document.head.appendChild(el);
                        resources.push(el);
                    });
                };

                // Inject resources and wait for Tailwind
                await Promise.all([inject(scriptRes), inject(fontRes1), inject(fontRes2)]);

                // 3. Inject Tailwind Config
                const configScript = document.createElement('script');
                configScript.id = 'tailwind-config-mypage';
                configScript.innerHTML = `
                    tailwind.config = {
                        darkMode: "class",
                        theme: {
                            extend: {
                                colors: {
                                    "primary": "#1392ec",
                                    "background-light": "#f6f7f8",
                                    "background-dark": "#101a22",
                                },
                                fontFamily: { "display": ["Plus Jakarta Sans"] }
                            },
                        },
                    }
                `;
                document.head.appendChild(configScript);
                resources.push(configScript);

                // 4. Reset styles
                const styleReset = document.createElement('style');
                styleReset.id = 'mypage-style-reset';
                styleReset.innerHTML = `
                    body { background-color: #f6f7f8 !important; background-image: none !important; margin: 0; }
                    .dark body { background-color: #101a22 !important; }
                `;
                document.head.appendChild(styleReset);
                resources.push(styleReset);

                if (isMounted) {
                    setTemplate(html);
                    // Small delay to ensure Tailwind has processed the new DOM
                    setTimeout(() => {
                        if (isMounted) setIsLoadingTemplate(false);
                    }, 50);
                }
            } catch (err) {
                console.error('Failed to load MyPage', err);
                if (isMounted) setIsLoadingTemplate(false);
            }
        };

        loadPage();

        return () => {
            isMounted = false;
            resources.forEach(el => {
                if (document.head.contains(el)) document.head.removeChild(el);
            });
        };
    }, []);

    useEffect(() => {
        if (isLoadingTemplate || !template) return;

        const nameEl = document.getElementById('user-name');
        const emailEl = document.getElementById('user-email');
        const fullname = localStorage.getItem('fullname') || 'User';
        const email = localStorage.getItem('email') || '';

        if (nameEl) nameEl.textContent = fullname;
        if (emailEl) emailEl.textContent = email;

        const logoutBtn = document.getElementById('logout-button');
        const withdrawBtn = document.getElementById('withdraw-button');

        const bindEvents = () => {
            if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);
            if (withdrawBtn) withdrawBtn.addEventListener('click', withdrawAccount);
        };

        bindEvents();

        return () => {
            if (logoutBtn) logoutBtn.removeEventListener('click', handleLogout);
            if (withdrawBtn) withdrawBtn.removeEventListener('click', withdrawAccount);
        };
    }, [isLoadingTemplate, template]);

    if (isLoadingTemplate) {
        return (
            <div style={{ display: 'flex', height: '100vh', width: '100vw', alignItems: 'center', justifyContent: 'center', backgroundColor: '#f6f7f8', position: 'fixed', top: 0, left: 0, zIndex: 1000 }}>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '15px' }}>
                    <div style={{ width: '40px', height: '40px', border: '4px solid #e7eef3', borderTop: '4px solid #1392ec', borderRadius: '50%', animation: 'spin 1s linear infinite' }}></div>
                    <div style={{ color: '#1392ec', fontWeight: 'bold' }}>Loading Profile...</div>
                </div>
                <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
            </div>
        );
    }

    return (
        <div
            dangerouslySetInnerHTML={{ __html: template }}
        />
    );
};

export default MyPage;
