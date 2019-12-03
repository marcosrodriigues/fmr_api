package mr.fmr.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import mr.fmr.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;


@Service
public class StorageServiceImpl implements StorageService {

    //private Path fileStorageLocation;

    private static Storage storage;
    private final String BUCKET_NAME = "fmr-api-14234341.appspot.com";

    static {
        try {
//            InputStream in = getClass.getResourceAsStream("/keys-gcp.json");

  //          GoogleCredentials cred = GoogleCredentials.fromStream(in);

    //        storage = StorageOptions.newBuilder().setCredentials(cred).build().getService();
            storage = StorageOptions.getDefaultInstance().getService();
            System.out.println(storage.getOptions().getCredentials());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Autowired
//    public StorageServiceImpl(FileStorageProperties properties) {
//
//        this.fileStorageLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception e) {
//            throw new FileStorageException("Não foi possível criar o caminho");
//        }
//    }


    @Override
    public String store(MultipartFile file) {
        final String fileName = createFileName(file);
        try {
            InputStream in = file.getInputStream();
            BlobInfo info =
                    storage.create(
                            BlobInfo
                            .newBuilder(BUCKET_NAME , fileName)
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build(), in);

            return info.getMediaLink();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

//        try {
//            if(filename.contains("..")) {
//                throw new FileStorageException("Filename contém caminho inválido");
//            }
//
//            Path targetLocation = this.fileStorageLocation.resolve(filename);
//
//            int count = 1;
//            while(targetLocation.toFile().exists()) {
//                String fullPathWithoutFilename = "/";
//                for (String tinyPath : targetLocation.toFile().getAbsolutePath().split("/")) {
//                    if (!tinyPath.isEmpty())
//                        fullPathWithoutFilename += tinyPath + "/";
//
//                    if (tinyPath.equalsIgnoreCase("upload")) break;
//                }
//
//                String fullname = targetLocation.toFile().getName();
//                String name = fullname.split("\\.")[0];
//                String ext = fullname.split("\\.")[1];
//                filename = name + "_" + count + "." + ext;
//                File novoFile = new File(fullPathWithoutFilename + filename);
//
//                targetLocation.toFile().renameTo(novoFile);
//            }
//
//            Files.copy(file.getInputStream(), targetLocation);
//
//            return filename;
//        } catch (IOException e) {
//            throw new FileStorageException("Não foi possível armazenar o arquivo" + filename + "!Tente novamente", e);
//        }
    }


    @Override
    public Resource loadAsResource(String filename) {
//        try {
//            Path file = this.fileStorageLocation.resolve(filename).normalize();
//
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists()) {
//                return resource;
//            }
//
//            throw new MyFileNotFoundException("Arquivo " + filename + " não encontrado!");
//        } catch (MalformedURLException e) {
//            throw new MyFileNotFoundException("Arquivo " + filename + " não encontrado!", e);
//        }
        return null;
    }

    private String createFileName(MultipartFile file) {
        String[] parts = file.getOriginalFilename().split("\\.");
        String ext = parts[parts.length - 1];
        Date d = new Date();
        String name = parts[0];
        for (int i = 1; i < parts.length; i++) {
            if ((i+1) == parts.length) name = name.concat("_" + d.getTime());
            else name = name.concat("_" + parts[i]);
        }
        name = name.concat("." + ext);
        return name;
    }
}

