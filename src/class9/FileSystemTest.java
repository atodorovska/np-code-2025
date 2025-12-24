package class9;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface IFile { //Component
    String getFileName();
    long getFileSize();
    String getFileInfo(int level);
}

class FileNameExistsException extends Exception {
    FileNameExistsException(String fileName, String folderName){
        super(String.format("There is already a file named %s in the folder %s", fileName, folderName));
    }

}

class IndentPrinter {
    public static String get (int level){
        return "    ".repeat(level);
    }
}

class File implements IFile {  //Leaf

    String fileName;
    long fileSize;

    public File(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String getFileInfo(int level) {
        return String.format("%sFile name %10s File size: %10d\n", IndentPrinter.get(level), fileName, fileSize);
    }

}

class Folder implements IFile { //Composite

    String folderName;
    List<IFile> children;
    public Folder(String folderName) {
        this.folderName = folderName;
        children = new ArrayList<IFile>();
    }

    @Override
    public String getFileName() {
        return folderName;
    }

    @Override
    public long getFileSize() {
        return children.stream()
                .mapToLong(IFile::getFileSize)
                .sum();
    }

    @Override
    public String getFileInfo(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name %10s Folder size: %10d\n", IndentPrinter.get(level), folderName, getFileSize()));
        for (IFile child : children) {
            sb.append(child.getFileInfo(level+1));
        }
        return sb.toString();

    }

    public void addFile(IFile file) throws FileNameExistsException {
        for (IFile child : children) {
            if (child.getFileName().equals(file.getFileName())) {
                throw new FileNameExistsException(file.getFileName(), this.folderName);
            }
        }
        children.add(file);
    }

}

class FileSystem {
    Folder root = new Folder("root");


    public void addFile(IFile file) throws FileNameExistsException {
        root.addFile(file);
    }

    @Override
    public String toString() {
        return root.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
//        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
//        System.out.println(fileSystem.findLargestFile());




    }
}