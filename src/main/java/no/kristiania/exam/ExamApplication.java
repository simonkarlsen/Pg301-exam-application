package no.kristiania.exam;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ExamApplication {



	public static void main(String[] args) {

		SpringApplication.run(ExamApplication.class, args);
	}

	@Bean
	MeterRegistryCustomizer<MeterRegistry> commonTags() {
		return r-> r.config().commonTags("exam", "examapp", "region", "europe-north-1");
	}



}
