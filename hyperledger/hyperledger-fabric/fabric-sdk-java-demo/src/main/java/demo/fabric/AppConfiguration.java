package demo.fabric;

import com.google.common.collect.Sets;
import demo.fabric.entity.FabricUserEntity;
import demo.fabric.repository.FabricUserRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class AppConfiguration {

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            private FabricUserRepository fabricUserRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Set<String> roles = new HashSet<>();
                roles.add("test1");
                roles.add("test2");

                FabricUserEntity fabricUserEntity = FabricUserEntity.builder()
                    .name("admin@RootCA")
                    .roles(roles)
                    .account("account")
                    .affiliation("org1")
                    .enrollmentSecret("rootcaadmin")
                    .build();

                FabricUserEntity save = fabricUserRepository.save(fabricUserEntity);
                System.out.println("## Success to save fabric user : " + save.getId());

                FabricUserEntity found = fabricUserRepository.findById(save.getId()).get();
                System.out.println("Found :: " + found.toString());
            }
        };
    }
}
