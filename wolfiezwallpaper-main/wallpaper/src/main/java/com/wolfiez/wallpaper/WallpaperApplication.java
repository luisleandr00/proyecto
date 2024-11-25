package com.wolfiez.wallpaper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.wolfiez.wallpaper")
@EntityScan("com.wolfiez.wallpaper.entity")
@EnableJpaRepositories("com.wolfiez.wallpaper.repository")
public class WallpaperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WallpaperApplication.class, args);
	}

}
