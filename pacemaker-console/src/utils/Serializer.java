package utils;

public interface Serializer
{
  void push(Object o);
  Object pop();
  void write() throws Exception;
  void read() throws Exception;
  
  default String serializerFormat() {
	  return "Serializer Format is...";
  }
  
}