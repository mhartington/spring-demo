package monostore.backend.service;

import monostore.backend.models.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SessionService {
    
    private final Map<String, CustomUserDetails> sessions = new ConcurrentHashMap<>();
    private final AtomicLong sessionIdCounter = new AtomicLong(1);
    
    /**
     * Create a new session for a user
     * @param userDetails The user details to create a session for
     * @return The session ID
     */
    public String createSession(CustomUserDetails userDetails) {
        String sessionId = "session_" + sessionIdCounter.getAndIncrement();
        sessions.put(sessionId, userDetails);
        return sessionId;
    }
    
    /**
     * Get user from session
     * @param sessionId The session ID
     * @return The user details if session exists, null otherwise
     */
    public CustomUserDetails getUserFromSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    /**
     * Remove a session
     * @param sessionId The session ID to remove
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    /**
     * Check if a session exists
     * @param sessionId The session ID to check
     * @return true if session exists, false otherwise
     */
    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    /**
     * Get session ID from cookie value
     * @param cookieValue The cookie value
     * @return The session ID or null if invalid
     */
    public String getSessionIdFromCookie(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return null;
        }
        
        // Parse the session cookie
        String[] parts = cookieValue.split("=");
        if (parts.length == 2 && "session".equals(parts[0].trim())) {
            return parts[1].trim();
        }
        
        return null;
    }
} 
