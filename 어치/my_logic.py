import streamlit as st
import pandas as pd
import os
from sentence_transformers import SentenceTransformer, util
from PIL import Image
import google.generativeai as genai
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

# --- 페이지 설정 ---
st.set_page_config(
    page_title="나의 AI 여행지 추천",
    layout="wide",
    initial_sidebar_state="collapsed"
)

# 커스텀 CSS - 여행 앱 느낌의 깔끔한 디자인
st.markdown("""
    <style>
    .main-header {
        text-align: center;
        padding: 2.5rem 0;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 15px;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }
    .travel-card {
        background-color: #ffffff;
        padding: 1.5rem;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 1rem;
    }
    .stMetric {
        background-color: #f8f9fa;
        padding: 1.2rem;
        border-radius: 10px;
        border-left: 4px solid #667eea;
    }
    h3 {
        color: #667eea;
        margin-top: 1rem;
    }
    </style>
""", unsafe_allow_html=True)

# --- 헤더 ---
st.markdown("""
    <div class="main-header">
        <h1>✈️ AI 여행지 추천 서비스</h1>
        <p style="font-size: 1.3em; margin-top: 0.5rem;">사진으로 찾는 나만의 여행지</p>
    </div>
""", unsafe_allow_html=True)

# --- 1. 모델 및 데이터 로드 (캐싱 처리하여 속도 최적화) ---
@st.cache_resource
def load_model():
    return SentenceTransformer('clip-ViT-B-32')

@st.cache_resource
def load_gemini_model():
    """Gemini 모델 로드"""
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        return None
    genai.configure(api_key=api_key)
    return genai.GenerativeModel('gemini-2.5-flash')

@st.cache_data
def load_csv_data():
    """CSV 데이터 로드 및 병합"""
    photo_path = '../appdata/tn_tour_photo_관광사진_F.csv'
    place_path = '../data/tn_visit_area_info_방문지정보_F.csv'
    
    photo_df = pd.read_csv(photo_path, encoding='utf-8-sig')
    place_df = pd.read_csv(place_path, encoding='utf-8-sig')
    
    # VISIT_AREA_ID를 기준으로 병합
    merged_df = pd.merge(
        photo_df, 
        place_df, 
        on='VISIT_AREA_ID',
        how='inner'
    )
    
    # PHOTO_FILE_NM 컬럼 정규화 (공백 제거, 문자열 변환)
    if 'PHOTO_FILE_NM' in merged_df.columns:
        merged_df['PHOTO_FILE_NM'] = merged_df['PHOTO_FILE_NM'].astype(str).str.strip()
    
    return merged_df

def get_place_info(merged_df, best_match_file):
    """
    best_match_file(PHOTO_FILE_NM)을 키값으로 사용하여 
    정확한 장소명(VISIT_AREA_NM_x)과 주소(ROAD_NM_ADDR)를 가져오는 함수
    행 인덱스가 꼬이지 않도록 정확한 매칭 수행
    """
    # 파일명 정규화
    normalized_match_file = str(best_match_file).strip()
    
    # PHOTO_FILE_NM 컬럼과 정확히 일치하는 행 찾기
    # .copy()를 사용하여 SettingWithCopyWarning 방지
    match_mask = merged_df['PHOTO_FILE_NM'] == normalized_match_file
    match_info = merged_df[match_mask].copy()
    
    # 정확한 매칭이 없으면 대소문자 무시 매칭 시도
    if match_info.empty:
        match_mask = merged_df['PHOTO_FILE_NM'].str.strip().str.lower() == normalized_match_file.lower()
        match_info = merged_df[match_mask].copy()
    
    if not match_info.empty:
        # 첫 번째 매칭 결과의 인덱스를 사용하여 안전하게 데이터 추출
        first_match_idx = match_info.index[0]
        row = merged_df.loc[first_match_idx]
        
        # VISIT_AREA_NM_x 컬럼에서 장소명 가져오기 (merge 후 photo_df의 컬럼)
        place_name = None
        if 'VISIT_AREA_NM_x' in row.index:
            place_name = row['VISIT_AREA_NM_x']
        elif 'VISIT_AREA_NM_y' in row.index:
            place_name = row['VISIT_AREA_NM_y']
        elif 'VISIT_AREA_NM' in row.index:
            place_name = row['VISIT_AREA_NM']
        
        # ROAD_NM_ADDR 컬럼에서 주소 가져오기
        address = row.get('ROAD_NM_ADDR', None)
        
        # NaN 체크 및 반환
        place_name = place_name if pd.notna(place_name) and str(place_name).strip() else '장소명 정보 없음'
        address = address if pd.notna(address) and str(address).strip() else '주소 정보 없음'
        
        return {
            'place_name': place_name,
            'address': address,
            'success': True
        }
    else:
        return {
            'place_name': None,
            'address': None,
            'success': False
        }

