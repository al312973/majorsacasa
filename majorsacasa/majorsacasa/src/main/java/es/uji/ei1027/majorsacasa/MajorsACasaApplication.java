package es.uji.ei1027.majorsacasa;

import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MajorsACasaApplication{

    private static final Logger log = Logger.getLogger(MajorsACasaApplication .class.getName());

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(MajorsACasaApplication .class).run(args);
    }
    
}