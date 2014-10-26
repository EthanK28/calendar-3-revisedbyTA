package com.mycompany.myapp.service;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import com.mycompany.myapp.dao.CalendarUserDao;
import com.mycompany.myapp.dao.EventAttendeeDao;
import com.mycompany.myapp.dao.EventDao;
import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;
import com.mycompany.myapp.domain.EventAttendee;
import com.mycompany.myapp.domain.EventLevel;

@Service
public class DefaultCalendarService implements CalendarService {
	@Autowired
    private EventDao eventDao;
	
	@Autowired
    private CalendarUserDao userDao;
	
	@Autowired
	private EventAttendeeDao eventAttendeeDao;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	public static final int MIN_NUMLIKES_FOR_HOT = 10;
	
	@Override
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/* CalendarUser */
	@Override
    public CalendarUser getUser(int id) {
		// TODO Assignment 3
		return this.userDao.findUser(id);
	}

	@Override
    public CalendarUser getUserByEmail(String email) {
		// TODO Assignment 3
		return this.userDao.findUserByEmail(email);
	}

	@Override
    public List<CalendarUser> getUsersByEmail(String partialEmail) {
		// TODO Assignment 3
		return this.userDao.findUsersByEmail(partialEmail);
	}

	@Override
    public int createUser(CalendarUser user) {
		// TODO Assignment 3
		return this.userDao.createUser(user);
	}
    
	@Override
    public void deleteAllUsers() {
		// TODO Assignment 3
		this.userDao.deleteAll();
	}
	
    
	
    /* Event */
	@Override
    public Event getEvent(int eventId) {
		// TODO Assignment 3		
		return this.eventDao.findEvent(eventId);
	}

	@Override
    public List<Event> getEventForOwner(int ownerUserId) {
		// TODO Assignment 3		
		return this.eventDao.findForOwner(ownerUserId);
	}

	@Override
    public List<Event> getAllEvents() {
		// TODO Assignment 3
		return this.eventDao.findAllEvents();
	}
	@Override
    public int createEvent(Event event) {
		// TODO Assignment 3
		
		if (event.getEventLevel() == null) {
			event.setEventLevel(EventLevel.NORMAL);
		}		
		
		return this.eventDao.createEvent(event);
	}
    
	@Override
    public void deleteAllEvents() {
		this.eventDao.deleteAll();
	}

	
	
    /* EventAttendee */
	@Override
	public List<EventAttendee> getEventAttendeeByEventId(int eventId) {
		// TODO Assignment 3
		return this.eventAttendeeDao.findEventAttendeeByEventId(eventId);
	}

	@Override
	public List<EventAttendee> getEventAttendeeByAttendeeId(int attendeeId) {
		// TODO Assignment 3
		return this.eventAttendeeDao.findEventAttendeeByAttendeeId(attendeeId);
	}

	@Override
	public int createEventAttendee(EventAttendee eventAttendee) {
		// TODO Assignment 3
		
		return this.eventAttendeeDao.createEventAttendee(eventAttendee);
	}

	@Override
	public void deleteEventAttendee(int id) {
		// TODO Assignment 3
		this.eventAttendeeDao.deleteEventAttendee(id);
		
	}

	@Override
	public void deleteAllEventAttendees() {
		// TODO Assignment 3
		this.eventAttendeeDao.deleteAll();		
	}
	
	
	
	/* upgradeEventLevels */
	@Override
	public void upgradeEventLevels() throws Exception{
		// TODO Assignment 3
		// 트랜잭션 관련 코딩 필요함
		
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		List<Event> events = new ArrayList<Event>();		
		events = eventDao.findAllEvents();
		System.out.println();
		System.out.println("비었니?"+events.isEmpty());
		for (Event event: events) {
			System.out.println("각각 값확인"+event.getId());			
		}		
		
		//Status 객체 제대로 적용 되어 있는지 확인
		System.out.println("status: "+status);
		try {			
			
			
			for (Event event: events) {
				if(canUpgradeEventLevel(event)) {
					System.out.println("업그레이드전 Level 확인:"+event.getEventLevel());
					upgradeEventLevel(event);
					System.out.println("업그레이드후 Level 확인:"+event.getEventLevel());
					System.out.println("----------------");
				}
			}
			this.transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
		
	}

	@Override
	public boolean canUpgradeEventLevel(Event event) {
		// TODO Assignment 3
		EventLevel currentLevel = event.getEventLevel();
		switch (currentLevel) {
		case NORMAL: return (event.getNumLikes()>=MIN_NUMLIKES_FOR_HOT);
		case HOT: return false;
		default: throw new IllegalArgumentException("Unkown Level: "+currentLevel);
		}
		
	}
	
	@Override
	public void upgradeEventLevel(Event event) {
		event.upgradeLevel();
		eventDao.udpateEvent(event);
	}


}