def generate_travel_guide(gemini_model, place_name, address):
    """
    gemini-2.5-flash 모델을 사용하여 여행 가이드 생성
    장소의 특징, 여행 팁, 근처 맛집 정보 포함
    """
    if gemini_model is None:
        return "⚠️ Gemini API가 설정되지 않아 가이드를 생성할 수 없습니다. .env 파일에 GEMINI_API_KEY를 설정해주세요."
    
    prompt = f"""당신은 전문 여행 가이드입니다. 다음 장소에 대한 친절하고 실용적인 여행 가이드를 작성해주세요.

**장소명**: {place_name}
**주소**: {address}

다음 세 가지 내용을 반드시 포함하여 한국어로 답변해주세요:

    1. **한 줄 요약**: 이 장소의 핵심 매력을 딱 한 문장(20자 내외)으로 요약할 것.
    2. **핵심 특징**: 장점 1가지만 짧게 설명.
    3. **여행 팁 & 맛집**: 방문 팁 1개와 맛집 1개만 짧은 리스트 형태로 제공.

    전체 답변은 5문장을 넘지 않도록 아주 간결하게 작성하세요.
    """

    try:
        response = gemini_model.generate_content(prompt)
        return response.text
    except Exception as e:
        return f"⚠️ 가이드 생성 중 오류가 발생했습니다: {str(e)}"

# 모델 및 데이터 로드
model = load_model()
gemini_model = load_gemini_model()
merged_df = load_csv_data()

# --- 2. 이미지 업로드 섹션 ---
uploaded_file = st.file_uploader(
    "📷 여행지 사진을 업로드해주세요",
    type=['jpg', 'jpeg', 'png'],
    help="사진을 업로드하면 AI가 유사한 장소를 찾아드립니다"
)

