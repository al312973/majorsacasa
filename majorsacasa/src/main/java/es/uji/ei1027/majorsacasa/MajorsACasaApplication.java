package es.uji.ei1027.majorsacasa;

import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MajorsACasaApplication{

    private static final Logger log = Logger.getLogger(MajorsACasaApplication .class.getName());

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(MajorsACasaApplication .class).run(args);
    }
	 // Configura l'accés a la base de dades (DataSource)
	 // a partir de les propietats a src/main/resources/applications.properties
	 // que comencen pel prefix spring.datasource
	 @Bean
	 @ConfigurationProperties(prefix = "spring.datasource")
	 public DataSource dataSource() {
	   return DataSourceBuilder.create().build();
	 }

}