package zad1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(30);

//		System.out.println(arg0);

        FileChannel fileChannel = FileChannel.open(arg0, StandardOpenOption.READ);

        int bytesRead = fileChannel.read(buffer);

        String s = "";

        StringBuilder sb = new StringBuilder();

        while (bytesRead != -1) {



            buffer.flip();  //make buffer ready for read

            Charset cs = Charset.forName("windows-1250");
            System.out.print("OK: ");
//            System.out.print(cs.decode(buffer));
            System.out.println("|");

            sb.append(cs.decode(buffer));

            while (buffer.hasRemaining()) {
                s = s + (char) buffer.get();
                //System.out.print((char) buffer.get()); // read 1 byte at a time
            }


            buffer.clear(); //make buffer ready for writing
            bytesRead = fileChannel.read(buffer);
        }

        fileChannel.close();

        System.out.print("END: ");
        System.out.print(sb.toString());
        System.out.println("|");
        System.out.println(s);
        String decoded = new String(s.getBytes("windows-1250"), StandardCharsets.UTF_8);


        System.out.println(decoded);
        System.out.println();
        System.out.println();

        String fp = "write.txt";
        FileOutputStream fos = new FileOutputStream(fp, true);
        FileChannel fileChannel2 = fos.getChannel();

        fileChannel2.position(fileChannel2.size());

        byte [] inputBytes = sb.toString().getBytes();
        ByteBuffer buffer2 = ByteBuffer.wrap(inputBytes);
        int noOfBytesWritten = fileChannel2.write(buffer2);
        fileChannel2.close();
        fos.close();


        return super.visitFile(arg0, arg1);
    }
}
