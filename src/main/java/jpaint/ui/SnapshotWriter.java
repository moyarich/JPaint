package jpaint.ui;

import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.*;

/** Writes diagnostic renders without introducing AWT or Swing dependencies. */
public final class SnapshotWriter {
    private SnapshotWriter(){}
    public static void write(Image image,String name) throws IOException {
        Path path=Path.of(name);Files.createDirectories(path.getParent());
        try(OutputStream out=new BufferedOutputStream(Files.newOutputStream(path))){
            int width=(int)image.getWidth(),height=(int)image.getHeight();
            out.write(("P6\n"+width+" "+height+"\n255\n").getBytes(java.nio.charset.StandardCharsets.US_ASCII));
            var pixels=image.getPixelReader();
            for(int y=0;y<height;y++)for(int x=0;x<width;x++){int pixel=pixels.getArgb(x,y);out.write((pixel>>16)&255);out.write((pixel>>8)&255);out.write(pixel&255);}
        }
    }
}
