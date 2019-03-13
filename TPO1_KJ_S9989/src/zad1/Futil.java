package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Futil 
{
	public static void processDir(String dirName, String resultFileName)
	{
		Path path = Paths.get(dirName + "/test.txt");
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
		try {
			
			Path p = Paths.get(dirName);
			FileVisitor<Path> fv = new MyFileVisitor();
			Files.walkFileTree(p, fv);
			
			
			
			
			FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
			
			int bytesRead = fileChannel.read(buffer);
			
			while (bytesRead != -1) {

			  buffer.flip();  //make buffer ready for read

			  while(buffer.hasRemaining()){
			      System.out.print((char) buffer.get()); // read 1 byte at a time
			  }

			  buffer.clear(); //make buffer ready for writing
			  bytesRead = fileChannel.read(buffer);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(dirName);
		System.out.println(resultFileName);
	}
}
