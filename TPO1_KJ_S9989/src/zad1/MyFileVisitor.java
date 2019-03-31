package zad1;

import java.io. ;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

class MyFileVisitor extends SimpleFileVisitor<Path> {

    private String outputFile;

    MyFileVisitor(String resultFileName) {
        this.outputFile = resultFileName;
    }

    @Override
    public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {

        StringBuilder sb = new StringBuilder();

        FileChannel inputChannel = FileChannel.open(arg0, StandardOpenOption.READ);

        ByteBuffer inputBuffer = ByteBuffer.allocate(10);
        int bytesRead = inputChannel.read(inputBuffer);

        while (bytesRead != -1) {

            inputBuffer.flip();  //make buffer ready for read

            Charset cs = Charset.forName("windows-1250");
            sb.append(cs.decode(inputBuffer));

            inputBuffer.clear(); //make buffer ready for writing
            bytesRead = inputChannel.read(inputBuffer);
        }

        inputChannel.close();

        FileChannel outputChannel  = new FileOutputStream(this.outputFile, true).getChannel();

        outputChannel.position(outputChannel.size());

        ByteBuffer outputBuffer = ByteBuffer.wrap(sb.toString().getBytes());

        outputChannel.write(outputBuffer);
        outputChannel.close();

        return super.visitFile(arg0, arg1);
    }
}
