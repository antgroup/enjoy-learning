import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi, tokenUtils, LoginRequest } from '../../api';
import './index.css';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    account: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.account || !formData.password) {
      setError('请输入账号和密码');
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      // 构建登录请求数据
      const loginData: LoginRequest = {
        account: formData.account,
        password: formData.password,
      };
      
      // 调用真实API登录
      const response = await userApi.login(loginData);
      
      if (response.code === '00000' && response.data) {
        // 保存token
        tokenUtils.setToken(response.data.token);
        
        // 显示欢迎信息
        if (response.data.isNewUser) {
          console.log('欢迎新用户:', response.data.username);
        } else {
          console.log('欢迎回来:', response.data.username);
        }
        
        // 跳转到首页，让IndexRoute处理后续逻辑
        navigate('/');
      } else {
        setError(response.errorMessage || '登录失败');
      }
      
    } catch (err: any) {
      console.error('登录错误:', err);
      setError(err.message || '登录失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-content">
        <div className="login-card">
          <div className="login-header">
            <h1 className="login-title">快乐学习</h1>
            <p className="login-subtitle">欢迎回来，开始您的学习之旅</p>
          </div>

          <form className="login-form" onSubmit={handleLogin}>
            {error && (
              <div className="error-message">
                {error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="account" className="form-label">账号</label>
              <input
                type="text"
                id="account"
                name="account"
                className="form-input"
                placeholder="请输入邮箱或手机号"
                value={formData.account}
                onChange={handleInputChange}
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password" className="form-label">密码</label>
              <input
                type="password"
                id="password"
                name="password"
                className="form-input"
                placeholder="请输入密码"
                value={formData.password}
                onChange={handleInputChange}
                disabled={loading}
              />
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading ? '登录中...' : '登录'}
            </button>
          </form>

          <div className="login-footer">
            <p className="login-tip">
              首次登录将自动注册账号
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
