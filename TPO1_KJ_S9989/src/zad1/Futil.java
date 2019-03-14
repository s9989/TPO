package zad1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

class Futil {

    static void processDir(String dirName, String resultFileName) {

        // create or clear result file
        try {
            FileChannel fileChannel2 = new FileOutputStream(resultFileName).getChannel();
            ByteBuffer buffer2 = ByteBuffer.wrap("".getBytes());

            fileChannel2.write(buffer2);
            fileChannel2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // walk through directory tree
        try {
            Path p = Paths.get(dirName);
            FileVisitor<Path> fv = new MyFileVisitor(resultFileName);
            Files.walkFileTree(p, fv);

        } catch (NoSuchFileException e) {
            System.out.print("Brak katalogu: ");
            System.out.println(e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
