package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	private MeetingService meetingService;
	private ParticipantService participantService;

	@Autowired
	public MeetingRestController(MeetingService meetingService, ParticipantService participantService) {
		this.meetingService = meetingService;
		this.participantService = participantService;
	}

//	@RequestMapping(value = "", method = RequestMethod.GET )
//	public ResponseEntity<?> getAllMeetings() {
//		Collection<Meeting> meetings = meetingService.getAll();
//		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
//	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long meetingId) {
		Meeting meeting = meetingService.findById(meetingId);

		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		meeting = meetingService.addMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/{login}", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipant(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
		Meeting meeting = meetingService.findById(meetingId);
		Participant participant = participantService.findByLogin(login);

		if (meeting == null || participant == null) {
			return new ResponseEntity("Unable to update. Participant with login " + login + " or meeting with id "
					+ +meetingId + " does not exist", HttpStatus.NOT_FOUND);
		}

		meeting.addParticipant(participant);
		meeting = meetingService.updateMeeting(meeting);
		return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants(@PathVariable("id") long meetingId) {
		Meeting meeting = meetingService.findById(meetingId);
		if (meeting == null) {
			return new ResponseEntity("Meeting with id: " + +meetingId + " does not exist", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delMeeting(@PathVariable("id") long meetingId) {
		Meeting meeting = meetingService.findById(meetingId);
		if (meeting == null) {
			return new ResponseEntity("Meeting with id: " + meetingId + " does not exist", HttpStatus.NOT_FOUND);
		}
		meeting = meetingService.deleteMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long meetingId, @RequestBody Meeting meeting) {
		Meeting foundMeeting = meetingService.findById(meetingId);
		if (foundMeeting == null) {
			return new ResponseEntity("Meeting with id: " + meetingId + " does not exist", HttpStatus.NOT_FOUND);
		}

		foundMeeting.setDate(meeting.getDate());
		foundMeeting.setDescription(meeting.getDescription());
		foundMeeting.setTitle(meeting.getTitle());
		meeting = meetingService.updateMeeting(foundMeeting);

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delParticipant(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
		Meeting meeting = meetingService.findById(meetingId);
		Participant participant = participantService.findByLogin(login);

		if (meeting == null || participant == null) {
			return new ResponseEntity("Unable to update. Participant with login " + login + " or meeting with id "
					+ +meetingId + " does not exist", HttpStatus.NOT_FOUND);
		}

		meeting.removeParticipant(participant);
		meeting = meetingService.updateMeeting(meeting);
		return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> showMeetings(
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "login", required = false) String login) {

		Collection<Meeting> meetings = meetingService.findByNameOrDescriptionOrParticipantsLogin(filter, sort, login);
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

}