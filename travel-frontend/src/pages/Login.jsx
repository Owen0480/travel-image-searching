import React, { useEffect, useState } from 'react';

const Login = () => {
    const [template, setTemplate] = useState('');
    const [isLoadingTemplate, setIsLoadingTemplate] = useState(true);

    const handleGoogleLogin = () => {
        const backendUrl = `http://${window.location.hostname}:8080`;
        window.location.href = `${backendUrl}/oauth2/authorization/google`;
    };

    useEffect(() => {
        let isMounted = true;
        const resources = [];

        const loadPage = async () => {
            try {
                // 1. Fetch HTML template with cache buster
                const timestamp = new Date().getTime();
                const res = await fetch(`/login_template.html?t=${timestamp}`);
                const html = await res.text();

                // If we get an SPA fallback, something is wrong
                if (html.includes('id="root"')) {
                    throw new Error('Login template collision detected');
                }

                // 2. Prepare resources
                const scriptRes = { type: 'script', src: 'https://cdn.tailwindcss.com?plugins=forms,container-queries', id: 'tailwind-cdn-login' };
                const fontRes1 = { type: 'link', href: 'https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Noto+Sans:wght@400;500;700&display=swap', rel: 'stylesheet' };
                const fontRes2 = { type: 'link', href: 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap', rel: 'stylesheet' };

                const inject = (def) => {
                    return new Promise((resolve) => {
                        const el = document.createElement(def.type);
                        Object.keys(def).forEach(key => { if (key !== 'type') el[key] = def[key]; });
                        if (def.type === 'script') {
                            el.onload = () => resolve(el);
                            el.onerror = () => resolve(el);
                        } else {
                            resolve(el);
                        }
                        document.head.appendChild(el);
                        resources.push(el);
                    });
                };

                await Promise.all([inject(scriptRes), inject(fontRes1), inject(fontRes2)]);

                // 3. Inject Tailwind Config
                const configScript = document.createElement('script');
                configScript.id = 'tailwind-config-login';
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
                                fontFamily: { "display": ["Plus Jakarta Sans", "sans-serif"] },
                                borderRadius: { "DEFAULT": "0.25rem", "lg": "0.5rem", "xl": "0.75rem", "full": "9999px" },
                            },
                        },
                    }
                `;
                document.head.appendChild(configScript);
                resources.push(configScript);

                // 4. Styles Reset (same as MyPage)
                const styleReset = document.createElement('style');
                styleReset.id = 'login-style-reset';
                styleReset.innerHTML = `
                    body { background-color: #f6f7f8 !important; background-image: none !important; margin: 0; }
                    .dark body { background-color: #101a22 !important; }
                `;
                document.head.appendChild(styleReset);
                resources.push(styleReset);

                if (isMounted) {
                    setTemplate(html);
                    setTimeout(() => { if (isMounted) setIsLoadingTemplate(false); }, 50);
                }
            } catch (err) {
                console.error('Failed to load login page', err);
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

        const googleBtn = document.getElementById('google-login-button');
        if (googleBtn) {
            googleBtn.addEventListener('click', handleGoogleLogin);
        }

        return () => {
            if (googleBtn) {
                googleBtn.removeEventListener('click', handleGoogleLogin);
            }
        };
    }, [isLoadingTemplate, template]);

    if (isLoadingTemplate) {
        return (
            <div style={{ display: 'flex', height: '100vh', width: '100vw', alignItems: 'center', justifyContent: 'center', backgroundColor: '#f6f7f8', position: 'fixed', top: 0, left: 0, zIndex: 1000 }}>
                <div style={{ width: '40px', height: '40px', border: '4px solid #e7eef3', borderTop: '4px solid #1392ec', borderRadius: '50%', animation: 'spin 1s linear infinite' }}></div>
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

export default Login;
