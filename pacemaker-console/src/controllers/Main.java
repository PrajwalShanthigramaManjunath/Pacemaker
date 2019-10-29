package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.impl.CollectionASCIITableAware;
import com.google.common.base.Optional;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;
import models.User;
import utils.XMLSerializer;

public class Main {

	PacemakerAPI paceApi;

	public Main() throws Exception
	{
		//XML Serializer
		var datastore = new File("datastore.xml");
		var serializer = new XMLSerializer(datastore);

		//JSON Serializer
		//File  datastore = new File("datastore.json");
		//Serializer serializer = new JSONSerializer(datastore);

		//Binary Serializer
		//File  datastore = new File("datastore.txt");
		//Serializer serializer = new BinarySerializer(datastore);

		paceApi = new PacemakerAPI(serializer);
		if (datastore.isFile())
		{
			paceApi.load();
		}
	}

	@Command(description = "Create a new User")
	public void createUser(@Param(name = "first name") String firstName, @Param(name = "last name") String lastName,
			@Param(name = "email") String email, @Param(name = "password") String password) {
		paceApi.createUser(firstName, lastName, email, password);
	}

	@Command(description = "Get a Users details")
	public void getUser(@Param(name = "email") String email) {
		var user = paceApi.getUserByEmail(email);
		System.out.println(user);
	}

	@Command(description="Get all users details")
	public void getUsers ()
	{
		var userList = new ArrayList<User> (paceApi.getUsers());
		var asciiTableAware = new CollectionASCIITableAware<User>(userList, "firstname", "lastname", "email"); 
		ASCIITable.getInstance().printTable(asciiTableAware);
	}

	@Command(description = "Delete a User")
	public void deleteUser(@Param(name = "email") String email) {
		var user = Optional.fromNullable(paceApi.getUserByEmail(email));
		if (user.isPresent()) {
			paceApi.deleteUser(user.get().id);
		}
	}

	@Command(description = "Add an activity")
	public void addActivity(@Param(name = "user-id") String id, @Param(name = "type") String type,
			@Param(name = "location") String location, @Param(name = "distance") double distance) {
		var user = Optional.fromNullable(paceApi.getUser(id));
		if (user.isPresent()) {
			paceApi.createActivity(id, type, location, distance);
		}
	}

	@Command(description = "Add Location to an activity")
	public void addLocation(@Param(name = "activity-id") Long id, @Param(name = "latitude") float latitude,
			@Param(name = "longitude") float longitude) {
		var activity = Optional.fromNullable(paceApi.getActivity(id));
		if (activity.isPresent()) {
			paceApi.addLocation(activity.get().id, latitude, longitude);
		}
	}

	@Command(description = "Current Serialization Format")
	public void currentSerialisationFormat() {
		System.out.println(paceApi.getSerializer().serializerFormat());
	}

	@Command(description = "Report active users")
	public void reportActiveUsers() {

		var asciiTableAware = 
				//data for the table
				new CollectionASCIITableAware<User>(
						paceApi.getUsers()
						.stream()
						.filter(p -> !p.getActivities().isEmpty())
						.collect(Collectors.toList()), 
				//properties to read (using getter methods)
				List.of("firstname", "lastname", "email", "activitiessize"), 
				//custom headers for the table
				List.of("First Name", "Last Name", "Email", "Number of Activities")); 

		ASCIITable.getInstance().printTable(asciiTableAware);
	}

	public static void main(String[] args) throws Exception {

		var main = new Main();

		var shell = ShellFactory.createConsoleShell("pm", "Welcome to pacemaker-console - ?help for instructions", main);
		shell.commandLoop();

		main.paceApi.store();
	}
}
