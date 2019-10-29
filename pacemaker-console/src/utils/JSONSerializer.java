package utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

public class JSONSerializer implements Serializer
{

	private Stack stack = new Stack();
	private File file;

	public JSONSerializer(File file)
	{
		this.file = file;
	}

	public void push(Object o)
	{
		stack.push(o);
	}

	public Object pop()
	{
		return stack.pop(); 
	}

	@SuppressWarnings("unchecked")
	public void read() throws Exception
	{
		ObjectInputStream is = null;

		try
		{

			//set up an array of classes that can be loaded.
			var classes = new Class[] { models.User.class, models.Activity.class, models.Location.class };
			var xstream = new XStream(new JettisonMappedXmlDriver());

			//set up permissions for loading via xstream
			XStream.setupDefaultSecurity(xstream);
			xstream.allowTypes(classes);	        

			is = xstream.createObjectInputStream(new FileReader(file));
			stack = (Stack) is.readObject();
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
		}
	}

	public void write() throws Exception
	{
		ObjectOutputStream os = null;

		try
		{
			var xstream = new XStream(new JettisonMappedXmlDriver());
			os = xstream.createObjectOutputStream(new FileWriter(file));
			os.writeObject(stack);
		}
		finally
		{
			if (os != null)
			{
				os.close();
			}
		}
	}

	  @Override
	  public String serializerFormat() {
		  return Serializer.super.serializerFormat() + "JSON";
	  }

}
