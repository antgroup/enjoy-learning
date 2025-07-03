// Types
export interface LoginRequest {
  account: string;
  password: string;
  username?: string;
}

export interface UserInfo {
  id: string;
  username: string;
  account: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponse<T> {
  code: string;
  userHint: string;
  errorMessage?: string;
  data: T | null;
}

export interface LoginResponseData {
  success: boolean;
  isNewUser: boolean;
  token: string;
  userId: string;
  username: string;
  account: string;
}

// Mock user database
const mockUsers: Record<string, UserInfo> = {
  'user@example.com': {
    id: '68311a04a03c895a222ff5be',
    username: '测试用户',
    account: 'user@example.com',
    status: 'active',
    createdAt: '2024-03-24 09:00:06',
    updatedAt: '2024-03-24 09:00:06'
  }
};

// Mock token generation
const generateMockToken = (userId: string, account: string, username: string): string => {
  return `mock_token_${userId}_${Date.now()}`;
};

// Mock login function
export const mockLogin = (request: LoginRequest): ApiResponse<LoginResponseData> => {
  const { account, password, username } = request;
  
  // Check if user exists
  const existingUser = mockUsers[account];
  const isNewUser = !existingUser;

  // For demo purposes, accept any password length >= 6
  if (password.length < 6) {
    return {
      code: 'A0400',
      userHint: 'USER INPUT PARAM ERROR',
      errorMessage: '密码错误',
      data: null
    };
  }

  if (isNewUser) {
    // Create new user
    const newUserId = `user_${Date.now()}`;
    const newUser: UserInfo = {
      id: newUserId,
      username: username || account,
      account,
      status: 'active',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    mockUsers[account] = newUser;

    return {
      code: '00000',
      userHint: 'SUCCESS',
      data: {
        success: true,
        isNewUser: true,
        token: generateMockToken(newUserId, account, newUser.username),
        userId: newUserId,
        username: newUser.username,
        account
      }
    };
  }

  // Existing user login
  return {
    code: '00000',
    userHint: 'SUCCESS',
    data: {
      success: true,
      isNewUser: false,
      token: generateMockToken(existingUser.id, account, existingUser.username),
      userId: existingUser.id,
      username: existingUser.username,
      account
    }
  };
};

// Mock get current user info
export const mockGetCurrentUser = (token: string): ApiResponse<UserInfo> => {
  if (!token.startsWith('mock_token_')) {
    return {
      code: 'A0200',
      userHint: 'USER LOGIN ERROR',
      errorMessage: 'Invalid token',
      data: null
    };
  }

  // In real implementation, we would decode the token and get user info
  // Here we just return a mock user
  const mockUser = Object.values(mockUsers)[0];
  
  return {
    code: '00000',
    userHint: 'SUCCESS',
    data: mockUser
  };
};

// Mock logout
export const mockLogout = (token: string): ApiResponse<boolean> => {
  if (!token.startsWith('mock_token_')) {
    return {
      code: 'A0200',
      userHint: 'USER LOGIN ERROR',
      errorMessage: 'Invalid token',
      data: null
    };
  }

  return {
    code: '00000',
    userHint: 'SUCCESS',
    data: true
  };
};

// Usage example:
/*
// Login
const loginResult = mockLogin({
  account: 'user@example.com',
  password: 'password123',
  username: '测试用户'
});

// Get user info
const userInfo = mockGetCurrentUser(loginResult.data?.token || '');

// Logout
const logoutResult = mockLogout(loginResult.data?.token || '');
*/ 
