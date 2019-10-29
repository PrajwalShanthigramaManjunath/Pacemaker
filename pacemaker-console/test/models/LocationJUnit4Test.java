package models;

import static org.junit.Assert.*;
import org.junit.Test;
import static models.Fixtures.locations;

public class LocationJUnit4Test {

	@Test
	public void testCreate()
	{
		assertEquals (23.3, locations[0].latitude, 0.01);
		assertEquals (33.3, locations[0].longitude, 0.01);
	}

	@Test
	public void testIds()
	{
		assertNotEquals(locations[0].id, locations[1].id);
	}

	@Test
	public void testToString()
	{
		assertEquals ("Location{" + locations[0].id + ", 23.3, 33.3}", locations[0].toString());
	}
}
