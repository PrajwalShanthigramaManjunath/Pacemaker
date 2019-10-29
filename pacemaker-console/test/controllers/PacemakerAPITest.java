package controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controllers.PacemakerAPI;
import models.Activity;
import models.Location;
import models.User;

import static models.Fixtures.users;
import static models.Fixtures.activities;
import static models.Fixtures.locations;

public class PacemakerAPITest
{
	private PacemakerAPI pacemaker;

	@BeforeEach
	public void setup()
	{
		pacemaker = new PacemakerAPI(null);
		for (User user : users)
		{
			pacemaker.createUser(user.firstName, user.lastName, user.email, user.password);
		}
	}

	@AfterEach
	public void tearDown()
	{
		pacemaker = null;
	}

	@Test
	public void testUser()
	{
		assertEquals (users.length, pacemaker.getUsers().size());
		pacemaker.createUser("homer", "simpson", "homer@simpson.com", "secret");
		assertEquals (users.length+1, pacemaker.getUsers().size());
		assertEquals (users[0], pacemaker.getUserByEmail(users[0].email));
	}  

	@Test
	public void testUsers()
	{
		assertEquals (users.length, pacemaker.getUsers().size());
		for (User user: users)
		{
			User eachUser = pacemaker.getUserByEmail(user.email);
			assertEquals (user, eachUser);
			assertNotSame(user, eachUser);
		}
	}

	@Test
	public void testEquals()
	{
		User homer = new User ("homer", "simpson", "homer@simpson.com",  "secret");
		User homer2 = new User ("homer", "simpson", "homer@simpson.com",  "secret"); 
		User bart   = new User ("bart", "simpson", "bartr@simpson.com",  "secret"); 

		assertEquals(homer, homer);
		assertEquals(homer, homer2);
		assertNotEquals(homer, bart);    
	}    

	@Test
	public void testDeleteUsers()
	{
		assertEquals (users.length, pacemaker.getUsers().size());
		User marge = pacemaker.getUserByEmail("marge@simpson.com");
		pacemaker.deleteUser(marge.id);
		assertEquals (users.length-1, pacemaker.getUsers().size());    
	}

	@Test
	public void testAddActivity()
	{
		User marge = pacemaker.getUserByEmail("marge@simpson.com");
		Activity activity = pacemaker.createActivity(marge.id, activities[0].type, activities[0].location, activities[0].distance);
		Activity returnedActivity = pacemaker.getActivity(activity.id);
		assertEquals(activities[0],  returnedActivity);
		assertNotSame(activities[0], returnedActivity);
	} 

	@Test
	public void testAddActivityWithSingleLocation()
	{
		User marge = pacemaker.getUserByEmail("marge@simpson.com");
		Long activityId = pacemaker.createActivity(marge.id, activities[0].type, activities[0].location, activities[0].distance).id;

		pacemaker.addLocation(activityId, locations[0].latitude, locations[0].longitude);

		Activity activity = pacemaker.getActivity(activityId);
		assertEquals (1, activity.route.size());
		assertEquals(0.0001, locations[0].latitude,  activity.route.get(0).latitude);
		assertEquals(0.0001, locations[0].longitude, activity.route.get(0).longitude);   
	} 

	@Test
	public void testAddActivityWithMultipleLocation()
	{
		User marge = pacemaker.getUserByEmail("marge@simpson.com");
		Long activityId = pacemaker.createActivity(marge.id, activities[0].type, activities[0].location, activities[0].distance).id;

		for (Location location : locations)
		{
			pacemaker.addLocation(activityId, location.latitude, location.longitude);      
		}

		Activity activity = pacemaker.getActivity(activityId);
		assertEquals (locations.length, activity.route.size());
		int i = 0;
		for (Location location : activity.route)
		{
			assertEquals(location, locations[i]);
			i++;
		}
	} 


}  