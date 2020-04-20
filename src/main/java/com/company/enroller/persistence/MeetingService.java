package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Meeting> getAll() {

		Query query = session.createQuery("from Meeting m order by m.title");

		return query.list();
	}

	public Meeting findById(long meetingId) {
		return (Meeting) session.get(Meeting.class, meetingId);
	}

	public Meeting addMeeting(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.save(meeting);
		transaction.commit();
		return meeting;
	}

	public Meeting deleteMeeting(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.delete(meeting);
		transaction.commit();
		return meeting;
	}

	public Meeting updateMeeting(Meeting meeting) {
		Transaction transaction = this.session.beginTransaction();
		session.update(meeting);
		transaction.commit();
		return meeting;
	}

	public Collection<Meeting> findByNameOrDescriptionOrParticipantsLogin(String filter, String sort, String login) {

		if (filter == null) {
			filter = "";
		}

		if (sort == null || (!sort.equals("title") && !sort.equals("date") && !sort.equals("description"))) {
			sort = "id";
		}

		Criteria criteria = session.createCriteria(Meeting.class);
		criteria.addOrder(Order.asc(sort));
		criteria.add(Restrictions.or(Restrictions.like("description", filter , MatchMode.ANYWHERE),
				Restrictions.like("title", filter, MatchMode.ANYWHERE)));

		if(login != null)
		{
			 criteria.createAlias("participants", "p", CriteriaSpecification.LEFT_JOIN);
			 criteria.add(Restrictions.eq("p.login", login));
		}
		
		

		return criteria.list();

	}

}
