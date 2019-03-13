package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor extends SimpleFileVisitor<Path> 
{
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
//		System.out.println(arg0);
		
		FileChannel fileChannel = FileChannel.open(arg0, StandardOpenOption.READ);
		
		int bytesRead = fileChannel.read(buffer);
		
		String s = new String();
		
		while (bytesRead != -1) {

		  buffer.flip();  //make buffer ready for read

		  while(buffer.hasRemaining()){
			  s = s + (char) buffer.get();
		      //System.out.print((char) buffer.get()); // read 1 byte at a time
		  }

		  buffer.clear(); //make buffer ready for writing
		  bytesRead = fileChannel.read(buffer);
		}
		
		System.out.println(s);
		String decoded = new String(s.getBytes("CP1250"), "UTF-8");
		System.out.println(decoded);
		System.out.println();
		
		return super.visitFile(arg0, arg1);
	}
}
