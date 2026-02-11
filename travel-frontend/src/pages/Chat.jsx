import React from 'react';

/**
 * /chat 라우트에서 Conversation.html(Travel AI UI)을 iframe으로 표시.
 * 실제 채팅 UI·API 연동은 public/Conversation.html에서 처리.
 */
const Chat = () => {
    return (
        <iframe
            src="/Conversation.html"
            title="AI Travel Recommendation Chat"
            style={{
                width: '100%',
                height: '100vh',
                minHeight: '500px',
                border: 'none',
                display: 'block',
            }}
        />
    );
};

export default Chat;