# 분석 버튼은 사진 업로드 후 상단에 표시
if uploaded_file is not None:
    # 분석 버튼
    if st.button("🔎 유사한 장소 찾기", type="primary", use_container_width=True):
        with st.spinner("🤖 AI가 사진을 분석하고 있습니다..."):
            # 1. 사용자 사진 임베딩
            user_img = Image.open(uploaded_file)
            user_img_emb = model.encode(user_img)
            
            # 2. DB 사진 비교 (images 폴더 내 파일)
            db_images_folder = 'images'
            db_image_files = [f for f in os.listdir(db_images_folder) 
                             if f.endswith(('.jpg', '.png', '.jpeg'))]
            
            if not db_image_files:
                st.error("❌ 비교할 사진이 images 폴더에 없습니다.")
            else:
                best_score = -1
                best_match_file = ""
                
                progress_bar = st.progress(0)
                status_text = st.empty()
                
                for idx, file_name in enumerate(db_image_files):
                    db_img_path = os.path.join(db_images_folder, file_name)
                    try:
                        db_img_emb = model.encode(Image.open(db_img_path))
                        score = util.cos_sim(user_img_emb, db_img_emb).item()
                        
                        if score > best_score:
                            best_score = score
                            best_match_file = file_name
                        
                        # 진행률 업데이트
                        progress_bar.progress((idx + 1) / len(db_image_files))
                        status_text.text(f"분석 중... ({idx + 1}/{len(db_image_files)})")
                    except Exception as e:
                        continue
                
                progress_bar.empty()
                status_text.empty()
                
                # 3. 결과 표시
                if best_score > 0.6:  # 유사도 임계값
                    # 데이터 정합성 보장: PHOTO_FILE_NM으로 정확한 정보 가져오기
                    place_info = get_place_info(merged_df, best_match_file)
                    
                    if place_info['success']:
                        place_name = place_info['place_name']
                        address = place_info['address']
                        
                        # 좌우 2열 레이아웃: 왼쪽에 사진, 오른쪽에 AI 답변
                        col_left, col_right = st.columns([1, 1], gap="large")
                        
                        with col_left:
                            st.markdown("### 📸 사진 비교 분석")
                            st.image(user_img, caption="내가 업로드한 사진", use_container_width=True)

                            st.markdown("---")

                            # 유사도 점수와 장소 정보 카드
                            st.markdown("---")
                            st.markdown("### 📊 분석 결과")
                            
                            col_score, col_place = st.columns(2)
                            with col_score:
                                st.metric(
                                    label="🎯 유사도 점수",
                                    value=f"{best_score:.2%}",
                                    delta=f"{best_score - 0.6:.2%}" if best_score > 0.6 else None
                                )
                            
                            with col_place:
                                st.metric(
                                    label="📍 장소명",
                                    value=place_name[:15] + "..." if len(place_name) > 15 else place_name
                                )
                            
                            with st.expander("📍 상세 주소 정보", expanded=False):
                                st.write(f"**주소:** {address}")
                                st.write(f"**매칭된 파일:** `{best_match_file}`")
                        
                        with col_right:
                            st.markdown("### 🗺️ AI 여행 가이드")
                            
                            # 1. AI가 찾은 매칭 사진을 먼저 보여줌
                            best_img_path = os.path.join(db_images_folder, best_match_file)
                            if os.path.exists(best_img_path):
                                st.image(Image.open(best_img_path), caption=f"추천 장소: {place_name}", use_container_width=True)

                            # 2. AI 설명 생성
                            with st.spinner("✍️ 가이드를 작성 중입니다..."):
                                travel_guide = generate_travel_guide(gemini_model, place_name, address)
                                st.markdown(travel_guide)
                    else:
                        st.error(f"⚠️ 사진은 찾았지만, CSV에서 장소 정보를 불러오지 못했습니다.\n매칭된 파일: `{best_match_file}`")
                else:
                    with st.spinner("이 사진이 무엇인지 AI가 분석 중입니다..."):
                        # DB엔 없는 사진 Gemini 로 찾기
                        analysis_result = gemini_model.generate_content(["이 사진이 어떤 사진인지 한 문장으로 설명해줘.", user_img])
                        st.info(f"💡 AI의 분석: {analysis_result.text}")
    
    # 사진 미리보기 (버튼 클릭 전)
    else:
        user_img = Image.open(uploaded_file)
        col_preview_left, col_preview_right = st.columns([1, 1], gap="large")
        
        with col_preview_left:
            st.markdown("### 📸 업로드한 사진")
            st.image(user_img, caption="내가 업로드한 사진", use_container_width=True)
        
        with col_preview_right:
            st.markdown("### 💡 안내")
            st.info("👆 위의 '유사한 장소 찾기' 버튼을 클릭하면 AI가 사진을 분석하고 여행 가이드를 제공합니다!")

# 사이드바에 정보 표시
with st.sidebar:
    st.header("ℹ️ 사용 방법")
    st.markdown("""
    1. **사진 업로드**: 여행지 사진을 드래그 앤 드롭
    2. **분석 시작**: "유사한 장소 찾기" 버튼 클릭
    3. **결과 확인**: 
       - 왼쪽: 업로드한 사진과 분석 결과
       - 오른쪽: AI가 생성한 여행 가이드
    
    ---
    
    **기술 스택:**
    - CLIP (Vision-Language Model)
    - Gemini 1.5 Flash (RAG)
    - Streamlit
    
    ---
    
    **주요 기능:**
    - 📸 이미지 기반 장소 매칭
    - 🗺️ AI 여행 가이드 생성
    - 📍 정확한 주소 정보 제공
    """)
    
    if st.button("🔄 데이터 새로고침"):
        st.cache_data.clear()
        st.cache_resource.clear()
        st.rerun()
