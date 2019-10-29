package controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import models.Activity;
import models.Location;
import models.User;
import utils.Serializer;

public class PacemakerAPI
{
	private Map<String, User>     userIndex       = new HashMap<>();
	private Map<String, User>   emailIndex      = new HashMap<>();
	private Map<Long, Activity> activitiesIndex = new HashMap<>();

	private Serializer serializer;

	public PacemakerAPI(Serializer serializer)
	{
		this.serializer = serializer;
	}

	public Collection<User> getUsers ()
	{
		return userIndex.values();
	}

	public Collection<Activity> getActivities ()
	{
		return activitiesIndex.values();
	}

	public Serializer getSerializer() {
		return serializer;
	}
	
	public  void deleteUsers() 
	{
		userIndex.clear();
		emailIndex.clear();
	}

	public User createUser(String firstName, String lastName, String email, String password) 
	{
		var user = new User (firstName, lastName, email, password);
		userIndex.put(user.id, user);
		emailIndex.put(email, user);
		return user;
	}

	public User getUserByEmail(String email) 
	{
		return emailIndex.get(email);
	}

	public User getUser(String id) 
	{
		return userIndex.get(id);
	}

	public void deleteUser(String id) 
	{
		var user = userIndex.remove(id);
		emailIndex.remove(user.email);
	}

	public Activity createActivity(String id, String type, String location, double distance)
	{
		Activity activity = null;
		var user = Optional.fromNullable(userIndex.get(id));
		if (user.isPresent())
		{
			activity = new Activity (type, location, distance);
			user.get().activities.put(activity.id, activity);
			activitiesIndex.put(activity.id, activity);
		}
		return activity;
	}


	public Activity getActivity (Long id)
	{
		return activitiesIndex.get(id);
	}

	public void addLocation (Long id, float latitude, float longitude)
	{
		var activity = Optional.fromNullable(activitiesIndex.get(id));
		if (activity.isPresent())
		{
			activity.get().route.add(new Location(latitude, longitude));
		}
	}

	@SuppressWarnings("unchecked")
	public void load() throws Exception
	{
		serializer.read();
		activitiesIndex = (Map<Long, Activity>) serializer.pop();
		emailIndex      = (Map<String, User>)   serializer.pop();
		userIndex       = (Map<String, User>)     serializer.pop();
	}

	public void store() throws Exception
	{
		serializer.push(userIndex);
		serializer.push(emailIndex);
		serializer.push(activitiesIndex);
		serializer.write(); 
	}

}
