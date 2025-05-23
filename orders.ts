import { Hono } from 'hono';
import { orders, carts, products, generateId, User } from '../data/index.js';
import { auth } from '../middleware/auth.js';
import { validate, orderSchema } from '../utils/validators.js';

type Variables = {
  user : User,
    body: {
        shipping_address: string;
        payment_method: string;
    };
};

const app = new Hono<{Variables: Variables}>();

// Get user orders
app.get('/', auth, (c) => {
  const { id: userId } = c.get('user');
  
  const userOrders = orders.filter(order => order.user_id === userId);
  
  return c.json({ orders: userOrders });
});

// Get order by ID
app.get('/:id', auth, (c) => {
  const { id: userId } = c.get('user');
  const orderId = c.req.param('id');
  
  const order = orders.find(o => o.id === orderId && o.user_id === userId);
  
  if (!order) {
    return c.json({ error: 'Order not found' }, 404);
  }
  
  return c.json({ order });
});

// Create new order
app.post('/', auth, validate(orderSchema), async (c) => {
  const { id: userId } = c.get('user');
  const { shipping_address, payment_method } = c.get('body');
  
  // Check if user has items in cart
  const cart = carts[userId];
  
  if (!cart || cart.items.length === 0) {
    return c.json({ error: 'Cannot create order with empty cart' }, 400);
  }
  
  // Create order
  const newOrder = {
    id: generateId(),
    user_id: userId,
    items: [...cart.items],
    total: cart.total,
    shipping_address,
    payment_method,
    status: 'pending',
    created_at: new Date().toISOString()
  };
  
  // Update product stock
  for (const item of cart.items) {
    const product = products.find(p => p.id === item.product.id);
    if (product) {
      product.stock -= item.quantity;
    }
  }
  
  // Add to orders
  orders.push(newOrder);
  
  // Clear cart
  carts[userId] = {
    items: [],
    total: 0
  };
  
  return c.json({
    message: 'Order created successfully',
    order: newOrder
  }, 201);
});

// Cancel order (only if pending)
app.delete('/:id', auth, (c) => {
  const { id: userId } = c.get('user');
  const orderId = c.req.param('id');
  
  const orderIndex = orders.findIndex(o => o.id === orderId && o.user_id === userId);
  
  if (orderIndex === -1) {
    return c.json({ error: 'Order not found' }, 404);
  }
  
  const order = orders[orderIndex];
  
  // Can only cancel pending orders
  if (order.status !== 'pending') {
    return c.json({ error: 'Cannot cancel order that is not pending' }, 400);
  }
  
  // Update order status
  order.status = 'cancelled';
  order.cancelled_at = new Date().toISOString();
  
  // Restore product stock
  for (const item of order.items) {
    const product = products.find(p => p.id === item.product.id);
    if (product) {
      product.stock += item.quantity;
    }
  }
  
  return c.json({
    message: 'Order cancelled successfully',
    order
  });
});

export default app;
