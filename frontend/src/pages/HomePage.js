import { Link } from 'react-router-dom';

function HomePage() {
  return (
    <div style={{ padding: '20px' }}>
      <h1>홈페이지</h1>
      <nav style={{ marginTop: '10px' }}>
        <Link to="/login" style={{ marginRight: '10px' }}>
          로그인
        </Link>
        <Link to="/register">회원가입</Link>
      </nav>
    </div>
  );
}

export default HomePage;
