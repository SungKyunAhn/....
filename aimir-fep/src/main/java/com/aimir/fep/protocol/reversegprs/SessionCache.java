/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.aimir.fep.protocol.reversegprs;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.commons.logging.Log;

/**
 * Class that provides access to cached sessions.  IoSessions are cached using
 * the host and the port.  This class is thread safe.
 */
public final class SessionCache implements Serializable {

	private static final long serialVersionUID = -3779573915963165490L;
	private static Log log = LogFactory.getLog(SessionCache.class);
	//private final ConcurrentMap<String,Queue<IoSession>> cachedSessions = 
    //        new ConcurrentHashMap<String,Queue<IoSession>>();
	
	private final ConcurrentMap<String,IoSession> cachedSessions = 
            new ConcurrentHashMap<String,IoSession>();
	
	static private SessionCache instance;
	
	private SessionCache(){}

	public static synchronized SessionCache getInstance() {
		if (instance == null)
			instance = new SessionCache();
		return instance;
	}
	
    public synchronized int getTotalSessionCount(){
    	return cachedSessions.size();
    }
    
    /**
     * Returns an IoSession that is connected and considered usable.  Note that
     * this is still on a best-effort basis, and there is no guarantee that the
     * connection can be used without errors, although it should be usually
     * safe to use it.
     * 
     * @param msg the message for which to look up an active session.
     * @throws IllegalArgumentException if a null request message was passed in.
     * @return an active IoSession, or null if none are found.
     */
    public synchronized IoSession getActiveSession(String targetId) {
        if (targetId == null || "".equals(targetId)) {
            throw new IllegalArgumentException("null request was passed in");
        }        
        
        Set<String> set = cachedSessions.keySet();
        for(String str : set){
        	log.info("Session Key:="+str);
        }
        
        log.info("Is ContainsKey ["+targetId+"]="+cachedSessions.containsKey(targetId));
        IoSession sess = cachedSessions.get(targetId);
        
        if (sess == null) {
        	log.info("cachedSessions is null");
            return null;
        }
        log.info("cachedSessions ="+sess.getRemoteAddress()+","+sess.isConnected()+","+sess.isClosing());
        if (sess.isConnected() && !sess.isClosing()) {
            return sess;
        }
        return null;

    }
    
    /**
     * Returns an IoSession that is connected and considered usable.  Note that
     * this is still on a best-effort basis, and there is no guarantee that the
     * connection can be used without errors, although it should be usually
     * safe to use it.
     * 
     * @param msg the message for which to look up an active session.
     * @throws IllegalArgumentException if a null request message was passed in.
     * @return an active IoSession, or null if none are found.
     */
    /*
    public synchronized IoSession getActiveSession(String targetId) {
        if (targetId == null || "".equals(targetId)) {
            throw new IllegalArgumentException("null request was passed in");
        }        
        
        Set<String> set = cachedSessions.keySet();
        for(String str : set){
        	log.info("Session Key:="+str);
        }
        
        log.info("Is ContainsKey ["+targetId+"]="+cachedSessions.containsKey(targetId));
        Queue<IoSession> queue = cachedSessions.get(targetId);
        
        if (queue == null) {
        	log.info("cachedSessions is null");
            return null;
        }
        log.info("cachedSessions size="+queue.size());
        IoSession cached = null;
        while ((cached = queue.poll()) != null) {
        	// see if the session is usable
        	log.info("cachedSessions ="+cached.getRemoteAddress()+","+cached.isConnected()+","+cached.isClosing());
            if (cached.isConnected() && !cached.isClosing()) {
                return cached;
            }
        }
        return null;
    }
    */    
    
    public synchronized void cacheSession(String targetId, IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("null session was passed in");
        }
        
        String key = targetId;
        if(cachedSessions.containsKey(key)){
        	return;
        }
        
        cachedSessions.put(key, session);

    }
    
    public synchronized void cacheNewSession(String targetId, IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("null session was passed in");
        }
        
        String key = targetId;
        if(cachedSessions.containsKey(key)){
        	cachedSessions.remove(key);
        }
        
        cachedSessions.put(key, session);

    }
    
    /**
     * Caches the given session using its remote host and port information.
     * 
     * @param session IoSession to cache
     * @throws IllegalArgumentException if a null session was passed in.
     */
    /*
    public synchronized void cacheSession(String targetId, IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("null session was passed in");
        }
        
        String key = targetId;
        if(cachedSessions.containsKey(key)){
        	return;
        }

        Queue<IoSession> newQueue = new ConcurrentLinkedQueue<IoSession>();
        Queue<IoSession> queue = cachedSessions.putIfAbsent(key, newQueue);
        if (queue == null) {
            // the value was previously empty
            queue = newQueue;
        }
        // add it to the queue
        queue.offer(session);

    }
     */
    
    /**
     * Removes the given session from the cache if it is in the cache.
     * 
     * @param session IoSession to remove from the cache
     * @throws IllegalArgumentException if a null session was passed in
     */
    public synchronized void removeSession(String targetId, IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("null session was passed in");
        }
        
        if (targetId == null || "".equals(targetId)) {
        	log.info("Key is empty");
            return;
        }
        
        String key = targetId;
        if(cachedSessions.containsKey(key)){
        	cachedSessions.remove(key);
        }

        /*
        Queue<IoSession> queue = cachedSessions.get(key);
        if (queue != null) {
            queue.remove(session);
        }
        */

    }
}