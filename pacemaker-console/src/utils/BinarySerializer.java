package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

public class BinarySerializer implements Serializer 
{

  private Stack stack = new Stack();
  private File file;

  public BinarySerializer(File file)
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
    try (ObjectInputStream is = new ObjectInputStream(
    								new BufferedInputStream(
    									new FileInputStream(file)))){
      stack = (Stack) is.readObject();
    }
  }
  
  public void write() throws Exception
  {
    try (ObjectOutputStream os = new ObjectOutputStream(
    								new BufferedOutputStream(
    									new FileOutputStream(file)))) {
      os.writeObject(stack);
    }
  }
  
  @Override
  public String serializerFormat() {
	  return Serializer.super.serializerFormat() + "Binary";
  }

}
