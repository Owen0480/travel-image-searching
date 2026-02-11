import React from 'react';

/**
 * 이미지 업로드 추천 화면 — public/imageSeaching.html (recommend API 연동)
 */
const ImageSearch = () => {
    return (
        <div style={{ width: '100%', height: 'calc(100vh - 100px)' }}>
            <iframe
                src="/imageSeaching.html"
                title="이미지로 여행지 추천"
                style={{ width: '100%', height: '100%', border: 'none' }}
            />
        </div>
    );
};

export default ImageSearch;
