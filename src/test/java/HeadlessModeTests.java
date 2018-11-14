import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tonyhsu17.RunHeadlessMode;

public class HeadlessModeTests {
    private File testFolder;
    private String des;

    @BeforeClass
    public void beforeClass() throws IOException {
        testFolder = new File("testFolder");
        testFolder.mkdir();

        des = testFolder + "/dest";
        new File(des).mkdir();
    }

    @AfterClass
    public void afterClass() throws IOException {
        // delete testFolder and files
        testFolder = new File("testFolder");
        if(testFolder.exists()) {
            Files.walkFileTree(testFolder.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file); // this will work because it's always a File
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); //this will work because Files in the directory are already deleted
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
    
    @Test
    public void testFilesSaved() throws IOException {
        String url = "http://www.shanaproject.com/feeds/user/48337/?count=50";
        RunHeadlessMode headless = new RunHeadlessMode(url, des);
        headless.run();
        File destFolder = new File(des);
        Assert.assertEquals(destFolder.list().length, 39, "Files downloaded mismatch +1 for log file");
    }
}
