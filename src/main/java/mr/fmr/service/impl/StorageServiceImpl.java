package mr.fmr.service.impl;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import mr.fmr.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
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

    private static Storage storage = null;
    private final String BUCKET_NAME = "frm_api";

    static {
        storage = StorageOptions.getDefaultInstance().getService();
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
        Date d = new Date();
        final String fileName = file.getOriginalFilename() + d.getTime();

        try {
            InputStream in = file.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] readBuf = new byte[4096];
            while(in.available() > 0) {
                int bytesRead = in.read(readBuf);
                os.write(readBuf, 0, bytesRead);
            }
            BlobInfo info =
                    storage.create(
                            BlobInfo
                            .newBuilder(BUCKET_NAME , fileName)
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build());

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
}
