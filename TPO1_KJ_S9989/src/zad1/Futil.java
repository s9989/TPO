package zad1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;

public class Futil 
{
	public static void processDir(String dirName, String resultFileName)
	{
		System.out.println(Charset.defaultCharset().name());

		try {
			String fp = "write.txt";
			FileOutputStream fos = new FileOutputStream(fp);
			FileChannel fileChannel2 = fos.getChannel();
			byte[] inputBytes = "".getBytes();
			ByteBuffer buffer2 = ByteBuffer.wrap(inputBytes);
			int noOfBytesWritten = fileChannel2.write(buffer2);
			fileChannel2.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Żółć");
		Path path = Paths.get(dirName + "/test.txt");
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
		try {

			Path p = Paths.get(dirName);
			FileVisitor<Path> fv = new MyFileVisitor();
			Files.walkFileTree(p, fv);
		} catch (NoSuchFileException e) {
			System.out.print("Brak katalogu: ");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

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

		} catch (NoSuchFileException e) {
			System.out.print("Brak katalogu: ");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println();

		System.out.println(dirName);
		System.out.println(resultFileName);
	}
}
