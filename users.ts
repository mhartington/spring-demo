import { Hono } from 'hono';
import { users, generateId, User } from '../data/index';
import { generateToken, auth } from '../middleware/auth';
import { validate, registerSchema, loginSchema } from '../utils/validators';

type Variables = {
  user: User,
  body: User
}
const app = new Hono<{Variables: Variables}>();


// Register new user
app.post('/register', validate(registerSchema), (c) => {
  const { username, email, password } = c.get('body');
  
  // Check if user with email already exists
  if (users.some(u => u.email === email)) {
    return c.json({ error: 'Email already in use' }, 400);
  }
  
  // Create new user
  const newUser = {
    id: generateId(),
    username,
    email,
    password, // In production, this would be hashed
    role: 'customer', // Default role
    created_at: new Date().toISOString()
  };
  
  users.push(newUser);
  
  // Generate token
  const token = generateToken(newUser);
  
  // Remove password from response
  const { password: _, ...userWithoutPassword } = newUser;
  
  return c.json({
    message: 'User registered successfully',
    user: userWithoutPassword,
    token
  }, 201);
});

// Login user
app.post('/login', validate(loginSchema), (c) => {
  const { email, password } = c.get('body');
  
  // Find user
  const user = users.find(u => u.email === email);
  
  // Check if user exists and password matches
  if (!user || user.password !== password) { // In production, would use proper password comparison
    return c.json({ error: 'Invalid credentials' }, 401);
  }
  
  // Generate token
  const token = generateToken(user);
  
  // Remove password from response
  const { password: _, ...userWithoutPassword } = user;
  
  return c.json({
    message: 'Login successful',
    user: userWithoutPassword,
    token
  });
});

// Get user profile (authenticated)
app.get('/profile', auth, (c) => {
  const authenticatedUser = c.get('user');
  
  // Find full user details
  const user = users.find(u => u.id === authenticatedUser.id);
  
  if (!user) {
    return c.json({ error: 'User not found' }, 404);
  }
  
  // Remove password from response
  const { password: _, ...userWithoutPassword } = user;
  
  return c.json({ user: userWithoutPassword });
});

export default app;
