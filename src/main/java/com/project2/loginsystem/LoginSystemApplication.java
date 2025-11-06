package com.project2.loginsystem;

import com.project2.loginsystem.entity.Roles;
import com.project2.loginsystem.repository.RolesRespository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LoginSystemApplication {

	public static void main(String[] args) {

        SpringApplication.run(LoginSystemApplication.class, args);
	}

    @Bean
    CommandLineRunner run(RolesRespository rolesRespository) {
        return args -> {
            // Create USER role if it doesn't exist
            if (rolesRespository.findByRoleNames("ROLE_USER") == null) {
                Roles userRole = new Roles();
                userRole.setRoleNames("ROLE_USER");
                rolesRespository.save(userRole);
            }

            // Create ADMIN role if it doesn't exist
            if (rolesRespository.findByRoleNames("ROLE_ADMIN") == null) {
                Roles adminRole = new Roles();
                adminRole.setRoleNames("ROLE_ADMIN");
                rolesRespository.save(adminRole);
            }
        };
    }

    @Bean(name = "emailTaskExecutor")
    public TaskExecutor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }

}
